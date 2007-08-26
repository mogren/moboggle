package mobo.menu;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import mobo.menu.MoBoggle;

public class MenuTest extends MIDlet {
	protected boolean mInit = false;
	
	protected void startApp() throws MIDletStateChangeException {
		if(!mInit)
		{
			mInit = true;
			MoBoggle.init(this, Display.getDisplay(this));
		}		
	}

	// Pause code
	protected void pauseApp() {}

	// Exit Code
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

}
