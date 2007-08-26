package mobo.game;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import games.res.Resources;
import games.gui.PopupCanvas;

public class BoardCanvas extends PopupCanvas implements CommandListener {

	protected static BoardCanvas mInst;			/** Singleton instance */
	protected boolean mAllowInteraction = false;/** Flag indicating if user can interact */

	public static final Command CMD_EXIT = new Command((Resources.TXT_QUIT), Command.BACK, 1);	/** Exit command */

	/**
	 * Returns singleton instance.
	 * @return The singleton instance.
	 */
	public static BoardCanvas getInstance()
	{
		if (mInst == null)
		{
			mInst = new BoardCanvas();
			mInst.setFullScreenMode(true);
		}
		return mInst;
	}

	/**
	 * Shuts down the canvas logic.
	 */
	public void shutdown()
	{
		mInst = null;
	}

	/**
	 * Paints the backgammon board, any surrounding
	 * graphics and all current animations.
	 * 
	 * @param g Graphics context.
	 */
	protected void paint(Graphics g)
	{
		try
		{
			// Clear background
			g.setColor(0x00aa55);
			g.fillRect(0,220,176, 0);
			// Draw backgammon board
			// Draw animations
			// Draw softbuttons
			// Draw popup if necessary
			if (m_popup != null && m_popup.isActive())
				m_popup.paint(g);
		}
		catch (Throwable t)
		{}
	}

	/**
	 * Handles command invokations from <code>SoftButtonControl</code>.
	 * @param c the command.
	 * @param d the displayable.
	 */
	public void commandAction(Command c, Displayable d)
	{
		boolean consumed = false;

		// Exit, can be invoked without being user's turn
		//if (!consumed && c == CMD_EXIT)
		//	consumed = BoardMediator.getLocalPlayer().commandAction(c);

		repaint();
	}



}
