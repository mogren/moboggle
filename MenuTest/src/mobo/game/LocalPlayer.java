package mobo.game;

import javax.microedition.lcdui.Command;

import mobo.menu.MoBoggle;
import games.res.Resources;
import games.gui.IStringInputHandler;
import games.gui.IPopupListener;

/**
 * A local player has access to the actual device. A <code>LocalPlayer</code> is
 * coupled with the <code>BoardCanvas</code>, knowing the low-level gui states
 * and receives interaction calls from the gui.
 * 
 * @author Peter Andersson
 */
public class LocalPlayer extends Player implements IStringInputHandler, IPopupListener {
	
	protected BoardCanvas mCanvas;	/** The canvas that this local player uses */

	/**
	 * Constructs a local player.
	 * @param id      The id of the local player.
	 * @param name    The name of the local player.
	 * @param white   The color of the local player.
	 * @param canvas  The backgammon board canvas this local
	 *                player acts upon.
	 */
	public LocalPlayer(int id, char[] name, BoardCanvas canvas)
	{
		init(id, name);
		mCanvas = canvas;
	}

	/**
	 * Called from <code>BoardCanvas</code> when a key is pressed.
	 * @param keyCode		The key code.
	 * @param gameCode	The game code.
	 * @return true if the keypress is consumed, false otherwise
	 */
	public boolean keyPressed(int keyCode, int gameCode)
	{
		boolean consumed = false;
		// Check if game is finished or if some state prevents interaction
		if (BoardMediator.isGameFinished() )
		{
			return consumed;
		}
		else	// Awaiting commitment, any keypress will do
		{
			// Commit moves
			//BoardMediator.commitTurn();
			//fireTurnCommit();
			consumed = true;
		}
		return consumed;
	}

	/**
	 * Called from <code>BoardCanvas</code> when a command is executed.
	 * @param c	The command.
	 * @return true if event is consumed, false otherwise.
	 */	
	public boolean commandAction(Command c)
	{
		boolean consumed = false;
		if (c == BoardCanvas.CMD_EXIT)
		{
			if (MoBoggle.getGameType() == MoBoggle.GAME_TYPE_LOCAL)
			{
				fireGameExited(IPlayerListener.LOCAL_QUIT);
				BoardMediator.exitGame(IPlayerListener.LOCAL_QUIT);
			}
			else
			{
				if (BoardMediator.isGameFinished())
				{
					fireGameExited(IPlayerListener.LOCAL_QUIT);
					BoardMediator.exitGame(IPlayerListener.LOCAL_QUIT);
				}
				else
				{
					char[][] alts = {(Resources.TXT_GIVE_UP).toCharArray(), (Resources.TXT_CANCEL).toCharArray()};
					MoBoggle.showPopup((Resources.TXT_QUIT_REMOTE).toCharArray(), alts,	0, 2, 2, this);
				}
			}
			consumed = true;
		}
		return consumed;
	}

	// see gui.InputHandler#handleInput(java.lang.String) javadoc
	public void handleStringInput(String s)
	{
		if (s != null)
			fireMessageSent(s.toCharArray());
	}

	/**
	 * PopupListener implementation, called from a popup.
	 */
	public void selectedChoice(byte choice, boolean timeOut)
	{
		switch(choice)
		{
		case 0: // give up
			fireGameExited(IPlayerListener.LOCAL_GIVE_UP);
			BoardMediator.exitGame(IPlayerListener.LOCAL_GIVE_UP);
			break;
		case 1: // save game
			fireGameExited(IPlayerListener.LOCAL_QUIT);
			BoardMediator.exitGame(IPlayerListener.LOCAL_QUIT);
			break;
		case 2: // cancel
			break;
		}
	}
}