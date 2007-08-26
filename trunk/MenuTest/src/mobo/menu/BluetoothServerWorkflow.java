package mobo.menu;

import java.io.IOException;
import javax.bluetooth.LocalDevice;

import games.res.Resources;
import games.gui.*;
import mobo.game.MoBoggleBTConnection;

public class BluetoothServerWorkflow implements IItemAction, IPopupListener {

	protected MoBoggleBTConnection mServerConnection = null;	/** The connection */
	protected Popup mPopup;										/** Reference to popup we need to close on successful connection */

	/**
	 * Called when user sets up a server.
	 */
	public void itemAction(MenuPage page, PageItem item)
	{
		synchronized (this)
		{
			mServerConnection = new MoBoggleBTConnection();
		}
		mPopup = MoBoggle.showPopup((Resources.TXT_BT_SERVER_OPENED).toCharArray(), Popup.ALT_CANCEL, 0, 0, 0, this);
		try
		{
			// If there already is a workflow waiting for client, just show popup.
			// Another thread is still waiting for a client somewhere in the dark,
			// and will handle any possible client connection.
			if (mServerConnection.isAwaitingClient())
			{
				// Just tell the server that we will accept a client if it arrives.
				// Non blocking call.
				mServerConnection.waitForClient();
			}
			else
			{
				// Wait for client, blocking call.
				mServerConnection.waitForClient();
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
				if (!mServerConnection.isClosed())
				{
					MoBoggle.setMoBoggleConnection(mServerConnection);
					MoBoggle.startRemoteGame(true, localName);
				}
			}
		}
		catch (IOException ioe)
		{
			mPopup = null;
			MoBoggle.showPopup((Resources.TXT_BT_SERVER_FAILURE + "\n" + ioe.getMessage()).toCharArray(), Popup.ALT_OK, 60, 0, 0, null);
			try
			{
				mServerConnection.close();
			} catch (IOException e) {}
			ioe.printStackTrace();
		}
	}

	/**
	 * Called when user cancels a server setup.
	 */
	public void selectedChoice(byte choice, boolean timeOut)
	{
		if (mPopup != null)
		{
			mPopup.dispose();
			mPopup = null;
		}
		synchronized (this)
		{
			if (mServerConnection != null)
				mServerConnection.pretendServerClose();
		}
	}
}
