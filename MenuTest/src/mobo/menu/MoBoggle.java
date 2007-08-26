package mobo.menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.*;

import games.bt.*;
import games.gui.*;
import games.res.*;
import mobo.game.*;

public class MoBoggle implements IPopupListener, CommandListener{
	
	protected static TextBox mTextBox;					/** Text box used for collecting user string input */
	protected static MoBoggle mInst;					/** Instance of this class, used as PopupListener or CommandListener */
	private static Popup mPopupCache;					/** Popup instance cache */
	private static PopupCanvas mCurrentCanvas;			/** Current canvas that is displayed */
	private static IGameConnection mRemoteConnection;/** The connection used in remote games */
	private static IPopupListener mPopupListener;		/** Current listener to popups */
	private IStringInputHandler mInputHandler;			/** Handler when user has entered string input */

	protected static int mGameType;						/** Current game type */
		
	protected static final Command OK = new Command(Resources.TXT_OK, Command.OK, 1);				/** Command used during user string input */
	protected static final Command CANCEL =	new Command(Resources.TXT_CANCEL, Command.CANCEL, 1);	/** Command used during user string input */
	
	public static int GAME_TYPE_LOCAL = 0;			/** Represents a game that is only played on this device */
	public static int GAME_TYPE_REMOTE_SERVER = 1;	/** Represents a distributed game where this device acts server */
	public static int GAME_TYPE_REMOTE_CLIENT = 2;	/** Represents a distributed game where this device acts client */
	  
	/**
	 * Initializes this class.
	 * @param bg		The bluegammon midlet.
	 * @param display	The display.
	 */
	public synchronized static void init(MenuTest mb, Display display)
	{
		if (mInst == null)
			mInst = new MoBoggle();				// Creates singleton listeners
		
		Device.init(mb, display);				// Initialize device facade
		MenuCanvas.getInstance().initShow();	// Initialize the menu control canvas
		setCanvas(MenuCanvas.getInstance());
		
		// Initialize generic string input handling
		mTextBox = new TextBox(null, null, 1, TextField.ANY);
		mTextBox.addCommand(OK);
		mTextBox.addCommand(CANCEL);
		mTextBox.setCommandListener(getInputCommandListener());
	}

	// Prevent external construction
	private MoBoggle() {}
	
	/**
	 * Shows a popup on the device display, enabling the user to make a choice
	 * amongst specified alternatives. Selected alternative is reported to
	 * specified listener. The popup has a timeout - if this timeout is reached
	 * the listener receives the specified timeout choice. If the user already has
	 * a popup open, a call to this method makes current popup go away and the
	 * listener to current popup is reported with the timeout choice. After this,
	 * specified popup is presented. Thus, it is the responsibility of the server
	 * or proxy layer to buffer multiple incoming popups. To check if a popup is
	 * currently displayed, use <code>Bluegammon.getCurrentPopup()</code> or
	 * <code>Bluegammon..isShowingPopup()</code>
	 * 
	 * @param text      Character array with text presented in popup.
	 * @param altTexts  Array of character arrays with alternatives presented in popup. If
	 *                  this argument is null, no alternatives are presented.
	 * @param timeOutInSeconds  The timeout in seconds. If this argument is set to zero,
	 *    			  the popup is displayed until the a user makes a choice. If no 
	 * 				  choices are given and the timeout is zero, this popup will not show.
	 * @param defaultChoice  The default selection when the popup appears.
	 * @param timeOutChoice  The choice reported if the popup times out or is overridden by
	 *		          another popup.
	 * @param listener  The listener to this popup.
	 */
	public synchronized static Popup showPopup(char[] text, char[][] altTexts,
			int timeOutInSeconds, int defaultChoice, int timeOutChoice,
			IPopupListener listener)
	{
		if (getCanvas() == null)
			return null;
		
		if (getCanvas().getPopup() != null && getCanvas().getPopup().isActive())
		{
			Popup p = getCanvas().getPopup();
			getPopupListener().selectedChoice(p.getTimeOutChoice(), true);
			p.dispose();
			mPopupCache = p;
		}
		
		Popup p = getPopupInstance();
		p.init(text, altTexts, (byte) timeOutInSeconds, (byte) defaultChoice,
				(byte) timeOutChoice, getPopupListener(), getCanvas().getWidth(),
				getCanvas().getHeight());
		mPopupListener = listener;
		getCanvas().setPopup(p);
		getCanvas().repaint();
		return p;
	}

	/**
	 * Returns true if a popup is currently displayed to the user
	 * @return true if a popup is displayed
	 */
	public synchronized static boolean isShowingPopup()
	{
		if (getCanvas() != null && getCanvas().getPopup() != null && getCanvas().getPopup().isActive())
			return true;
		else
			return false;
	}

	/**
	 * Returns current displayed popup
	 * @return Current popup or null if no popup is displayed
	 */
	public synchronized static Popup getCurrentPopup()
	{
		if (!isShowingPopup())
			return null;
		else
			return getCanvas().getPopup();
	}

	public void selectedChoice(byte choice, boolean timeOut) {}
	
	// CommandListener implementation for input textbox
	public void commandAction(Command c, Displayable d)
	{
		if (d == mTextBox)
		{
			synchronized (getInputLock())
			{
				if (c != CANCEL &&
						mTextBox.getString().length() > 0 &&
						mInputHandler != null)
				{
					final String input = mTextBox.getString();
					new Thread(new Runnable()
							{
						public void run()
						{
							mInputHandler.handleStringInput(input);
						}
							}, "InputHandler").start();
				}
				setCanvas(getCanvas());
			}
		}
	}
	/**
	 * Returns a popup instance from cache.
	 * @return	A popup instance
	 */
	protected synchronized static Popup getPopupInstance()
	{
		if (mPopupCache == null)
		{
			mPopupCache = new Popup();
		}
		return mPopupCache;
	}	
	
	/**
	 * Returns a popuplistener that can be used for dispatching
	 * popup events, used internally only.
	 * @return A popuplistener dispatching events.
	 */
	protected synchronized static IPopupListener getPopupListener()
	{
		return getInstance();
	}
	
	/**
	 * Returns a commandlistener that can be used for dispatching
	 * user string input, used internally only.
	 * @return A command listener for reporting user string input.
	 */
	protected synchronized static CommandListener getInputCommandListener()
	{
		return getInstance();
	}
	
	/**
	 * Returns an instance of <code>MoBoggle</code>,
	 * used internally only for listener implementations.
	 * @return  A MoBoggle instance
	 */
	private static MoBoggle getInstance()
	{
		return mInst;
	}
	
	/**
	 * Returns the locking object of the user input mechanism
	 * 
	 * @return The input monitor
	 */
	protected static Object getInputLock()
	{
		return mTextBox;
	}
	
	/**
	 * Sets specified canvas as current
	 * 
	 * @param c The canvas to set
	 */
	public synchronized static void setCanvas(PopupCanvas c)
	{
		mCurrentCanvas = c;
		Device.getDisplay().setCurrent(c);
		c.repaint();
	}
	
	/**
	 * Returns current canvas
	 * 
	 * @return Current canvas
	 */
	public synchronized static PopupCanvas getCanvas()
	{
		return mCurrentCanvas;
	}
	
	/**
	 * Shuts down the MIDlet.
	 */
	public synchronized static void shutdown()
	{
		if (mPopupCache != null)
		{
			mPopupCache.dispose();
			mPopupCache = null;
		}
		
		mInst = null;
		
		if (mRemoteConnection != null)
		{
			try
			{
				mRemoteConnection.close();
				mRemoteConnection = null;
			} catch (IOException e) {}
		}
		
		System.gc();
		
		Device.getMidlet().notifyDestroyed();
	}	
	
	// Start game code
	
	/**
	 * Sets the connection used in remote game. Must be set before invoking
	 * <code>startRemoteGame</code>
	 * @param conn	The connection used with remote player.
	 */
	public synchronized static void setMoBoggleConnection(IGameConnection conn)
	{
		mRemoteConnection = conn;
	}
	
	/**
	 * Sets current game type, one of <code>GAME_TYPE_LOCAL</code>, 
	 * <code>GAME_TYPE_REMOTE_SERVER</code>, <code>GAME_TYPE_REMOTE_CLIENT</code>.
	 * @param gameType The current game type.
	 */
	protected static void setGameType(int gameType)
	{
		mGameType = gameType;
	}

	/**
	 * Returns current game type, one of <code>GAME_TYPE_LOCAL</code>, 
	 * <code>GAME_TYPE_REMOTE_SERVER</code>, <code>GAME_TYPE_REMOTE_CLIENT</code>.
	 * @return The current game type.
	 */
	public static int getGameType()
	{
		return mGameType;
	}

	/**
	 * Starts a remote game, either as server or as client. This method uses the
	 * connection set in method <code>setMoBoggleConnection</code>. It handshakes
	 * with the other device and starts a new game, or resumes a game if any of the
	 * devices has a saved game. On conflicting preferred settings (color and rules)
	 * the server wins.
	 * 
	 * @param server		True if server, false if client.
	 * @param localName	The name of this player.
	 * @see Bluegammon#setBackgammonConnection(BackgammonConnection)
	 */
	public static void startRemoteGame(boolean server, char[] localName)
	{
		DataInputStream in = mRemoteConnection.getInput();
		DataOutputStream out = mRemoteConnection.getOutput();
		
		// Occurs when connecting to a closed connection
		// i.e. the remote device pressed cancel during connecting phase
		if (in == null || out == null)
			return;

		MoBoggle.showPopup((Resources.TXT_STARTING_GAME).toCharArray(), null, 10, 0, 0, null);
		setGameType(server ? GAME_TYPE_REMOTE_SERVER : GAME_TYPE_REMOTE_CLIENT);
		Handshake handshake = new Handshake();
		boolean fail = false;
		try
		{
			if (server)
				handshake.serverHandshake(in, out, new String(localName));
			else
				handshake.clientHandshake(in, out, new String(localName));
		}
		catch (Throwable t)
		{
			fail = true;
			MoBoggle.showPopup((Resources.TXT_HANDSHAKE_FAIL).toCharArray(), Popup.ALT_OK, 0, 0, 0, null);
			return;
		}

		if (!fail)
		{
			// Create local player and add listener
			Player p1 = new LocalPlayer(Device.getDeviceId(), localName, BoardMediator.getCanvas());
			PlayerListenerProxy proxy = new PlayerListenerProxy(out);
			p1.addListener(proxy);
			
			// Craeate remote player
			Player p2 = new RemotePlayer(handshake.getRemoteId(), handshake.getRemoteName(), in);
			
			// Create game board
			BoardMediator.init(p1, p2);
			setCanvas(BoardMediator.getCanvas());
			
			// Start music
			//Audio.stopSound(Audio.MUSIC);

			if (server)
			{
				Popup p = MoBoggle.getCurrentPopup();
				if (p != null) 
					p.dispose();
			}
		}
		getCanvas().repaint();
	}

	/**
	 * Exits a game.
	 */
	public synchronized static void exitGame()
	{
		// Is it a local or a remote game
		if (getGameType() == GAME_TYPE_LOCAL)
		{
			if (!BoardMediator.isGameFinished())
			{
				// Local game is not finished, save it
			}
			else
			{
				// Local game is finished, remove any local saved game
			}
		}
		else if (getGameType() == GAME_TYPE_REMOTE_CLIENT || getGameType() == GAME_TYPE_REMOTE_SERVER)
		{
			//BoardMediator.forceGameFinished();
			if (mRemoteConnection != null)
			{
				try
				{
					mRemoteConnection.close();
					mRemoteConnection = null;
				} catch (IOException e) {}
			}
		}
		
		// Show menu
		MenuCanvas.getInstance().initShow();
		setCanvas(MenuCanvas.getInstance());
	}
}
