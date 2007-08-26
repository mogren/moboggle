package mobo.menu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import games.res.Resources;
import games.gui.*;

/**
 * A page that presents nearby devices, and gives
 * feedback when search is active. Presentation and
 * callhandler to the underlying workflow, an instance of 
 * <code>BluetoothClientWorkflow</code>.
 * 
 * @see bluegammon.gui.BluetoothClientWorkflow
 * @author PK CM
 */
public class BluetoothDevicePage extends MenuPage implements IFocusablePage, CommandListener, Runnable
{
	
	protected static final Command REFRESH = new Command(Resources.TXT_REFRESH, Command.OK, 1);	/** The refresh command of this page */
	protected PopupCanvas mCanvas;				/** Our canvas */
	protected SoftButtonControl mSoftButtons;	/** Our softbuttons */
	protected Menu mMenu;						/** The menu */
	protected Command mOldCommand;				/** Command to set when leaving page */
	protected CommandListener mOldListener;		/** Listener to set when leaving page */
	protected boolean mSearching = false;		/** Flag indicating if we are looking for devices */
	protected int mBTImage = 0;					/** Current search title image index */	
	protected Image[] mImages;					/** Search title images */
	
	/**
	 * The workflow for connecting to a client that is selected from
	 * this page.
	 */
	protected BluetoothClientWorkflow mClientWorkflow;

	/**
	 * Creates the nearby bluetooth devices page.
	 * @param title		The title of the page.
	 * @param menu		The menu this page belongs to.
	 * @param canvas		The canvas this page is drawed upon.
	 * @param softButtons	The softbutton control used in above canvas.
	 */
	public BluetoothDevicePage(char[] title, Menu menu, PopupCanvas canvas, SoftButtonControl softButtons)
	{
		super(title, null);
		mCanvas = canvas;
		mSoftButtons = softButtons;
		mMenu = menu;
		mImages = new Image[2];
		mImages[0] = Resources.getImage(Resources.IMG_BT1);
		mImages[1] = Resources.getImage(Resources.IMG_BT2);
		new Thread(this, "BTAnim").start();
	}

	/**
	 * Adds an item and repaints.
	 */
	public void addItem(PageItem item)
	{
		super.addItem(item);
		mCanvas.repaint();
	}

	/**
	 * Registers the behaviour to enable when user
	 * wants to connect as client to a device listed in
	 * this page.
	 * 
	 * @param bch		The workflow for connecting to a device as client.
	 */
	public void setClientWorkflow(BluetoothClientWorkflow bch)
	{
		mClientWorkflow = bch;
	}

	/**
	 * Returns whether this page displays the icon
	 * indicating device search or not.
	 * @return	true for searching, false otherwise.
	 */
	public synchronized boolean isSearching()
	{
		return mSearching;
	}

	/**
	 * Enables or disables the searching
	 * @param on
	 */
	public synchronized void setSearching(boolean on)
	{
		mSoftButtons.enable(REFRESH, !on);
		mSearching = on;
		mCanvas.repaint();
		notifyAll();
	}

	// FocusablePage impl

	/**
	 * Called when this page shows up. Sets specific softbuttons.
	 */
	public void onEnter()
	{
		mOldListener = mSoftButtons.getCommandListener();
		mOldCommand = mSoftButtons.getRightCommand();
		mSoftButtons.setCommandListener(this);
		mSoftButtons.setRightCommand(REFRESH);
		mSoftButtons.enable(REFRESH, !mSearching);
	}

	/**
	 * Called when this page is hidden for something else.
	 * Resets softbuttons.
	 */
	public void onLeave()
	{
		mSoftButtons.setRightCommand(mOldCommand);
		mSoftButtons.setCommandListener(mOldListener);
	}

	// CommandListener impl

	/**
	 * Called from softbutton control.
	 */
	public void commandAction(Command c, Displayable d)
	{
		if (c == REFRESH)
			mClientWorkflow.refresh();
		else
			mMenu.goBack();
	}

	// Runnable impl

	/**
	 * Bluetooth search animation.
	 */
	public void run()
	{
		synchronized (this)
		{
			while (true)
			{
				while (!mSearching)
				{
					setTitleImage(null);
					mCanvas.repaint();
					try
					{
						wait();
					} catch (InterruptedException e) { }
				}
				while (mSearching)
				{
					setTitleImage(mImages[mBTImage++]);
					mBTImage %= mImages.length;
					mCanvas.repaint();
					try
					{
						wait(500);
					} catch (InterruptedException e) { }
				}
			}
		}
	}
}