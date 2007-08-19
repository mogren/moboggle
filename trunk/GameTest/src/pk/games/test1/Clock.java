package pk.games.test1;

import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;

public class Clock extends TimerTask {	
	int left;	// Time left

	public Clock(int startTime) { left = startTime; }
	public void run() { left--; }
	public int getTimeLeft() { return this.left; }
	
	public void renderTime(Graphics g, CustomFont f, int x, int y) {
		
		// draw the timer box
		g.setColor(0x333388);
		g.fillRect(x,y,108,15);
		g.setColor(0x000000);
		g.drawRect(x,y,108,15);

		// draw the time left string
		int m = left / 60;
		int s = left % 60;
		if(s<10) f.drawString(g,"Time left: " + m + ":0" + s,x+4,y+4,0);
		else f.drawString(g,"Time left: " + m + ":" + s,x+4,y+4,0);
	}
}
