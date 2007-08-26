package games.res;

import javax.microedition.lcdui.Image;

public class Resources {
	
	public static final String TXT_OK = "Ok";
	public static final String TXT_CANCEL = "Cancel";
	public static final String TXT_REFRESH = "Refresh";
	public static final String TXT_BACK = "Back";
	public static final String TXT_HELP = "Help";
	public static final String TXT_ABOUT = "MoBoggle about text. Long text here should be automatically line wrapped. едц";
	public static final String TXT_YES = "Yes";
	public static final String TXT_NO = "No";
	public static final String TXT_STARTING_GAME = "Starting a MoBoggle game";
	public static final String TXT_HANDSHAKE_FAIL = "Handshake failed";
	public static final String TXT_QUIT = "Quit";
	public static final String TXT_QUIT_REMOTE = "Quit remote";
	public static final String TXT_GIVE_UP = "Give up";
	public static final String TXT_REMOTE_GAVE_UP = "Remote gave up";
			
	public static final String TXT_BT_REMOTE_QUIT = "Remote quit";
	public static final String TXT_BT_CONN_LOST = "Lost connection";
	public static final String TXT_BT_SERVER_OPENED = "Server opened";
	public static final String TXT_BT_UNKNOWN = "Unknown";
	public static final String TXT_BT_SERVER_FAILURE = "Server failure";
	public static final String TXT_BT_DISC_FAIL = "Bluetooth discovery failed";
	public static final String TXT_BT_NO_DEVICES = "No devices found";
	public static final String TXT_BT_CONNECT = "Connect";
	public static final String TXT_BT_NONSERVER = "Nonserver";
	public static final String TXT_BT_CONN_FAIL = "Connection failed";
	
	public static final String IMG_BG = "mobobackgr2.png";
	public static final String IMG_BT1 = "bt1.png";
	public static final String IMG_BT2 = "bt2.png";
	public static final String TXT_TITLE = "Huvudmeny";
	
	public static final int COLOR_TITLE_FG = 0xffffff;	/** Page title color  			0xffffff*/
	public static final int COLOR_TITLE_BG = 0x00aa55;	/** Page title back color  		0x880000*/ 
	public static final int COLOR_ITEM_LBL = 0xcccc33;	/** Enabled item label color 	0x00ffff*/ 
	public static final int COLOR_ITEM_DIS = 0x555599;	/** Disabled item label color 	0x888888*/
	public static final int COLOR_ITEM_SEL = 0xbbccff;	/** Selected item label color 	0xffffff*/
	public static final int COLOR_ITEM_CUR = 0x220077;	/** Cursor..	*/
	
	public static final int COL_SOFT_BORDER = 0x5500cc;
	public static final int COL_SOFT_BG = 0x220077;
	public static final int COL_SOFT_COMMAND = 0xcccc33;
	public static final int COL_SOFT_DISABLED = 0x555599;
	
	public static final int COL_POPUP_BORDER = 0x5500cc;
	public static final int COL_POPUP_BG = 0xcc000044;
	public static final int COL_POPUP_TXT = 0xcccc33;
	public static final int COL_POPUP_ALTCOL = 0x555599;
	public static final int COL_POPUP_SELECTEDALTCOL = 0xcccc33;
	
	/**
	 * Returns specified image.
	 * @param fileName  File name.
	 * @return    		An image.
	 */
	public static synchronized Image getImage(String fileName)
	{
		Image img = null;
		try {
			img =  Image.createImage("/" + fileName);
		}catch (Exception e){}
		return img;
	}
}
