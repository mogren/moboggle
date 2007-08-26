package mobo.game;

import java.util.Vector;

/**
 * Abstract class for a player. Contains generic functionality and
 * has helper methods for sending events to any <code>PlayerListener</code>.
 * 
 * @author PK CM
 */
public abstract class Player {
		
	/**
	 * Listeners, vector of PlayerListener. Generally in J2ME ,
	 * there is support for only one listener when using the observer pattern.
	 * Here, we allow multiple listeners to easily enable extensions of the
	 * game; even though there is only one listener in this implementation of
	 * the game.
	 */
	protected Vector mListeners = new Vector();
	protected int mID;							/** ID of this player */
	protected char[] mName;						/** Name of this player */

	/**
	 * Initializes this player.
	 * @param id		The id of the player.
	 * @param name	The name of the player.
	 * @param white	The color of the player, true for white, false for black.
	 */
	public void init(int id, char[] name)
	{
		mID = id;
		mName = name;
	}
	
	/**
	 * Returns the id of this player.
	 * @return	The player id.
	 */
	public int getId() { return mID; }
	
	/**
	 * Returns the name of this player.
	 * @return	The player name.
	 */
	public char[] getName() { return mName; }
	
	/**
	 * Adds a listener to this player.
	 * @param listener	The listener to add.
	 */
	public void addListener(IPlayerListener listener) { mListeners.addElement(listener); }

	/**
	 * Fires a move made event to listeners.
	 * @param possibleMoveIndex	The index of the possible move that represents the move.
	 */
	protected void fireMoveMade(int possibleMoveIndex)
	{
		synchronized(mListeners)
		{
			for (int i = mListeners.size() - 1; i >= 0; i--)
				((IPlayerListener)mListeners.elementAt(i)).moveMade(getId(), possibleMoveIndex);
		}
	}
	
	/**
	 * Fires a undo performed event to listeners.
	 */
	protected void fireUndoPerformed()
	{
		synchronized(mListeners)
		{
			for (int i = mListeners.size() - 1; i >= 0; i--)
				((IPlayerListener)mListeners.elementAt(i)).undoPerformed(getId());
		}
	}

	/**
	 * Fires a turn commit event to listeners.
	 */
	protected void fireTurnCommit()
	{
		synchronized(mListeners)
		{
			for (int i = mListeners.size() - 1; i >= 0; i--)
				((IPlayerListener)mListeners.elementAt(i)).turnCommit(getId());
		}
	}
	
	/**
	 * Fires a game exited to listeners.
	 * @param reason	The reason for exiting the game, one of
	 * 	<code>PlayerListener.LOCAL_QUIT</code>, 
	 * 	<code>PlayerListener.REMOTE_QUIT</code>, 
	 *	<code>PlayerListener.LOCAL_GIVE_UP</code>, 
	 * 	<code>PlayerListener.REMOTE_GIVE_UP</code>.
	 */
	protected void fireGameExited(int reason)
	{
		synchronized(mListeners)
		{
			for (int i = mListeners.size() - 1; i >= 0; i--)
				((IPlayerListener)mListeners.elementAt(i)).gameExited(getId(), reason);
		}
	}
	
	/**
	 * Fires a message event to listeners.
	 * @param message	The message content.
	 */
	protected void fireMessageSent(char[] message)
	{
		synchronized(mListeners)
		{
			for (int i = mListeners.size() - 1; i >= 0; i--)
				((IPlayerListener)mListeners.elementAt(i)).messageSent(getId(), message);
		}
	}
}
