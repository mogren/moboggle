package pk.games.test1;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Test1 extends MIDlet{
	private static Test1Canvas test1Canvas;
	public Test1() {
		test1Canvas = new Test1Canvas();
	}
	public void startApp() {
		Display display = Display.getDisplay(this);
		test1Canvas.start();
		display.setCurrent(test1Canvas);
	} 
	public void pauseApp() { } 
	public void destroyApp(boolean unconditional) { }
	
}
