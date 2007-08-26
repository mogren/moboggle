package games.res;

import javax.microedition.lcdui.Display;
import mobo.menu.MenuTest;	/** Import midlet package*/

public final class Device {

	protected static Display mDisplay;	/** The display */
	protected static MenuTest mMidlet;	/** The midlet instance */

	public static final int KEYCODE_BACK = -11;
	public static final int KEYCODE_LEFT_SOFT = -6;
	public static final int KEYCODE_RIGHT_SOFT = -7;
	
	public static void init(MenuTest mb, Display display) {
		mMidlet = mb;
		mDisplay = display;
	}
	
	/**
	 * Returns the midlet instance.
	 * @return	The midlet.
	 */
	public static MenuTest getMidlet()
	{
		return mMidlet;
	}

	/**
	 * Returns the display of this midlet.
	 * @return	The display.
	 */
	public static Display getDisplay()
	{
		return mDisplay;
	}

	/**
	 * Returns a unique id for this device. The K750i supports
	 * the systemproperty "com.sonyericsson.imei" giving the IMEI number. This is used for
	 * calculating a unique id. If this property does not exist, current time is
	 * used for id creation instead.
	 * The id is cached in RMS and is thus only calculated once.
	 * 
	 * @return	Device identifyer
	 */
	public static int getDeviceId()
	{
		int id;
		//int id = RmsFacade.getInt(Bluegammon.DEVICE_ID);
		//if (id == 0)
		//{
		String idStr = System.getProperty("com.sonyericsson.imei");
		if (idStr == null)
		{
			idStr = Long.toString(System.currentTimeMillis());
		}
		id = calcIdFromString(idStr);
		//RmsFacade.setInt(Bluegammon.DEVICE_ID, id);
		//}
		return id;
	}

	/**
	 * Calculates an indentifer from a string.
	 * @param s	The string
	 * @return	The id
	 */
	protected static int calcIdFromString(String s)
	{
		int res = 0;
		// Left shift, XOR the char val, XOR shift overflow bit
		for (int i = s.length()-1; i >= 0; i--)
			res = ((res << 1) ^ s.charAt(i)) ^ ((res & 0x80000000) != 0 ? 1 : 0);

		return res;
	}
}
