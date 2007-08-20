package pk.games.menu;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pk.games.test1.Test1Canvas;

public class Test1GUI extends MIDlet{
	protected Display display;
	private Test1Canvas gameScr;
	private MainMenuScr mainMenuScr;
	
	public Test1GUI() {}
	
	public Display getdisplay() {
		return display;
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		System.gc();
		notifyDestroyed();
	}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		try{
			mainMenuScr = new MainMenuScr(this);
		} catch (Exception ex) {}
		mainMenuScreenShow(null);
	}

	public void mainMenuScreenShow(Alert alert) {
		if (alert==null)
			display.setCurrent(mainMenuScr);
		else
			display.setCurrent(alert,mainMenuScr);
	}
	
	protected void mainMenuScreenQuit() {
		try {
			destroyApp(true);
		} catch (MIDletStateChangeException ex) {}
	}
	
	protected void gameScreenShow() {
		try { 	
			gameScr = null;
			gameScr = new Test1Canvas(this);      
			gameScr.start();
			display.setCurrent(gameScr);
		} catch (Exception ex) {}  
	}
}
