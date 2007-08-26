package mobo.game;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * <p>
 * Represents a player that is on the other side of a connection;
 * a remote player does not have access to this device.
 * </p><p>
 * This <code>RemotePlayer</code> is also a <code>PlayerListener</code> and
 * retreives events from a <code>LocalPlayer</code> remotely sent from a
 * <code>PlayerListenerProxy</code> via streams. It then deparses remote data
 * and calls appropriate action on the <code>BoardMediator</code>.
 * </p> 
 * @author PK CM
 */
public class RemotePlayer extends Player implements IPlayerListener, Runnable {

	protected DataInputStream mIn;	/** Data input stream */

	/**
	 * Creates a remote player.
	 * @param id		The id of the remote player.
	 * @param name	The name of the remote player.
	 * @param white	The color of the remote player, true for white, false for black.
	 * @param in		The stream the remote player is reading events from.
	 */
	public RemotePlayer(int id, char[] name, DataInputStream in)
	{
		init(id, name);
		mIn = in;
		new Thread(this,"RemotePlayer").start();
	}

	/**
	 * Called when a move made event is received.
	 */
	public void moveMade(int id, int moveIndex)
	{
		//BoardMediator.makePlayerMove(moveIndex);
		fireMoveMade(moveIndex);
	}

	/**
	 * Called when an undo performed event is received.
	 */
	public void undoPerformed(int id)
	{
		//BoardMediator.undoLastMove();
		fireUndoPerformed();
	}

	/**
	 * Called when a turn commit event is received.
	 */
	public void turnCommit(int id)
	{
		//BoardMediator.commitTurn();
		fireTurnCommit();
	}

	/**
	 * Called when a message event is received.
	 */
	public void messageSent(int id, char[] msg)
	{
		BoardMediator.showMessage(msg);
		fireMessageSent(msg);
	}

	/**
	 * Called when a game exit event is received.
	 */
	public void gameExited(int id, int reason)
	{
		fireGameExited(reason);
		BoardMediator.exitGame(reason);
	}

	/**
	 * Runnable implementation,
	 * reading data from the input stream and
	 * deserializes the events.
	 */
	public void run()
	{
		boolean running = true;
		try
		{
			while (running)
			{
				// Read command byte
				byte cmd = mIn.readByte();
				// Read id
				int id = mIn.readInt();

				switch (cmd)
				{
				// Got a movement
				case PlayerListenerProxy.MOVE:
					int possibleMoveIndex = mIn.readInt();
					moveMade(id, possibleMoveIndex);
					break;
					// Got an undo
				case PlayerListenerProxy.UNDO:
					undoPerformed(id);
					break;
					// Got new turn
				case PlayerListenerProxy.TURN:
					turnCommit(id);
					break;
					// Other player exited
				case PlayerListenerProxy.EXIT:
					int reason = mIn.readInt();
					running = false;
					gameExited(id, reason);
					break;
					// Got a message
				case PlayerListenerProxy.MSG:
					String msg = mIn.readUTF();
					messageSent(id, msg.toCharArray());
					break;
				}
			}
		}
		catch (EOFException eof)
		{
			if (running) 
				BoardMediator.lostRemoteConnection(eof);
		}
		catch (IOException ioe)
		{
			if (running) 
				BoardMediator.lostRemoteConnection(ioe);
		}
	}
}