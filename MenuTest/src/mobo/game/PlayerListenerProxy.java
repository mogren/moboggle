package mobo.game;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class serializes all events from sent to a <code>PlayerListener</code> and 
 * sends them to specified output stream.
 * In this game there is a <code>RemotePlayer</code>
 * on the other side of the connection. The <code>RemotePlayer</code> is also a
 * <code>PlayerListener</code> and will get same listener events as this proxy via
 * streams. Relies on that specified outstream delivers and handles all fuzziness with
 * I/O.
 * 
 * @author PK CM
 */
public class PlayerListenerProxy implements IPlayerListener
{
	public static final byte MOVE = (byte) 0;	/** Identification byte for reporting a piece movement. Following is an ID integer and an move index integer.*/
	public static final byte UNDO = (byte) 1;	/** Identification byte for reporting an undo. Following is an ID integer.*/
	public static final byte TURN = (byte) 2;	/** Identification byte for reporting a committed turn. Following is an ID integer.*/
	public static final byte MSG  = (byte) 3;	/** Identification byte for sending a message to remote player. Following is an ID integer and a UTF-8-string.*/
	public static final byte EXIT = (byte) 4;	/** Identification byte for reporting a player's shutdown. Following is an ID integer and a reason integer.*/

	protected DataOutputStream mOut;	/** The data output stream */

	/**
	 * Creates a new <code>PlayerListenerProxy</code> that
	 * sends received <code>PlayerListener<code> events via
	 * specified output stream.
	 * @param out	The stream to send events to.
	 */
	public PlayerListenerProxy(DataOutputStream out)
	{
		mOut = out;
	}

	// See interface javadoc
	public void moveMade(int id, int moveIndex)
	{
		try
		{
			mOut.writeByte(MOVE);
			mOut.writeInt(id);
			mOut.writeInt(moveIndex);
			mOut.flush();
		}
		catch (IOException e)
		{
			BoardMediator.lostRemoteConnection(e);
		}
	}

	// See interface javadoc
	public void undoPerformed(int id)
	{
		try
		{
			mOut.writeByte(UNDO);
			mOut.writeInt(id);
			mOut.flush();
		}
		catch (IOException e)
		{
			BoardMediator.lostRemoteConnection(e);
		}
	}

	// See interface javadoc
	public void turnCommit(int id)
	{
		try
		{
			mOut.writeByte(TURN);
			mOut.writeInt(id);
			mOut.flush();
		}
		catch (IOException e)
		{
			BoardMediator.lostRemoteConnection(e);
		}
	}

	// See interface javadoc
	public void messageSent(int id, char[] msg)
	{
		try
		{
			mOut.writeByte(MSG);
			mOut.writeInt(id);
			mOut.writeUTF(new String(msg));
			mOut.flush();
		}
		catch (IOException e)
		{
			BoardMediator.lostRemoteConnection(e);
		}
	}

	// See interface javadoc
	public void gameExited(int id, int reason)
	{
		try
		{
			// Sending the reason to other device,
			// flip local/remote if giving up/quitting
			if (reason == LOCAL_GIVE_UP)
			{
				reason = REMOTE_GIVE_UP;
			}
			else if (reason == REMOTE_GIVE_UP)
			{
				reason = LOCAL_GIVE_UP;
			}
			else if (reason == REMOTE_QUIT)
			{
				reason = LOCAL_QUIT;
			}
			else if (reason == LOCAL_QUIT)
			{
				reason = REMOTE_QUIT;
			}
			mOut.writeByte(EXIT);
			mOut.writeInt(id);
			mOut.writeInt(reason);
			mOut.flush();
		}
		catch (IOException e)
		{
			BoardMediator.lostRemoteConnection(e);
		}
	}
}