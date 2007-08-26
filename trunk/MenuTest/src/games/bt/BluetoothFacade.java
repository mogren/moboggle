package games.bt;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Facade for JSR82, connecting via the <code>btspp</code> protocol.
 * @author PK CM
 */
public class BluetoothFacade implements DiscoveryListener
{
	public static final String BT_PROTOCOL = "btspp";	/** Protocol */	
	protected Hashtable mServers = new Hashtable();		/** service id / server instance map */
	protected final Object DEVICE_LOCK = new Object();	/** Device Discovery lock */
	protected final Object SERVICE_LOCK = new Object();	/** Service Discovery lock */
	protected Vector mDevices = new Vector();			/** Device lookup result */
	protected ServiceRecord mRecord = null;				/** Service lookup result */

	/**
	 * Returns all devices nearby. This method
	 * blocks until discovery is finished.
	 * @return a Vector of <code>RemoteDevice</code>s.
	 */
	public Vector findDevices() throws IOException
	{
		synchronized(DEVICE_LOCK)
		{
			mDevices.removeAllElements();
			DiscoveryAgent discoveryAgent =	LocalDevice.getLocalDevice().getDiscoveryAgent();
			discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
			try
			{
				DEVICE_LOCK.wait();
			} catch (InterruptedException e) {}
			return mDevices;
		}
	}

	/**
	 * Returns a client StreamConnection for specified
	 * device and service number. This method searches for bluetooth
	 * services on specified device. If no service with specified service number 
	 * is found, null is returned.
	 * 
	 * @param serviceNumber 	The ID for the provided service
	 * @param device			The device to connect to
	 * @return A streamconnection or null if service not provided by device
	 * @throws IOException
	 */
	public StreamConnection connect(String serviceNumber, RemoteDevice device) throws IOException
	{
		ServiceRecord record = null;
		synchronized(SERVICE_LOCK)
		{
			mRecord = null;
			UUID[] filter = { new UUID(serviceNumber, false) };

			DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			int trans = discoveryAgent.searchServices(null, filter, device, this);

			try
			{
				SERVICE_LOCK.wait();
			} catch (InterruptedException e) {}
			record = mRecord;
			mRecord = null;
		}
		if (record != null)
			return connect(record);
		else
			return null;
	}

	/**
	 * Returns a client StreamConnection for specified
	 * service record.
	 * 
	 * @param record	The service record
	 * @return A streamconnection
	 * @throws IOException
	 */
	public StreamConnection connect(ServiceRecord record)
	throws IOException
	{
		int security = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
		String conURL = record.getConnectionURL(security, false);

		if (!conURL.startsWith(BT_PROTOCOL))
			throw new IOException("Protocol mismatch, expected " + BT_PROTOCOL);

		StreamConnection conn = (StreamConnection)Connector.open(conURL);
		return conn;
	}

	/**
	 * Setups a server if needed and returns a client. This method blocks until a
	 * client is connected. If multiple clients are allowed to be connected,
	 * simply call this method multiple times.
	 * 
	 * @param serviceNumber 	The ID for the provided service
	 * @return A streamconnection
	 * @throws IOException
	 */
	public StreamConnection waitForClient(String serviceNumber)
	throws IOException
	{
		// Set BT device to general discoverable mode
		LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);

		// Accept a client
		StreamConnectionNotifier server = (StreamConnectionNotifier) mServers.get(serviceNumber);
		if (server == null)
		{
			server = (StreamConnectionNotifier)Connector.open(BT_PROTOCOL + "://localhost:" + serviceNumber);
			mServers.put(serviceNumber, server);
		}
		StreamConnection clientConnection = server.acceptAndOpen();
		return clientConnection;
	}

	/**
	 * Closes the server setup for specified service ID.
	 * 
	 * @param serviceNumber   The ID for the provided service
	 * @throws IOException
	 */
	public void closeServer(String serviceNumber) throws IOException
	{
		StreamConnectionNotifier server = (StreamConnectionNotifier) mServers.get(serviceNumber);
		if (server != null)
		{
			server.close();
			mServers.remove(serviceNumber);
			server = null;
		}
	}

	// DiscoveryListener implementation

	// See interface javadoc
	public void servicesDiscovered(int transID, ServiceRecord[] records)
	{
		for (int i = 0; i < records.length; i++)
		{
			String conURL = records[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (conURL.startsWith(BT_PROTOCOL))
			{
				synchronized (SERVICE_LOCK)
				{
					mRecord = records[i];
					SERVICE_LOCK.notifyAll();
				}
				break;
			}
		}
	}

	// See interface javadoc
	public void serviceSearchCompleted(int transID, int respCode)
	{
		synchronized (SERVICE_LOCK)
		{
			SERVICE_LOCK.notifyAll();
		}
	}

	// See interface javadoc
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod)
	{
		synchronized(DEVICE_LOCK)
		{
			mDevices.addElement(btDevice);
		}
	}

	// See interface javadoc
	public void inquiryCompleted(int discType)
	{
		synchronized(DEVICE_LOCK)
		{
			DEVICE_LOCK.notifyAll();
		}
	}
}