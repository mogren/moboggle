package pk.games.menu;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class MainMenuScr extends List implements CommandListener {
	private Test1GUI midlet;
	private Command exitCommand = new Command("Quit", 1,1);
	private Command selectCommand = new Command("Ok", 2,1);

	public MainMenuScr(Test1GUI midlet) throws Exception {
		super("Mini Boggle Test",Choice.IMPLICIT);
		this.midlet = midlet;
		append("New Game",null);
//		append("Options",null);
		addCommand(selectCommand);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == exitCommand) {
			midlet.mainMenuScreenQuit();
		} 
		else if (c == selectCommand) {
			processMenu();
		}
		else {
			processMenu();
		}
	}
	
	private void processMenu() {
		try {
			List down = (List)midlet.display.getCurrent();
			switch (down.getSelectedIndex()) {
				case 0: scnNewGame(); break;
//				case 1: scnOption(); break;
			};
		} catch (Exception ex) {}
	}
	
	private void scnNewGame() {
		midlet.gameScreenShow();
	}
}
