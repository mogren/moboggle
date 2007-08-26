package mobo.game;

/**
 * Implementations of this class can be registered in
 * the <code>Player</code> and will receive events upon player actions.
 * @author PK CM
 */
public interface IPlayerListener
{
	public static final int LOCAL_QUIT = 0;		/** Constant denoting a game exit on user quitting  */
	public static final int REMOTE_QUIT = 1;	/** Constant denoting a game exit on remote user quitting  */
	public static final int LOCAL_GIVE_UP = 2;	/** Constant denoting a game exit on local user giving up */
	public static final int REMOTE_GIVE_UP = 3;	/** Constant denoting a game exit on remote user giving up */

	/**
	 * Called when a player performs a move.
	 * @param id			Player id.
	 * @param moveIndex	The moveindex in possible move array used for move.
	 */
	public void moveMade(int id, int moveIndex);
	/**
	 * Called when player performs an undo.
	 * @param id			Player id.
	 */
	public void undoPerformed(int id);
	/**
	 * Called when player is finished with his/her turn.
	 * @param id			Player id.
	 */
	public void turnCommit(int id);
	/**
	 * Called when player sent a message.
	 * @param id			Player id.
	 * @param msg	The message.
	 */
	public void messageSent(int id, char[] msg);
	/**
	 * Called when player exits the game for some reason.
	 * @param id			Player id.
	 * @param reason	The reason for exiting game.
	 */
	public void gameExited(int id, int reason);
}
