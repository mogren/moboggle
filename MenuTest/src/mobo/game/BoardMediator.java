package mobo.game;

import java.io.IOException;

import mobo.menu.MoBoggle;
import games.res.Resources;
import games.gui.Popup;

/**
 * <p>
 * The <code>BoardMediator</code> class is active during a moboggle game. It takes care
 * of coordination between <code>BoardState</code> and <code>BoardCanvas</code> i.e.
 * between the gui and the logic. <code>BoardCanvas</code> is detached from any
 * moboggle - this connection is made by this class by implementing
 * <code>BoardStateListener</code> to receive events upon board state changes. The
 * <code>BoardMediator</code> then calls the <code>BoardCanvas</code> to update the gui
 * when state changes.
 * </p><p>
 * In order to save the game at any time, but still have animations, the
 * <code>BoardState</code> differs from the <code>BoardCanvas</code>: when the state 
 * is changed (e.g. a move is made), the <code>BoardState</code> is updated
 * directly. The <code>BoardCanvas</code> has intermittent states; e.g. during an animation
 * of a piece movement, one piece is "missing" from <code>BoardCanvas</code> board until
 * the piece is put down.
 * </p><p>  
 * This class is a simple coordinater, thus it does not have any state. The
 * <code>BoardMediator</code> is accessed statically.
 * <p>
 * 
 * @see bluegammon.gui.BoardCanvas
 * @see bluegammon.logic.BoardState
 * @author PK CM
 */
public class BoardMediator {
	/**
	 * Starts the BoardMediator.
	 */
	public static void startup()
	{
		//BoardState.getInstance().setGameListener(new BoardMediator());
	}

	/**
	 * Initiates a new or resumed game with specified players.
	 * @param p1			Player 1.
	 * @param p2			Player 2.
	 * @param resumed		True if resumed, false if new game.
	 */
	public static void init(Player p1, Player p2)
	{
		BoardCanvas canvas = BoardCanvas.getInstance();
	}

	/**
	 * Shuts the BoardMediator down.
	 */
	public static void shutdown()
	{
		BoardCanvas.getInstance().shutdown();
	}


	/**
	 * Called from interaction, exits current game.
	 * 
	 * @param reason	An integer denoting the reason for quitting,
	 * 				one of <code>PlayerListener.LOCAL_QUIT</code>,
	 * 				<code>PlayerListener.REMOTE_QUIT,</code>
	 * 				<code>PlayerListener.LOCAL_GIVE_UP,</code>
	 * 				<code>PlayerListener.REMOTE_GIVE_UP</code>
	 */
	public synchronized static void exitGame(int reason)
	{
		if (reason == IPlayerListener.LOCAL_GIVE_UP || reason == IPlayerListener.REMOTE_GIVE_UP)
		{
			boolean localLoser = reason == IPlayerListener.LOCAL_GIVE_UP;
			if (reason == IPlayerListener.REMOTE_GIVE_UP)
				MoBoggle.showPopup((Resources.TXT_REMOTE_GAVE_UP).toCharArray(), Popup.ALT_OK, 10, 0, 0, null);

			if (localLoser)
				MoBoggle.exitGame();
		}
		else if (reason == IPlayerListener.REMOTE_QUIT)
		{
			if (!isGameFinished())
			{
				MoBoggle.showPopup((Resources.TXT_BT_REMOTE_QUIT).toCharArray(), Popup.ALT_OK, 0, 0, 0, null);
				MoBoggle.exitGame();
			}
		}
		else
		{
			if (MoBoggle.isShowingPopup())
				MoBoggle.getCurrentPopup().dispose();
			MoBoggle.exitGame();
		}
	}

	/**
	 * Called from IO framework remote connection is lost in a 
	 * remote game.
	 * @param e		The exception that was
	 * 				the reason of losing connection.
	 */
	public synchronized static void lostRemoteConnection(IOException e)
	{
		if (!isGameFinished())
		{
			MoBoggle.showPopup((Resources.TXT_BT_CONN_LOST).toCharArray(), Popup.ALT_OK, 0, 0, 0, null);
			MoBoggle.exitGame();
		}
	}  

	/**
	 * Called when remote player sent a message
	 * @param mess	the message.
	 */
	public static void showMessage(char[] mess)
	{
		//mess = (new String(getOpponentPlayer().getName()) + ":\n\n" + new String(mess)).toCharArray();
		//MoBoggle.showPopup(mess, null, 60, 0, 0, null);
	}

	/**
	 * Returns whether the game is finished or not
	 * @return	true if finished, false otherwise
	 */
	public static boolean isGameFinished()
	{
		return true;
	}

	/**
	 * Returns the boardcanvas singleton instance.
	 * @return The boardcanvas singleton instance.
	 */
	public static BoardCanvas getCanvas()
	{
		return BoardCanvas.getInstance();
	}

	/** Prevent construction */
	private BoardMediator()	{}
}