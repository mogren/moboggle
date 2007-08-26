package games.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.StreamConnection;

/**
 * This interface defines needed methods for connecting to 
 * another device on a remote game.
 * @author PK CM
 */
public interface IGameConnection
{
	/**
	 * Opens a server and waits for client. This method should block
	 * until a client connects or an IOException is thrown.
	 * @throws IOException
	 */
	public void waitForClient() throws IOException;
	/**
	 * Returns true if this connection is awaiting a client already.
	 * @return	True if awaiting client, false otherwise.
	 */
	public boolean isAwaitingClient();
	/**
	 * Connects a client to a server.
	 * @param serverInfo		Any info needed to connect to the server.
	 * @return				True if client connects successfully, false otherwise.
	 * @throws IOException
	 */
	public boolean connectClient(Object serverInfo) throws IOException;
	/**
	 * If server has got a client or client is connected to a server,
	 * this method returns the connection. Otherwise it returns null.
	 * @return	The stream connection or null.
	 * @throws IOException
	 */
	public StreamConnection getConnection() throws IOException;
	/**
	 * If server has got a client or client is connected to a server,
	 * this method returns the input stream. Otherwise it returns null.
	 * @return the input stream or null.
	 */
	public DataInputStream getInput();
	/**
	 * If server has got a client or client is connected to a server,
	 * this method returns the output stream. Otherwise it returns null.
	 * @return the output stream or null.
	 */
	public DataOutputStream getOutput();
	/**
	 * Closes all resources opened in this connection.
	 * @throws IOException
	 */
	public void close() throws IOException;
	/**
	 * Returns if this connection has been closed or not.
	 * @return true if closed, false otherwise.
	 */
	public boolean isClosed();
}
