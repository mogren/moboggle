package pk.games.test1;

import javax.microedition.lcdui.Graphics;

public class FoundWords {
	private String[] list;	// List of submitted words
	private int count;		// Number of words in the list
	private int boxWidth;	// Width of the list
	private int boxHeight;	// Height of the list
	
	public FoundWords() { 
		//TODO: Bättre lista
		list = new String[100];
		boxWidth = 52;
		boxHeight = 148;
	}
	
	// Submit a word to the found words list
	public void submitWord(char[] data, int length) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++) 
			buf.append(data[i]);
		list[count++]=buf.toString();;		
	}
	
	// Word count getter
	public int getCount(){
		return count;
	}
	
	// Draw a box with the list of found words, adds to bottom of the list and scrolls
	// if too many words.
	public void renderFoundWords(Graphics g, CustomFont f, int x, int y) {
		
		// draw list box
		g.setColor(0x333388);
		g.fillRect(x,y,boxWidth,boxHeight);	
		g.setColor(0x000000);
		g.drawRect(x,y,boxWidth,boxHeight);

		//draw the list
		int charWidth = f.charWidth('a');
		int charHeight = f.getHeight();
		int rows = (boxHeight-2)/(charHeight+2);
		int cols = (boxWidth -4)/(charWidth);
		int offset = Math.max(0, count-rows);
		for(int i=offset;i<count;i++){
			if(list[i].length() > cols) {
				f.drawSubstring(g, list[i], 0, cols-4, x+4, 2+y+(i-offset)*charHeight,0);
				f.drawString(g, "...", x+4+(cols-4)*charWidth, 2+y+((i-offset)*charHeight), 0);
			}
			else
				f.drawString(g, list[i], x+4, 2+y+((i-offset)*charHeight), 0);
		}
	}
}
