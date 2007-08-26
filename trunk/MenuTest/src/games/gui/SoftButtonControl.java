package games.gui;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import games.res.*;

/**
 * A custom item for softbuttons. Has functionality for
 * setting one <code>Command</code> per softbutton. This
 * also binds the backbutton if any of the command's type
 * is <code>Command.BACK</code>.
 * 
 * @author PK CM
 */
public class SoftButtonControl
{
	protected Displayable mDisplayable;				/** Displayable that is used for drawing this control */
	protected Command mLeftCommand;					/** Left command */
	protected Command mRightCommand;				/** Right command */
	protected Command mBackCommand;					/** Back command */
	protected CommandListener mListener;			/** Command listener */
	protected boolean mLeftCommandEnabled = true;	/** Enabled/disabled flag for left command */	
	protected boolean mRightCommandEnabled = true;	/** Enabled/disabled flag for right command */		
	protected char[] mLeft;							/** String of left softbutton */	
	protected char[] mRight;						/** String of right softbutton */	
	protected Font mFont;							/** Font used when drawing softbutton strings */	
	protected int mMaxWidth;						/** Biggest text width amongst the commands */
	protected int[] mTranspBuf;
	
	/**
	 * Initializes this softbutton control.
	 * 
	 * @param d		The displayable that this control draw on.
	 * @param font	The font used for rendering softbuttons.
	 * @param leftCommand		The left softbutton command or null.
	 * @param rightCommand	The right softbutton command or null.
	 */
	public void init(Displayable d, Font font, Command leftCommand, Command rightCommand)
	{
		mDisplayable = d;
		mFont = font;
		setRightCommand(rightCommand);
		setLeftCommand(leftCommand);
	}
	
	/**
	 * Returns the command listener.
	 * @return	The command listener.
	 */
	public CommandListener getCommandListener()
	{
		return mListener;
	}
	
	/**
	 * Sets the commandlistener that will be reported upon
	 * softkey presses.
	 * @param listener	The listener.
	 */
	public void setCommandListener(CommandListener listener)
	{
		mListener = listener;
	}
	
	/**
	 * Returns the command assigned to left softbutton.
	 * @return The left comamand. 
	 */
	public Command getLeftCommand()
	{
		return mLeftCommand;
	}
	
	/**
	 * Sets the left softbutton command.
	 * @param c	The command.
	 */
	public void setLeftCommand(Command c)
	{
		if (mBackCommand == mLeftCommand)
		{
			mBackCommand = null;
		}
		mLeftCommand = c;
		if (mLeftCommand != null)
		{
			mLeft = mLeftCommand.getLabel().toCharArray();
			if (mLeftCommand.getCommandType() == Command.BACK)
			{
				mBackCommand = c;
			}
			enable(c, true);
		}
		calcWidth();
	}
	
	/**
	 * Returns the command assigned to right softbutton.
	 * @return The right comamand. 
	 */
	public Command getRightCommand()
	{
		return mRightCommand;
	}
	
	/**
	 * Sets the right softbutton command.
	 * @param c	The command.
	 */
	public void setRightCommand(Command c)
	{
		if (mBackCommand == mRightCommand)
		{
			mBackCommand = null;
		}
		mRightCommand = c;
		if (mRightCommand != null)
		{
			mRight = mRightCommand.getLabel().toCharArray();
			if (mRightCommand.getCommandType() == Command.BACK)
			{
				mBackCommand = c;
			}
			enable(c, true);
		}
		calcWidth();
	}
	
	/**
	 * Enables/disables a command.
	 * @param c 			The command.
	 * @param enable		True for enable, false for disable.
	 */
	public void enable(Command c, boolean enable)
	{
		if (c == mLeftCommand)
		{
			mLeftCommandEnabled = enable;
		}
		if (c == mRightCommand)
		{
			mRightCommandEnabled = enable;
		}
	}
	
	/**
	 * Call this from the displayable when a key is pressed to
	 * activate this control.
	 * 
	 * @param keyCode	The keycode reported in <code>Displayable</code>'s
	 * 				<code>keyPressed</code> method.
	 */
	public void keyPressed(int keyCode)
	{
		if (keyCode == Device.KEYCODE_LEFT_SOFT)
		{
			if (mLeftCommand != null && mListener != null && mLeftCommandEnabled)
			{
				mListener.commandAction(mLeftCommand, mDisplayable);
			}
		}
		else if (keyCode == Device.KEYCODE_RIGHT_SOFT)
		{
			if (mRightCommand != null && mListener != null && mRightCommandEnabled)
			{
				mListener.commandAction(mRightCommand, mDisplayable);
			}
		}
		else if (keyCode == Device.KEYCODE_BACK)
		{
			if (mBackCommand != null && mListener != null && 
					(mBackCommand == mLeftCommand && mLeftCommandEnabled ||
							mBackCommand == mRightCommand && mRightCommandEnabled))
			{
				mListener.commandAction(mBackCommand, mDisplayable);
			}
		}
	}
	
	/**
	 * Call this from the displayable a repaint is necessary to
	 * draw this control
	 * 
	 * @param g	The graphics context reported in <code>Displayable</code>'s
	 * 			<code>paint</code> method.
	 */
	public void paint(Graphics g)
	{
		int w = mDisplayable.getWidth();
		int h = mDisplayable.getHeight();
		g.setFont(mFont);
		if (mLeftCommand != null)
		{
			paintCommand(g, mLeft, w, h, mLeftCommandEnabled, false);
		}
		if (mRightCommand != null)
		{
			paintCommand(g, mRight, w, h, mRightCommandEnabled, true);
		}
	}
	
	/**
	 * Calculate softbutton width.
	 */
	protected void calcWidth()
	{
		int twl = mLeft == null ? 0: mFont.charsWidth(mLeft,0,mLeft.length);
		int twr = mRight == null ? 0: mFont.charsWidth(mRight,0,mRight.length);
		int mw = Math.max(twr, twl);
		if (mMaxWidth != mw)
		{
			mMaxWidth = mw;
			recalcTransparantBuffer();
		}
		
	}
	
	/**
	 * Paint a softbutton command.
	 * @param g			Graphics context to draw to.
	 * @param text		Text of command.
	 * @param w			Width of softbutton.
	 * @param h			Height of softbutton.
	 * @param enabled		True if softbutton is enabled, false otherwise.
	 * @param rightAlign	True if softbutton is to the right, false for left.
	 */
	protected void paintCommand(Graphics g, char[] text,
			int w, int h, boolean enabled, boolean rightAlign)
	{
		int textH = mFont.getHeight();
		int x = 0;
		if (rightAlign)
		{
			x = w - mMaxWidth - 2;
		}
		g.drawRGB(mTranspBuf, 0, mMaxWidth+2,
				x, h-textH-1, mMaxWidth+2, textH+1, true);
		g.setColor(enabled ? Resources.COL_SOFT_COMMAND : Resources.COL_SOFT_DISABLED);
		x += mMaxWidth / 2;
		x += rightAlign ? 2 : 1;
		g.drawChars(text,0,text.length, x, h, Graphics.BOTTOM | Graphics.HCENTER);
	}
	
	/**
	 * Calculate transparant background.
	 */
	protected void recalcTransparantBuffer()
	{
		mTranspBuf = new int[(mMaxWidth+2) * (mFont.getHeight()+1)];
		for (int i = mMaxWidth+2; i < mTranspBuf.length; i++)
		{
			mTranspBuf[i] = Resources.COL_SOFT_BG;
		}
		for (int i = 0; i < mMaxWidth+2; i++)
		{
			mTranspBuf[i] = Resources.COL_SOFT_BORDER;
		}
		for (int i = 0; i < mFont.getHeight()+1; i++)
		{
			mTranspBuf[(i *  (mMaxWidth+2))] = Resources.COL_SOFT_BORDER;
			mTranspBuf[(i *  (mMaxWidth+2)) +  mMaxWidth+1] = Resources.COL_SOFT_BORDER;
		}
	}
}
