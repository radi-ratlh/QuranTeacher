/*
 *               In the name of Allah
 * This file is part of The "Quran Teacher or Learn Arabic" Project. Use is subject to
 * license terms.
 *
 * @author:         Fazle Rabbi Rahat
 * 
 */
package QuranTeacher;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import QuranTeacher.Preferences.AnimationPreferences;
import QuranTeacher.Preferences.AudioPreferences;
import QuranTeacher.Preferences.deltaPixelProperty;
import QuranTeacher.RenderAnimation.Animation;
import QuranTeacher.RenderAnimation.FocusCheckRunnable;
import QuranTeacher.RenderAudio.Reciter;
import QuranTeacher.RenderImages.ImageLoader;
import QuranTeacher.Texts.QuranText;
import QuranTeacher.WordInformation.WordInfoLoader;
import QuranTeacher.WordInformation.WordInformation;


public class AnimationPanel extends Animation {

	/**
	 * For handling user input
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	private QuranText quranText;
	private Selected runningAyah=new Selected(0, 0);
	private String bismillah="\u0628\u0650\u0633\u0652\u0645\u0650 "
			+ "\u0627\u0644\u0644\u0651\u064e\u0647\u0650 \u0627\u0644"
			+ "\u0631\u0651\u064e\u062d\u0652\u0645\u064e\u0670\u0646"
			+ "\u0650 \u0627\u0644\u0631\u0651\u064e\u062d\u0650\u064a\u0645\u0650";
	//private String displayText;
	
	private Reciter reciter;
	private boolean paused=false;
	private UserInputListener userInputListener;
	private Thread focusCheckingThread;
	
	private AudioPreferences audioPrefs;
	private boolean isAnimAudioOn;//unimportant, not actually used except in one case
	private int audioSIndex;
	
	public AnimationPanel() {
		//System.out.println("DisplayPanel() called");
		displayText="Failed to load Quran Text";
		quranText=null;
		
		Runnable loader=new Runnable() {
			
			@Override
			public void run() {
				quranText=new QuranText(QuranText.QuranTextFileName);
				new WordInfoLoader().load();//initialize word infos
				//java memory proble, so excluded
				//new ImageLoader().load();//initialize images
			}
		};
		
		Thread t=new Thread(loader);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		setFocusable(true);
		focusCheckingThread=new Thread();
		
		audioPrefs=PreferencesDialog.getAudioPref();
		updateAudioPref();
		
		Reciter.createDirectory();
		//for getting info box
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getY()<currentDisplayPoint.y+height)
				{
					Point focused=e.getPoint();
					focused.y+=scrollY;
					
					if( !focusCheckingThread.isAlive())
					{
						focusCheckingThread=new Thread(new FocusCheckRunnable(focused));
						focusCheckingThread.start();
						
						//System.out.println(Thread.activeCount());
					}
				}
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(e.getX()>=startPoint.x-20)
				{
					scrollY=(e.getY()*currentDisplayPoint.y)/getBounds().height;
				}
			}
		});
		
		//for scrolling
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				//System.out.println(e.getWheelRotation());
				int delta=e.getWheelRotation();
				
				if((delta <0 && (scrollY+delta*scrollDelta)>=0) || 
						(delta>0 && (scrollY+delta*scrollDelta)<currentDisplayPoint.y))
					scrollY+=delta*scrollDelta;
				
				//System.out.println(scrollY);
				
			}
		});
		
		//for requesting focus
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				animationRunning=paused;
				paused=!paused;
				userInputListener.pauseStateChanged(paused);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
				
				//System.out.println("Entered");
				//setCursor(new Cursor(Cursor.HAND_CURSOR));
				//System.out.println(getCursor().getName());
			}
		});
		
		//keyBoard input
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println(e.getKeyCode());
				int keyCode=e.getKeyCode();
				//scroll up
				if(keyCode==KeyEvent.VK_UP && (scrollY-scrollDelta)>=0)
				{
					scrollY-=scrollDelta;
				}
				//scroll Down
				else if(keyCode==KeyEvent.VK_DOWN && (scrollY+scrollDelta)<currentDisplayPoint.y)
				{
					scrollY+=scrollDelta;
				}
				//speed up
				else if(keyCode==KeyEvent.VK_RIGHT)//-->
				{
					setDisplayAction("speedDown");
					userInputListener.speedChanged(false);
				}
				//speed down
				else if(keyCode==KeyEvent.VK_LEFT)//<--
				{
					setDisplayAction("speedUp");
					userInputListener.speedChanged(true);
				}
				
				else if(keyCode==KeyEvent.VK_SPACE)
				{
					Selected nextAyah=getNextAyah();
					
					if(nextAyah!=null)
					{
						setAyah(nextAyah);
						SelectionPanel.setSelectionIndex(nextAyah);
					}
				}
			}
		});
		
	}
	
	@Override
	protected void goToNextStep()
	{
		if(paused)
			return;
		
		if(AnimationPreferences.continuous==false)
			return;
		
		if(reciter!=null && Reciter.isAlive())
			return;
		
		Selected nextAyah=getNextAyah();
		
		if(nextAyah!=null)
		{
			setAyah(nextAyah);
			SelectionPanel.setSelectionIndex(nextAyah);
		}
		
	}
	
	private Selected getNextAyah()
	{
		int i=runningAyah.suraIndex;
		int j=runningAyah.ayahIndex;
		
		if(j==SuraInformation.totalAyas[i]-1)
		{
			if(i!=113)
			{
				return (new Selected(i+1,0));
			}
			
			else return null;
		}
		else
		{
			return (new Selected(i,j+1));
		}

	}
	
	public void setDisplayAction(String action)//toolbar buttons
	{
		if(action.equals("pause"))
		{
			animationRunning=false;
			paused=true;
		}
		else if(action.equals("resume"))
		{
			animationRunning=true;
			paused=false;
		}
		
		else if(action.equals("restart"))
		{
			resetDisplay();
			paused=false;
			if(audioPrefs.isAudioON())
				new Reciter(runningAyah);
		}
		
		else if(action.equals("speedUp"))
		{
			//decrease deltaPixel
			deltaPixel+=deltaPixelProperty.delta;
			if(deltaPixel>deltaPixelProperty.maxDeltaPixel)
				deltaPixel=deltaPixelProperty.maxDeltaPixel;
			
		}
		else if(action.equals("speedDown"))
		{
			//increase deltaPixel
			deltaPixel-=deltaPixelProperty.delta;
			if(deltaPixel<deltaPixelProperty.minDeltaPixel)
				deltaPixel=deltaPixelProperty.minDeltaPixel;
			
		}
	}
	
	public void setAudioAction(String actionCommand)
	{
		/*if(actionCommand.equals("play"))
		{
			if(reciter!=null && !reciter.isAlive())
				reciter=new Reciter(runningAyah);
		}
		else*/ if(actionCommand.equals("stop"))
		{
			if(reciter!=null && Reciter.isAlive())
				reciter.stop();
		}
		else if(actionCommand.equals("pause"))
		{
			if(reciter!=null && Reciter.isAlive())
				reciter.pause();
		}
		else if(actionCommand.equals("resume"))
		{
			if(reciter!=null && Reciter.isAlive())
				reciter.resume();
		}
	}
	
	public void setAyah(Selected ayah)//go button,next,previous,restart clicked
	{
		if(audioPrefs.isAudioON())
		{
			setAudioAction("stop");
		}
		
		runningAyah=ayah;
		
		displayText=quranText.getQuranText(ayah);
		setInfoOfWords(ayah);
		
		TranslationPanel.setTranslationText(ayah);
		//TafsirPanel.setTafsirText(ayah);
		
		resetDisplay();
		
		if(paused)
		{
			paused=false;
			userInputListener.pauseStateChanged(paused);
		}
		
		if(ayah.ayahIndex==0 && !(ayah.suraIndex==0 || ayah.suraIndex==8))
			//not sura fatiha and sura atTawba
			setFirstSentence(bismillah);
		
		//repaint();
		
		if(audioPrefs.isAudioON())
		{
			reciter=new Reciter(ayah);
		}
	}
	
	//declared in Animation.java
	/*public void setPreferences(Preferences pref) {
		
		this.preferences=pref;
	}*/
	
	public void refresh()
	{
		repaint();
	}
	
	public int getDeltaPixel()
	{
		return deltaPixel;
	}
	
	public boolean isAnimationRunning()
	{
		return animationRunning;
	}
	
	private void setInfoOfWords(Selected ayah)
	{
		//index of first ayah of the sura in all ayah sets
		int indexOfFirstAyah=SuraInformation.totalAyahsUpto(ayah.suraIndex);
		int indexOfSelectedAyah=indexOfFirstAyah+ayah.ayahIndex;
		//address in the info list of that selected ayah
		//index of the first word of this ayah
		int indxOfFirstWord=WordInfoLoader.startIndexOfAyah.get(indexOfSelectedAyah);
		//index of the first word of the next ayah
		int indxOfFWNextA=WordInfoLoader.startIndexOfAyah.get(indexOfSelectedAyah+1);
		
		//now listing all the word informations
		List<WordInformation>wordsOfAyah=new ArrayList<>();
		List<Image>images=new ArrayList<>();
		
		for(int i=indxOfFirstWord;i<indxOfFWNextA;i++)
		{
			wordsOfAyah.add(WordInfoLoader.infoWords.get(i));
			images.add(ImageLoader.getImageFromFile(i));
		}
		
		//saving this to infoWord of animation.java
		infoOfWord=wordsOfAyah;
		wordImages=images;
	}
	
	
	public void setUserInputListener(UserInputListener listener)
	{
		this.userInputListener=listener;
	}
	
	public void startAnimationTimer()
	{
		timer.start();
	}
	
	public AudioPreferences getAudioPref()
	{
		return new AudioPreferences("audio.preferences",isAnimAudioOn,audioSIndex);
	}
	
	public void updateAudioPref()
	{
		//audioPrefs=PreferencesDialog.getAudioPref();
		isAnimAudioOn=audioPrefs.isAudioON();
		audioSIndex=audioPrefs.getAudioSourceIndex();
		
		String url=AudioPreferencesPanel.getAudioSourceLink(audioSIndex);
		if(url!=Reciter.DefaultURL)
		{
			Reciter.setDefaultUrl(url);
			//System.out.println("new reciter url set");
		}
	}
}
