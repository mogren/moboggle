package games.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import games.res.Device;

/**
 * <p>
 * A <code>Handshake</code> consists of sending and receiving data as specified
 * below, where one player has server role and the other has client role.
 * </p><p>
 * Stores needed data for further setup, and initializes static aspects
 * of the game (rules and random seed).
 * </p><p>
 * <pre>
 * SERVER                              CLIENT
 *                                     SEND random seed (int)
 *                                     SEND client id (int)
 *                                     SEND client name (UTF)
 * SEND server id (int)
 * SEND server name (UTF)
 * SEND client color (boolean)
 * </pre>
 * </p>
 * @author PK CM
 */
public class Handshake
{
	protected int mRemoteId;			/** The id of the remote device being handshaked with*/
	protected char[] mRemoteName;		/** The name of the remote device being handshaked with*/

	/**
	 * Performs a handshake as a server. 
	 * 
	 * @param dis           The input stream from the other device.
	 * @param dos			The output stream to the other device.
	 * @param localName     The name of this device.
	 * @throws IOException  if the handshake fails.
	 */
	public void serverHandshake( DataInputStream dis, DataOutputStream dos, String localName) throws IOException
	{
		// Receive client stats
		mRemoteId = dis.readInt();
		mRemoteName = dis.readUTF().toCharArray();

		// Send server stats
		dos.writeInt(Device.getDeviceId());
		dos.writeUTF(localName);
		dos.flush();
	}

	/**
	 * Performs a handshake as a client. 
	 * 
	 * @param dis           The input stream from the other device.
	 * @param dos			The output stream to the other device.
	 * @param localName     The name of this device.
	 * @throws IOException  if the handshake fails.
	 */
	public void clientHandshake( DataInputStream dis, DataOutputStream dos, String localName) throws IOException
	{
		long randomSeed = System.currentTimeMillis();

		// Send client stats
		dos.writeLong(randomSeed);
		dos.writeInt(Device.getDeviceId());
		dos.writeUTF(localName);
		dos.flush();

		// Receive server stats
		mRemoteId = dis.readInt();
		mRemoteName = dis.readUTF().toCharArray();
	}

	/**
	 * Returns the id of the other device after a successful handshake.
	 * 
	 * @return   The id of the remote device.
	 */
	public int getRemoteId() { return mRemoteId; }

	/**
	 * Returns the name of the other device after a successful handshake.
	 * 
	 * @return   The name of the remote device.
	 */
	public char[] getRemoteName() { return mRemoteName; }
}