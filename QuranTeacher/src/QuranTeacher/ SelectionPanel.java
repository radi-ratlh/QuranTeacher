/*
 *               In the name of Allah
 * This file is part of The "Quran Teacher or Learn Arabic" Project. Use is subject to
 * license terms.
 *
 * @author:         Fazle Rabbi Rahat
 * 
 */
package QuranTeacher;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.border.TitledBorder;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import java.awt.Insets;

import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.JRadioButton;

import QuranTeacher.Preferences.AnimationPreferences;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class SelectionPanel extends JPanel {

	/**
	 * Contains list of Sura names and their corresponding Ayah no.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	private Integer[][] totalAyahList=new Integer [114][];
	private String[] suraNameList=new String[114];
	private int[] totalAyah=new int[114];
	
	private DefaultComboBoxModel<String> suraNamesBox=new DefaultComboBoxModel<>();
	@SuppressWarnings("unchecked")
	private final ComboBoxModel<Integer>[] models=new ComboBoxModel[114];
	
	private SelectionListener selectionListener;
	private static JComboBox<Integer> ayahBox;
	private static JComboBox<String> suraBox;
	
	private JRadioButton rdbtnSingleAyah;
	private JRadioButton rdbtnContinuous;
	
	public SelectionPanel() {
		setForeground(Color.WHITE);
		setBackground(Color.DARK_GRAY);
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Select", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 255, 0)));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblSura = new JLabel("Sura :");
		lblSura.setForeground(Color.ORANGE);
		lblSura.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_lblSura = new GridBagConstraints();
		gbc_lblSura.insets = new Insets(0, 0, 5, 5);
		gbc_lblSura.anchor = GridBagConstraints.EAST;
		gbc_lblSura.gridx = 0;
		gbc_lblSura.gridy = 0;
		add(lblSura, gbc_lblSura);
		
		//creating suraBox
		suraBox = new JComboBox<String>();
		suraBox.setBackground(Color.LIGHT_GRAY);
		suraBox.setForeground(Color.BLACK);
		suraBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		GridBagConstraints gbc_suraBox = new GridBagConstraints();
		gbc_suraBox.gridwidth = 5;
		gbc_suraBox.insets = new Insets(0, 0, 5, 0);
		gbc_suraBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_suraBox.gridx = 1;
		gbc_suraBox.gridy = 0;
		add(suraBox, gbc_suraBox);
		
		JLabel lblAya = new JLabel("Aya :");
		lblAya.setForeground(Color.ORANGE);
		lblAya.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_lblAya = new GridBagConstraints();
		gbc_lblAya.anchor = GridBagConstraints.EAST;
		gbc_lblAya.insets = new Insets(0, 0, 5, 5);
		gbc_lblAya.gridx = 0;
		gbc_lblAya.gridy = 1;
		add(lblAya, gbc_lblAya);
		
		//creating ayahBox
		
		ayahBox = new JComboBox<Integer>();
		ayahBox.setForeground(Color.BLACK);
		ayahBox.setBackground(Color.LIGHT_GRAY);
		ayahBox.setEditable(true);
		ayahBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_ayaBox = new GridBagConstraints();
		gbc_ayaBox.gridwidth = 5;
		gbc_ayaBox.insets = new Insets(0, 0, 5, 0);
		gbc_ayaBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_ayaBox.gridx = 1;
		gbc_ayaBox.gridy = 1;
		add(ayahBox, gbc_ayaBox);
		
		JButton button = new JButton("<<Prev");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i=suraBox.getSelectedIndex();
				int j=ayahBox.getSelectedIndex();
				
				if(j==0)
				{
					if(i!=0)
					{
						//go to previous sura, last ayah
						j=SuraInformation.totalAyas[i-1]-1;
						selectionListener.ayahSelected(new Selected(i-1,j));//listener is not null
						suraBox.setSelectedIndex(i-1);
						ayahBox.setSelectedIndex(j);
					}
				}
				else
				{
					selectionListener.ayahSelected(new Selected(i,j-1));//listener is not null
					ayahBox.setSelectedIndex(j-1);
				}
			}
		});
		
		button.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.anchor = GridBagConstraints.EAST;
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 1;
		gbc_button.gridy = 7;
		add(button, gbc_button);
		
		JButton btnNext = new JButton("Next>>");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i=suraBox.getSelectedIndex();
				int j=ayahBox.getSelectedIndex();
				
				if(j==SuraInformation.totalAyas[i]-1)
				{
					if(i!=113)
					{
						//go to next sura, first ayah
						suraBox.setSelectedIndex(i+1);
						ayahBox.setSelectedIndex(0);
						selectionListener.ayahSelected(new Selected(i+1,0));//listener is not null
					}
				}
				else
				{
					ayahBox.setSelectedIndex(j+1);
					selectionListener.ayahSelected(new Selected(i,j+1));//listener is not null
				}
			}
		});
		btnNext.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.anchor = GridBagConstraints.EAST;
		gbc_btnNext.insets = new Insets(0, 0, 5, 5);
		gbc_btnNext.gridx = 2;
		gbc_btnNext.gridy = 7;
		add(btnNext, gbc_btnNext);
		
		JButton btnGo = new JButton("Go");
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.insets = new Insets(0, 0, 5, 5);
		gbc_btnGo.anchor = GridBagConstraints.EAST;
		gbc_btnGo.gridx = 3;
		gbc_btnGo.gridy = 7;
		add(btnGo, gbc_btnGo);
		
		
		try
		{
			getSuraNames(suraNameList,totalAyah);
			for(int i=0;i<114;i++)
			{
				suraNamesBox.addElement(Integer.toString(i+1)+"."+suraNameList[i]);
				totalAyahList[i]=new Integer[totalAyah[i]];
				for(int j=0;j<totalAyah[i];j++)
				{
					totalAyahList[i][j]=j+1;
				}
				models[i]=new DefaultComboBoxModel<>(totalAyahList[i]);
			}
			
		}catch(Exception e)
		{
			System.err.println("ComboBox fails");
			System.err.println(e);
			e.printStackTrace();
		}
		
		//setting initial values to suraBox and ayahBox
		suraBox.setModel(suraNamesBox);
		suraBox.setSelectedIndex(0);
		ayahBox.setModel(models[0]);
		ayahBox.setSelectedIndex(0);
		
		JLabel lblDisplay = new JLabel("Animation :");
		lblDisplay.setForeground(Color.ORANGE);
		GridBagConstraints gbc_lblDisplay = new GridBagConstraints();
		gbc_lblDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_lblDisplay.gridx = 0;
		gbc_lblDisplay.gridy = 8;
		add(lblDisplay, gbc_lblDisplay);
		
		rdbtnSingleAyah = new JRadioButton("Single Ayah");
		
		rdbtnSingleAyah.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange()==ItemEvent.SELECTED)
					AnimationPreferences.continuous=false;
				else
					AnimationPreferences.continuous=true;
			}
		});
		rdbtnSingleAyah.setBackground(Color.DARK_GRAY);
		rdbtnSingleAyah.setForeground(Color.YELLOW);
		GridBagConstraints gbc_rdbtnSingleAyah = new GridBagConstraints();
		gbc_rdbtnSingleAyah.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnSingleAyah.gridx = 1;
		gbc_rdbtnSingleAyah.gridy = 8;
		add(rdbtnSingleAyah, gbc_rdbtnSingleAyah);
		
		rdbtnContinuous = new JRadioButton("Continuous");
		rdbtnContinuous.setForeground(Color.YELLOW);
		rdbtnContinuous.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_rdbtnContinuous = new GridBagConstraints();
		gbc_rdbtnContinuous.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnContinuous.gridx = 2;
		gbc_rdbtnContinuous.gridy = 8;
		add(rdbtnContinuous, gbc_rdbtnContinuous);
		
		
		setAyahAnimationType();//single ayah or continuous
		
		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(rdbtnSingleAyah);
		buttonGroup.add(rdbtnContinuous);
		
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i=suraBox.getSelectedIndex();
				int j=ayahBox.getSelectedIndex();
				
				Selected ayah=new Selected(i, j);
				if(selectionListener!=null)
					selectionListener.ayahSelected(ayah);
				else
					System.out.println("No listener available");
				
				//System.out.println("Going to "+suraNameList[i]+" Ayah "+(j+1));
			}
		});
		
		//suraBox action listening
		suraBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Sura box responded");
				int i=suraBox.getSelectedIndex();
				ayahBox.setModel(models[i]);
				Selected ayah=new Selected(i, -1);
				if(selectionListener!=null)
					selectionListener.ayahSelected(ayah);//going to sidePanel
				else
					System.out.println("No listener available");
			}
		});
	}
	
	
	
	private void getSuraNames(String[] nameList,int[] totalAyah)
	{
		for(int i=0;i<114;i++)
		{
			nameList[i]=SuraInformation.suraInformations[i].title;
			totalAyah[i]=SuraInformation.suraInformations[i].ayahCount;
		}
	}
	
	
	
	public void setSelectionListener(SelectionListener listener)
	{
		this.selectionListener=listener;
	}
	
	public static void setSelectionIndex(Selected ayah)
	{
		suraBox.setSelectedIndex(ayah.suraIndex);
		ayahBox.setSelectedIndex(ayah.ayahIndex);
	}
	
	public void setAyahAnimationType()
	{
		if(AnimationPreferences.continuous)
			rdbtnContinuous.setSelected(true);
		else
			rdbtnSingleAyah.setSelected(true);
	}
}
