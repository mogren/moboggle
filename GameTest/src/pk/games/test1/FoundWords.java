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
	
	public void submitWord(char[] data, int length) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++) 
			buf.append(data[i]);
		list[count++]=buf.toString();;		
	}
	
	public void renderFoundWords(Graphics g, CustomFont f, int x, int y) {
		
		// draw list box
		g.setColor(0x333388);
		g.fillRect(x,y,boxWidth,boxHeight);	
		g.setColor(0x000000);
		g.drawRect(x,y,boxWidth,boxHeight);

		// draw the list
		int length;
		int outside;
		int charWidth = f.charWidth('a');
		int start;
		if (count * f.getHeight() > boxHeight-4)
			start= (count * f.getHeight() - (boxHeight-4))/f.getHeight();
		else 
			start=0;
			
		for(int i = start; i<count; i++) {
			length = f.stringWidth(list[i]);
			if(length > boxWidth) {
				outside = (length - boxWidth)/charWidth;
				f.drawSubstring(g, list[i], 0, (length/charWidth)-(outside+4), x+4, 2+y+((i-start)*f.getHeight()), 0);
				f.drawString(g, "...", x+4+(((length/charWidth)-(outside+4))*f.charWidth('a')), 2+y+((i-start)*f.getHeight()), 0);
			}
			else
				f.drawString(g, list[i], x+4, 2+y+((i-start)*f.getHeight()), 0);
		}
	}

}
