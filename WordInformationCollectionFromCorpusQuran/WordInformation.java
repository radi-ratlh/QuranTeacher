/*
 *               In the name of Allah
 * This file is part of The "Quran Teacher or Learn Arabic" Project. Use is subject to
 * license terms.
 *
 * @author:         Fazle Rabbi Rahat
 * 
 */


public class WordInformation {

	static final int maxPartsOfSpeech=10;
	
	int index;
	String cell1;
	String wordId;
	String transLiteration;
	String meaning;
	
	String cell2;
	String imageLink;
	String imageId;
	
	String cell3;
	String[] partsOfSpeeches;
	String[] segmentColors;
	String[] partsOfSpeechDetails;
	
	@Override
	public String toString() {
		return    "\n\nindex="+index
				+ "\ncell1=" + cell1 
				+ "\nwordId=" + wordId
				+ "\ntransLiteration=" + transLiteration 
				+ "\nmeaning="+ meaning 
				+ "\n\ncell2=" + cell2 
				+ "\nimageLink="+imageLink
				+ "\nimageId=" + imageId
				+ "\n\ncell3=" + cell3 
				+ "\npartsOfSpeeches="+ getElements(partsOfSpeeches) 
				+ "\nsegmentColors="+ getElements(segmentColors) 
				+ "\npartsOfSpeechDetails="+ getElements(partsOfSpeechDetails);
	}

	public WordInformation() {
		
		partsOfSpeeches=new String[maxPartsOfSpeech];
		segmentColors=new String[maxPartsOfSpeech];
		partsOfSpeechDetails=new String[maxPartsOfSpeech];
	}
	
	public String getElements(String[] array)
	{
		String s;
		
		if(array[0]!=null)
			s=array[0];
		else
			return null;
		
		for(int i=1;i<maxPartsOfSpeech && array[i]!=null;i++)
		{
			s+=","+array[i];
		}
		return s;
	}

}
