package games.gui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import games.res.*;

/**
 * <p>
 * The <code>Popup</code> represents a popup with text and zero or many
 * alternatives that user can select among. A popup can be shown for a 
 * specific amount of time or forever, the latter case requiring a user to close
 * the popup. The choice of the alternatives can be reported by implementing
 * a <code>PopupListener</code>
 * </p><p>
 * This class contains all functionality of a popup, i.e. interaction,
 * graphics and callback.
 * </p>
 * 
 * @author PK, CM
 */
public class Popup implements Runnable
{
	  public static final char[][] ALT_OK;				/** Preset alternative containing OK */
	  public static final char[][] ALT_CANCEL;			/** Preset alternative containing CANCEL */
	  public static final char[][] ALT_YES_NO;			/** Preset alternatives containing YES and NO */
	  public static final char[][] ALT_OK_CANCEL;		/** Preset alternatives containing OK and CANCEL */
	  public static final char[][] ALT_YES_NO_CANCEL;	/** Preset alternatives containing YES, NO, and CANCEL */

	  // Setup preset alternatives
	  static
	  {
	    ALT_OK = new char[1][];
	    ALT_OK[0] = Resources.TXT_OK.toCharArray();
	    ALT_CANCEL = new char[1][];
	    ALT_CANCEL[0] = Resources.TXT_CANCEL.toCharArray();
	    ALT_YES_NO = new char[2][];
	    ALT_YES_NO[0] = Resources.TXT_YES.toCharArray();
	    ALT_YES_NO[1] = Resources.TXT_NO.toCharArray();
	    ALT_OK_CANCEL = new char[2][];
	    ALT_OK_CANCEL[0] = ALT_OK[0];
	    ALT_OK_CANCEL[1] = ALT_CANCEL[0];
	    ALT_YES_NO_CANCEL = new char[3][];
	    ALT_YES_NO_CANCEL[0] = ALT_YES_NO[0];
	    ALT_YES_NO_CANCEL[1] = ALT_YES_NO[1];
	    ALT_YES_NO_CANCEL[2] = ALT_CANCEL[0];
	  }
		
	protected char[] mText;						/** The text to show in the popup */	
	protected byte mAlternatives;				/** Number of alternatives to select in popup */	
	protected char[][] mAltTexts;				/** Array of texts as chararrays in alternatives */	
	protected byte mTimeOut;					/** Time out in seconds */	
	protected byte mTimeOutAlt;					/** Alternative reported back if timeout is reached */	
	protected byte mCurAlt;						/** Current alternative index */	
	protected IPopupListener mListener;			/** Listener to this popup */	
	protected volatile boolean mActive = true;	/** Flag indicating if popup is active */	
	protected int mWidth;						/** Width of popup */	
	protected int mHeight;						/** Height of popup */
	/**
	 * Indices indicating where to break the text organized as [line][0 = start
	 * char offset | 1 = char len]
	 */
	protected int[][] mBreakTextData;				
	protected int mVisibleLines;				/** Number of visible text lines in popup */
	protected int mCurLine;						/** Current line offset */	
	protected int mMaxLine;						/** Maximum line offset */	
	protected int mYOffset;						/** Horizontal coordinate offset of text */
	protected long mEndTime;					/** The life time of this popup */
	
	// Graphical data
	protected Font mFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL);
	protected int mFontHeight = mFont.getHeight();
	protected int mBorderColor = Resources.COL_POPUP_BORDER; // 0xdd0000;
	protected int mBackgroundColor = Resources.COL_POPUP_BG; //0xcc440000;
	protected int mTextColor = Resources.COL_POPUP_TXT; //0xffffff;
	protected int mAlternativeColor = Resources.COL_POPUP_ALTCOL; //0xff2200;
	protected int mSelectedAlternativeColor = Resources.COL_POPUP_SELECTEDALTCOL; //0xffffff;
	protected static int[] mRGBData;
	
	/**
	 * Space between actual popup graphics and the rectangle reported in method <code>init</code>
	 */
	protected static final int OFFSET_POPUP = 8;	
	protected static final int OFFSET_TEXT = 2;		/** Space between popup edges and text content */	
	protected static final int OFFSET_ALT = 4;		/** Space between alternative texts */	
	protected static final int SB_WIDTH = 5;		/** Scrollbar width */
	/** Textbreak constants */
	protected static final char[] TEXTBREAKS = { ' ', '?', ';', ',', '.', '!',':', '-', '=', '(', ')', '[', ']' };
	protected static final char NEWLINE = '\n';		/** New line constant */
	
	/**
	 * Creates an uninitialized popup. Call <code>init</code> to setup
	 * this popup instance. This constructor is used for instance cache
	 * functionality.
	 */
	public Popup() {}
	
	/**
	 * Initializes this popup.
	 * 
	 * @param text       The text to show in popup.
	 * @param altTexts   The alternatives to select among or null if no choices.
	 * @param timeOut    Time out for this popup in seconds (0 means no timeout).
	 * @param defaultAlt Index of default alternative.
	 * @param timeOutAlt Alternative index reported on time out.
	 * @param listener   The popuplistener being reported on selection or timeout or null
	 *                   if no listener.
	 * @param width      Canvas width.
	 * @param height     Canvas height.
	 */
	public void init(char[] text, char[][] altTexts, byte timeOut,
			byte defaultAlt, byte timeOutAlt, IPopupListener listener, int width,
			int height)
	{
		// Set parameters
		mText = text;
		mAltTexts = altTexts;
		if (altTexts != null)
		{
			mAlternatives = (byte) altTexts.length;
		}
		else
		{
			mAlternatives = 0;
		}
		mTimeOut = timeOut;
		mTimeOutAlt = timeOutAlt;
		mListener = listener;
		mCurAlt = defaultAlt;
		mWidth = width - (OFFSET_POPUP << 1);
		mHeight = height - (OFFSET_POPUP << 1);
		mActive = true;
		if (mTimeOut > 0)
		{
			// Set timeout
			mEndTime = System.currentTimeMillis() + (mTimeOut * 1000);
		}
		else if (mAlternatives > 0)
		{
			// No timeout
			mEndTime = 0;
		}
		else
		{
			// This should never happen - a popup with no alternatives and no timeout
			mEndTime = System.currentTimeMillis();
		}
		
		mVisibleLines =
			Math.max(1, ((mHeight - (OFFSET_TEXT << 1)) / mFontHeight) - 1);
		int w = mWidth - (OFFSET_TEXT << 1) - (SB_WIDTH << 1);
		mCurLine = 0;
		
		mBreakTextData = breakString(text, w);
		
		// Calculate height
		mYOffset = 0;
		mMaxLine = mBreakTextData.length - mVisibleLines + 1;
		if (mBreakTextData.length < mVisibleLines)
		{
			int newH = mBreakTextData.length * mFontHeight;
			if (mAlternatives > 0)
				newH += mFontHeight;
			newH += OFFSET_TEXT + OFFSET_ALT;
			mYOffset = (mHeight - newH) >> 1;
			mHeight = newH;
		}
		
		// Create transparent rgb buffer if needed (8 lines)
		if (mRGBData == null || mRGBData.length != mWidth * 8)
		{
			mRGBData = new int[mWidth * 8];
			for (int i = 0; i < mRGBData.length; i++)
			{
				mRGBData[i] = mBackgroundColor;
			}
		}
		
		// Start poll thread
		new Thread(this, "PopupPoll").start();
	}
	
	/**
	 * Breaks specified character array and returns a break matrix by line,
	 * organized as [line][0 | 1] where 0 means starting offset in text for this
	 * line, 1 means number of characters of this line.
	 * 
	 * @param text  The string to break
	 * @param width Width in pixels to break on
	 * @return A break index table
	 */
	protected int[][] breakString(char[] text, int width)
	{
		// Count text lines
		int offset = 0;
		int lines = 0;
		int newOffset;
		
		while (offset < text.length)
		{
			newOffset = 
				findNextBreak(text, offset, text.length - offset, width, mFont);
			offset = newOffset;
			lines++;
		}
		
		int[][] indices = new int[lines][2];
		
		// Setting offset data
		lines = 0;
		offset = 0;
		while (offset < text.length)
		{
			newOffset =
				findNextBreak(text, offset, text.length - offset, width, mFont);
			indices[lines][0] = offset;
			indices[lines][1] = newOffset - offset;
			lines++;
			offset = newOffset;
		}
		
		return indices;
	}
	
	/**
	 * Returns next break when breaking a string.
	 * 
	 * @param text   The chars to calculate on
	 * @param offset From what offset to read in chars
	 * @param len    How many characters to read
	 * @param w      Width
	 * @param f      Font
	 * @return Offset of next break or length of text if no more breaks
	 */
	public int findNextBreak(char[] text, int offset, int len, int w, Font f)
	{
		int breakOffset = offset;
		int textW = 0;
		int niceB = -1;
		char c;
		charLoop: while (breakOffset <= offset + len && textW < w)
		{
			if (breakOffset == offset + len)
				c = TEXTBREAKS[0]; // last character + 1, fake break char
			else
				c = text[breakOffset];
			if (c == NEWLINE)
			{
				// got a nice break here, new line
				niceB = breakOffset;
				break charLoop;
			}
			
			// Try finding break charachters
			breakCharLoop:
				for (int i = TEXTBREAKS.length - 1; i >= 0; i--)
				{
					if (c == TEXTBREAKS[i])
					{
						niceB = breakOffset;
						break breakCharLoop;
					}
				}
			if (breakOffset == offset + len - 1)
			{
				// Special case, skip the last character
				niceB = breakOffset + 1;
			}
			breakOffset++;
			textW += f.charWidth(c);
		}
		if (niceB > offset && niceB < offset + len - 2 && (text[niceB + 1] == ' '))
			return niceB + 2;        // case: special case to get rid of extra spaces
		else if (niceB > offset && niceB < offset + len)
			return niceB + 1;        // case: found a nice break, use this
		else if (breakOffset > offset + 1)
			return breakOffset - 1;  // case: broke due to text width too big
		else if (breakOffset == offset)
			return breakOffset + 1;  // case: broken on first char, step one more
		else
			return breakOffset;      // case: default
	}
	
	/**
	 * Paints the popup. Call this from your <code>Displayable</code>'s
	 * paint method.
	 * 
	 * @param g  Graphics context to paint on.
	 */
	public void paint(Graphics g)
	{
		if (mActive)
		{
			// draw transparent background
			for (int y = OFFSET_POPUP + mYOffset; y < OFFSET_POPUP + mYOffset + mHeight; y += 8)
			{
				g.drawRGB(mRGBData, 0, mWidth, OFFSET_POPUP, y, mWidth, Math.min(8,
						OFFSET_POPUP + mYOffset + mHeight - y), true);
			}
			
			// border
			g.setColor(mBorderColor);
			g.drawRect(OFFSET_POPUP, OFFSET_POPUP + mYOffset, mWidth, mHeight);
			
			// text
			g.setColor(mTextColor);
			g.setFont(mFont);
			int y = OFFSET_POPUP + OFFSET_TEXT + mYOffset;
			int maxLine =
				Math.min(mCurLine + mVisibleLines, mBreakTextData.length);
			for (int i = mCurLine; i < maxLine; i++)
			{
				int offset = mBreakTextData[i][0];
				int len = mBreakTextData[i][1];
				if (len == 1 && mText[offset] == NEWLINE)
				{
					y += mFontHeight;
				}
				else
				{
					if (mText[offset + len - 1] == NEWLINE)
					{
						len--;
					}
					g.drawChars(mText, offset, len, OFFSET_POPUP + OFFSET_TEXT
							+ (mWidth >> 1), y, Graphics.TOP | Graphics.HCENTER);
					y += mFontHeight;
				}
			}
			
			// scrollbar
			if (mVisibleLines < mBreakTextData.length)
			{
				int sbh = mVisibleLines * mFontHeight;   // Scrollbar max height
				int sbstep = ((sbh - 4) << 8) / mMaxLine; // Scrollbar height * 256
				int sbX =
					OFFSET_POPUP + mWidth - SB_WIDTH - (SB_WIDTH >> 1); // Scrollbar x-coordinate
				g.setColor(mTextColor);
				g.fillRect(sbX, OFFSET_POPUP + OFFSET_TEXT + ((mCurLine * sbstep) >> 8),
						SB_WIDTH, 4 + (sbstep >> 8));
			}
			
			// alternatives
			if (mAlternatives > 0)
			{
				y =
					OFFSET_POPUP + OFFSET_TEXT + mHeight + mYOffset - OFFSET_TEXT - mFontHeight;
				int dx = (mWidth / (mAlternatives + 1));
				int x = OFFSET_POPUP + OFFSET_TEXT;
				for (int i = 0; i < mAlternatives; i++)
				{
					char[] t = mAltTexts[i];
					x += dx;
					int xx = x - (mFont.charsWidth(t, 0, t.length) >> 1);
					if (mCurAlt != i)
					{
						// Unselected alternative
						g.setColor(mAlternativeColor);
						g.drawChars(t, 0, t.length, xx, y, Graphics.TOP | Graphics.LEFT);
					} 
					else
					{
						// Selected alternative
						g.setColor(mAlternativeColor);
						g.drawChars(t, 0, t.length, xx + 1, y + 1,
								Graphics.TOP | Graphics.LEFT);
						g.setColor(mSelectedAlternativeColor);
						g.drawChars(t, 0, t.length, xx, y, Graphics.TOP | Graphics.LEFT);
					}
				}
			}
		}
	}
	
	/**
	 * Handles user interaction when pressing a key.
	 * Call this from your <code>Displayable</code>'s
	 * keyPressed keyRepeated method.
	 * 
	 * @param keyCode  The keycode.
	 * @param gameCode The gamecode.
	 */
	public void keyPressed(int keyCode, int gameCode)
	{
		if (mActive)
		{
			if (mAlternatives < 1)
			{
				// If no choice, any key will do
				mActive = false;
				if (mListener != null)
					mListener.selectedChoice(mCurAlt, false);
			}
			else
			{
				switch (gameCode)
				{
				// Scroll text
				case Canvas.DOWN:
				{
					mCurLine++;
					if (mCurLine >= mMaxLine)
						mCurLine = 0;
					break;
				}
				case Canvas.UP:
				{
					if (mMaxLine > 0)
						mCurLine--;
					if (mCurLine < 0)
						mCurLine = mMaxLine - 1;
					break;
				}
				// Select among choices
				case Canvas.RIGHT:
				{
					mCurAlt++;
					if (mCurAlt >= mAlternatives)
						mCurAlt = 0;
					break;
				}
				case Canvas.LEFT:
				{
					mCurAlt--;
					if (mCurAlt < 0)
						mCurAlt = (byte) (mAlternatives - 1);
					break;
				}
				case Canvas.FIRE:
				{
					// Select
					if (mCurAlt >= 0)
					{
						mActive = false;
						if (mListener != null)
							mListener.selectedChoice(mCurAlt, false);
					}
					break;
				}
				case Device.KEYCODE_BACK:
				{
					mActive = false;
					if (mListener != null)
						mListener.selectedChoice(mTimeOutAlt, false);
					break;
				}
				}
			}
		}
	}
	
	/**
	 * Disposes all resources held by this popup and closes it.
	 */
	public void dispose()
	{
		mActive = false;
		mText = null;
		mAltTexts = null;
		mListener = null;
		mBreakTextData = null;
		System.gc();
	}
	
	/**
	 * Returns whether this popup is active or not.
	 * 
	 * @return true if active, false otherwise.
	 */
	public boolean isActive()
	{
		return mActive;
	}
	
	/**
	 * Returns alternative index on timeout
	 * 
	 * @return timeout alternative
	 */
	public byte getTimeOutChoice()
	{
		return mTimeOutAlt;
	}
	
	/**
	 * Called by framework to check if popup reached its' timeout.
	 * 
	 * @return true if timeout, false otherwise.
	 */
	protected boolean pollTimeout()
	{
		if (mActive)
		{
			if (mEndTime > 0 && System.currentTimeMillis() > mEndTime)
			{
				mActive = false;
				if (mListener != null)
				{
					mListener.selectedChoice(mTimeOutAlt, true);
					return true;
				}
			}
		}
		return false;
	}
	
	// Runnable impl to poll this popup
	public void run()
	{
		while (isActive())
		{
			// Poll popup timeout
			try
			{
				Thread.sleep(1000);
				pollTimeout();
			} catch (InterruptedException e) {}
		}
	}
}