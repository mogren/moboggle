package mobo.menu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import games.res.*;
import games.gui.*;

public class MenuCanvas extends PopupCanvas implements IMenuListener, IItemAction, CommandListener{
	
	protected static MenuCanvas mInst = null;	/** Singleton instance */
	protected Menu mMenu;						/** The menu instance */
	protected SoftButtonControl mSoftButtons;	/** Softbuttons of the backgammon menu canvas */
	protected Image mBackground;				/** The background image */
	
	protected static final int ITEM_HELP = 0;		/** Item property key containing help text */ 
	protected static final int ACTION_QUIT = 1;		/** Action key for quitting midlet */
	protected static final int ACTION_ABOUT = 2;	/** Action key for showing about box */
	
	protected static final Command CMD_BACK = new Command(Resources.TXT_BACK, Command.BACK, 1);	/** Softbutton back command */
	protected static final Command CMD_HELP = new Command(Resources.TXT_HELP, Command.ITEM, 1);	/** Softbutton help command */
	
	protected MenuCanvas() {
		mBackground = Resources.getImage(Resources.IMG_BG);
		mSoftButtons = new SoftButtonControl();
		
		// Create menu
		MenuPage mainPage = new MenuPage(Resources.TXT_TITLE.toCharArray(), null);
		mMenu = new Menu(mainPage, this, new ExtendedMenuPainter(getWidth()));
		
	    // Menu titles
	    MenuPage CMPage =  new MenuPage("Meny titel".toCharArray(), null);
	    MenuPage btPage =  new MenuPage("Bluetooth".toCharArray(), null);
	    
	    // Menu main page
	    PageItem BTItem = new PageItem("Play via bluetooth".toCharArray(), null, null, btPage);
	    mainPage.addItem(BTItem);
	    PageItem CMItem = new PageItem("CM-Page".toCharArray(), null, null, CMPage);
	    mainPage.addItem(CMItem);
	    mainPage.addItem( new PageItem("About".toCharArray(), null, this, null, ACTION_ABOUT));
	    mainPage.addItem( new PageItem("Quit".toCharArray(), null, this, null, ACTION_QUIT));
	    
	    // CM Page
	    CMPage.addItem( new PageItem("Undermeny..".toCharArray(), null, this, null, ACTION_ABOUT));

	    // Menu BT Page
	    PageItem btServerItem =	new PageItem("Setup server".toCharArray(), null, new BluetoothServerWorkflow(), null);
	    BluetoothDevicePage bdp = new BluetoothDevicePage("Devices".toCharArray(), mMenu, this, mSoftButtons);
	    BluetoothClientWorkflow bch = new BluetoothClientWorkflow(bdp);
	    bdp.setClientWorkflow(bch);
	    PageItem btClientItem = new PageItem("Connect as client".toCharArray(), null, bch, bdp);

	    btPage.addItem(btServerItem);
	    btPage.addItem(btClientItem);

	    setFullScreenMode(true);

	    // Initiate softbuttons
	    mSoftButtons.init(this, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL), CMD_BACK, CMD_HELP);
	    mSoftButtons.setCommandListener(this);
	    mSoftButtons.enable(CMD_HELP, false);
	    
	    // Setup menu
	    int menuPadding = 16;
	    mMenu.setLocation(0, menuPadding);
	    mMenu.setDimensions(getWidth(), getHeight() - menuPadding * 2);
	    mMenu.setFrameData(10, 20);
	    mMenu.setListener(this);
	    mMenu.start();	    
	}
	
	/**
	 * Creates the menu and initiates the gui controls
	 */
	public static MenuCanvas getInstance() {
		if(mInst == null)
			mInst = new MenuCanvas();
		return mInst;
	}
	
	/**
	 * Initializes the states of items and
	 * starts the muzak, called on an already
	 * initialized menu when it is focused again.
	 */
	public void initShow()
	{
		PageItem selItem = mMenu.getSelectedItem();
		if (selItem != null)
		{
			Object helpTxt = selItem.getProperty(ITEM_HELP);
			mSoftButtons.enable(CMD_HELP, helpTxt != null);
		}
	}
	
	/**
	 * Paints the menu, the possible popup, and the softbuttons.
	 * @param g	The graphics context to draw on.
	 */
	protected void paint(Graphics g)
	{
		g.setColor(0x888833);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(mBackground, getWidth() / 2, getHeight() / 2, Graphics.VCENTER | Graphics.HCENTER);
		
		mMenu.paint(g);
		mSoftButtons.paint(g);
		if (getPopup() != null && getPopup().isActive())
		{
			getPopup().paint(g);
		}
	}
	
	public void newPage(MenuPage fromPage, MenuPage toPage, boolean back) {
		mSoftButtons.enable(CMD_BACK, toPage != mMenu.getStartPage());
		if (fromPage instanceof IFocusablePage)
			((IFocusablePage)fromPage).onLeave();
		
		if (toPage instanceof IFocusablePage)
			((IFocusablePage)toPage).onEnter();
	}
	
	public void itemSelected(MenuPage page, PageItem oldItem, PageItem newItem) {
		if (newItem != null)
		{
			Object helpTxt = newItem.getProperty(ITEM_HELP);
			mSoftButtons.enable(CMD_HELP, helpTxt != null);
		}
	}
	
	public void actionCalled(MenuPage page, PageItem item, IItemAction action) {}
	
	public void transitionStarted(MenuPage fromPage, MenuPage toPage, long delay, int frames, boolean back) {}
	
	public void transitionStopped(MenuPage fromPage, MenuPage toPage) {}
	
	/**
	 * Called when user presses a key. Dispatches the keypress to
	 * possible popup, menu and softbuttons.
	 * @param keyCode	The code of the key that is pressed.
	 */
	protected void keyPressed(int keyCode)
	{
		if (getPopup() != null && getPopup().isActive())
		{
			getPopup().keyPressed(keyCode, getGameAction(keyCode));
			repaint();
		}
		else
		{
			mMenu.keyPressed(keyCode);
			mSoftButtons.keyPressed(keyCode);
		}
	}
	
	public void commandAction(Command c, Displayable d)
	{
		if (c == CMD_BACK)
		{
			mMenu.goBack();
		}
		else if (c == CMD_HELP)
		{
			PageItem item = mMenu.getSelectedItem();
			if (item != null)
			{
				char[] helpTxt = (char[])item.getProperty(ITEM_HELP);
				if (helpTxt != null)
				{
					MoBoggle.showPopup(helpTxt, Popup.ALT_OK, 0, 0, 0, null);
				}
			}
		}
	}
	
	/**
	 * ItemAction implementation, called from items in
	 * this menu. Instead of creating one class per action,
	 * we centralize the action behaviour here. Each action
	 * is identified by its id.
	 * 
	 * @param page	The page from which the action was called
	 * @param item	The item to which the action belongs 
	 */
	public void itemAction(MenuPage page, PageItem item)
	{
		int id = item.getId();
		switch(id)
		{
		case ACTION_QUIT:
			MoBoggle.shutdown();
			break;
		case ACTION_ABOUT:
			MoBoggle.showPopup(Resources.TXT_ABOUT.toCharArray(), Popup.ALT_OK, 0, 0, 0, null);
			break;
		}
	} 
		
	/**
	 * Special painter that also paints <code>GameRecordPageItem</code>s and
	 * adds special effects for selected items.
	 */
	static class ExtendedMenuPainter extends DefaultMenuPainter
	{
		protected int[] mRGBData;
		protected int mCanvasWidth;
		
		public ExtendedMenuPainter(int canvasWidth)
		{
			// Create selected item rgb buffer
			mCanvasWidth = canvasWidth;
			mRGBData = new int[mCanvasWidth * 4];
			int bgcol = Resources.COLOR_ITEM_CUR; //0x880000;
			for (int i = 0; i < mCanvasWidth; i++)
			{
				double alpha = (double)Math.abs(i - mCanvasWidth / 2) / (double)mCanvasWidth;
				int col = bgcol | (255 - (int)(500 * alpha) << 24);
				mRGBData[i]                     = col;
				mRGBData[i + mCanvasWidth    ] = col;
				mRGBData[i + mCanvasWidth * 2] = col;
				mRGBData[i + mCanvasWidth * 3] = col;
			}
		}
		
		// Extend PageItem.paintItem to handle GameRecordPageItems
		protected void paintItem(Graphics g, PageItem item, boolean selected, int x, int y, int w, int iMaxW, boolean to, boolean from)
		{
			if (selected)
			{
				int itemH = getItemHeight(item);
				// draw transparent background on selected item
				for (int by = y; by < y + itemH; by += 4)
				{
					g.drawRGB(mRGBData, 0, mCanvasWidth, x - mCanvasWidth/2, by, mCanvasWidth, Math.min(4, y + itemH - (by - y)), true);
				}	
			}
			super.paintItem(g, item, selected, x, y, w, iMaxW, to, from);
		}		
	} // End of Painter
}
