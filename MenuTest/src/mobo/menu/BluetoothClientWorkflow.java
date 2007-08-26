package mobo.menu;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import games.res.Resources;
import games.bt.IGameConnection;
import games.gui.*;
import mobo.game.MoBoggleBTConnection;


/**
 * Handles the different steps of connecting to a server
 * as a client. Controls the gui component, the 
 * <code>BluetoothDevicePage</code>
 * 
 * @see bluegammon.gui.BluetoothDevicePage
 * @author PK CM
 */
public class BluetoothClientWorkflow implements IItemAction, DiscoveryListener, IPopupListener
{
	protected BluetoothDevicePage mDevicePage;				/** The page we add found device items to */
	protected Popup mPopup;									/** Reference to popup we need to close on successful connection */
	protected IGameConnection mClientConnection = null;	/** The connection */
	protected static final int REMOTE_DEVICE = 100;			/** Page item property key identifying the remote device */

	/**
	 * Creates a workflow for connecting to a server as client. Uses
	 * the specified device page as a gui component.
	 * @param devicePage	The gui component of this workflow.
	 */
	public BluetoothClientWorkflow(BluetoothDevicePage devicePage)
	{
		mDevicePage = devicePage;
	}

	/**
	 * Refreshes the view of nearby devices. 
	 */
	public synchronized void refresh()
	{
		if (!mDevicePage.isSearching())
		{
			mDevicePage.removeAllItems();
			DiscoveryAgent discoveryAgent;
			try
			{
				discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
				discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
				mDevicePage.setSearching(true);
			}
			catch (BluetoothStateException e)
			{
				searchingStopped();
				e.printStackTrace();
				MoBoggle.showPopup((Resources.TXT_BT_DISC_FAIL + "\n" + e.getMessage()).toCharArray(), Popup.ALT_OK, 10, 0, 0, null);
			}
		}
	}


	/**
	 * Called when all nearby devices has been found or
	 * an error occurred during search.
	 */
	public void searchingStopped()
	{
		mDevicePage.setSearching(false);
		if (mDevicePage.size() == 0)
			MoBoggle.showPopup(Resources.TXT_BT_NO_DEVICES.toCharArray(), Popup.ALT_OK, 60,0,0, null);
	}

	/**
	 * Called when user selects an item in a page. This
	 * item can be that user wants to see list of devices
	 * to connect to, or the actual device item to connect to.
	 * 
	 * @param page	The page this action is called from.
	 * @param item	The item this action is called from.
	 */
	public void itemAction(MenuPage page, PageItem item)
	{
		Object rDevObj = item.getProperty(REMOTE_DEVICE);
		if (rDevObj != null)
		{
			// User has selected a device to connect to
			RemoteDevice rDev = (RemoteDevice)rDevObj;
			connectTo(rDev, item.getLabel());
		}
		else
		{
			// User is moving to the page displaying all nearby
			// devices, start a refresh if view is empty
			if (!mDevicePage.isSearching() && mDevicePage.size() == 0)
				refresh();
		}
	}

	/**
	 * Called when user presses cancel during connection to server.
	 */
	public void selectedChoice(byte choice, boolean timeOut)
	{
		Popup p = MoBoggle.getCurrentPopup();
		if (p != null) p.dispose();
		IGameConnection bgconn = null;
		synchronized(this)
		{
			bgconn = mClientConnection;
		}
		if (bgconn != null)
		{
			try
			{
				bgconn.close();
			} catch (IOException e) {}
		}
	}

	/**
	 * Connects to a remote server.
	 * @param rDev	The remote device running the server.
	 * @param name	The name of the remote device.
	 */
	protected void connectTo(RemoteDevice rDev, char[] name)
	{
		mPopup = MoBoggle.showPopup((Resources.TXT_BT_CONNECT + " " + new String(name) + "...").toCharArray(), Popup.ALT_CANCEL, 0, 0, 0, this);
		synchronized (this)
		{
			if (mClientConnection == null)
				mClientConnection = new MoBoggleBTConnection();
		}
		try
		{
			if (!mClientConnection.connectClient(rDev))
			{
				MoBoggle.showPopup(Resources.TXT_BT_NONSERVER.toCharArray(),Popup.ALT_OK, 60, 0, 0, null);
			}
			else
			{
				if (mPopup != null)
				{
					mPopup.dispose();
					mPopup = null;
				}
				char[] localName = Resources.TXT_BT_UNKNOWN.toCharArray();
				try
				{
					localName = LocalDevice.getLocalDevice().getFriendlyName().toCharArray();
				} catch (Throwable t) {}
				MoBoggle.setMoBoggleConnection(mClientConnection);
				MoBoggle.startRemoteGame(false, localName);
			}
		}
		catch (IOException e)
		{
			mPopup = null;
			MoBoggle.showPopup((Resources.TXT_BT_CONN_FAIL + "\n" + e.getMessage()).toCharArray(), Popup.ALT_OK, 60, 0, 0, null);
			try
			{
				mClientConnection.close();
			} catch (IOException e1) {}
		}
	}

	// DiscoveryListener implementation

	// See interface javadoc
	public synchronized void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod)
	{
		// Try to find the name of the device
		char[] name = Resources.TXT_BT_UNKNOWN.toCharArray();
		try
		{
			String nameStr = btDevice.getFriendlyName(false);
			if (nameStr != null)
			{
				name = nameStr.toCharArray();
			}
		}
		catch(Throwable t) {}

		PageItem devItem = new PageItem(name, null, this, null);
		devItem.setProperty(REMOTE_DEVICE, btDevice);
		mDevicePage.addItem(devItem);
	}

	// See interface javadoc
	public void inquiryCompleted(int discType)
	{
		searchingStopped();
	}

	// See interface javadoc
	public void servicesDiscovered(int transID, ServiceRecord[] records) {}

	// See interface javadoc
	public void serviceSearchCompleted(int transID, int respCode) {}
}
