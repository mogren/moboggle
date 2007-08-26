package games.gui;

import java.util.Stack;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import games.res.Device;

public class Menu implements Runnable{
	/** The menu painter */
	protected IMenuPainter mPainter;
	/** The start page of this menu */
	protected MenuPage mStartPage;
	/** Current page */
	protected MenuPage mCurPage;
	/** Current selected item index in current page */
	protected int mCurItemIndex;
	/** Transition flag */
	protected boolean mInTransition;
	/** Back transition flag */
	protected boolean mTransitionBack;
	/** Transition destination page */
	protected MenuPage mTransPage;
	/** Current transition frame */
	protected int mFrame;
	/** Maximum transition frames */
	protected int mFrames;
	/** Transition frame delay in milliseconds, 500 as default */
	protected long mFrameDelay = 500;
	/** The menu listener. For a compact framework, we allow only one listener. */
	protected IMenuListener mListener;
	/** Animation lock */
	protected final Object ANIM_LOCK = new Object(); 
	/** Thread running flag */
	protected volatile boolean mRunning = true;
	/** The canvas that renders this menu */
	protected Canvas mCanvas; 
	/** The navigation trail stack */
	protected Stack mHistory = new Stack();
	/** Menu width */
	protected int mWidth = 0;
	/** Menu height */
	protected int mHeight = 0;
	/** Menu x offset */
	protected int mX = 0;
	/** Menu y offset */
	protected int mY = 0;
	
	/**
	 * Creates a new menu.
	 * 
	 * @param startPage	The first page in this menu.
	 * @param canvas		The canvas that this menu is drawn upon.
	 * @param painter		The painter used to draw this menu.
	 */
	public Menu(MenuPage startPage, Canvas canvas, IMenuPainter painter)
	{
		mStartPage = startPage;
		setCanvas(canvas);
		setPainter(painter);
	}
	
	/**
	 * Activates this menu and jumps to the starting page. 
	 */
	public void start()
	{
		new Thread(this, "Menu thread").start();
		gotoPage(mStartPage);
	}
	
	/**
	 * Call this from your displayable's paint method to paint the menu.
	 * @param g	The graphics context.
	 */
	public void paint(Graphics g)
	{
		if (mWidth == 0 && mHeight == 0 && mCanvas != null)
		{
			mWidth = mCanvas.getWidth() - mX;
			mHeight = mCanvas.getHeight() - mY;
		}
		if (mInTransition && mFrames > 0)
		{
			mPainter.paintTransition(g, mCurPage, mTransPage,
					mX, mY, mWidth, mHeight, mFrame, mFrames, mTransitionBack);
		}
		else
		{
			mPainter.paintMenu(g, mCurPage, mX, mY, mWidth, mHeight);
		}
	}
	/**
	 * Call this from your displayable's keyPressed or keyRepeated method 
	 * invoke user interaction on the menu.
	 * @param keyCode 	The keyCode.
	 */
	public void keyPressed(int keyCode)
	{
		MenuPage curPage = getCurrentPage();
		if (curPage != null && mCanvas != null)
		{
			if (keyCode == Device.KEYCODE_BACK)
			{
				goBack();
				return;
			}
			int gameCode = mCanvas.getGameAction(keyCode);
			switch (gameCode)
			{
			case Canvas.LEFT:
				goBack();
				break;
			case Canvas.FIRE:
			case Canvas.RIGHT:
				PageItem item = getSelectedItem();
				if (gameCode == Canvas.FIRE && item != null && item.getAction() != null)
				{
					doAction(item.getAction(), curPage, item);
				}
				if (item != null && item.getSubPage() != null)
				{
					gotoPage(item.getSubPage());
				}
				break;
			case Canvas.UP:
				setSelectedItemIndex(getSelectedItemIndex()-1);
				break;
			case Canvas.DOWN:
				setSelectedItemIndex(getSelectedItemIndex()+1);
				break;
			}
		}
	}
	
	/**
	 * Steps forward to a new page.
	 * @param newPage 	The new page.
	 */
	public void gotoPage(MenuPage newPage)
	{
		MenuPage curPage = null;
		synchronized(ANIM_LOCK)
		{
			curPage = getCurrentPage();
			if (curPage != null)
			{
				mHistory.push(curPage);
			}
		}
		if (mListener != null)
		{
			mListener.newPage(curPage, newPage, false); 
			mListener.itemSelected(newPage, null,
					newPage == null ? null : (newPage.itemAt(newPage.getSelectedIndex()))
			);
		}
		startTransition(curPage, newPage, false);
	}
	
	/**
	 * Steps back to previous page. Does nothing if there are no previous pages.
	 */
	public void goBack()
	{
		MenuPage curPage = null;
		MenuPage backPage = null;
		synchronized(ANIM_LOCK)
		{
			curPage = getCurrentPage();
			if (mHistory.size() > 0)
			{
				backPage = (MenuPage)mHistory.pop();
			}
		}
		if (backPage != null)
		{
			if (mListener != null)
			{
				mListener.newPage(curPage, backPage, true); 
				mListener.itemSelected(backPage, null,
						backPage == null ? null : (backPage.itemAt(backPage.getSelectedIndex()))
				);
			}
			startTransition(curPage, backPage, true);
		}
	}
	
	/**
	 * Executes specified action.
	 * @param action		The action to execute.
	 * @param page		The page from which the action was executed.
	 * @param item		The item the action is called from.
	 */
	protected void doAction(final IItemAction action, final MenuPage page, final PageItem item)
	{
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					action.itemAction(page, item);
					if (mListener != null)
					{
						mListener.actionCalled(page, item, action);
					}
					if (mCanvas != null)
					{
						mCanvas.repaint();
					}
				}
				catch (Throwable t)
				{
					//System.out.println("Exception in ItemAction");
					//t.printStackTrace();
				}
			}
		},"ItemAction runner").start();
	}
	
	/**
	 * Returns current page. If a transition between pages are ongoing,
	 * the destination page of the transition is returned.
	 * @return Current page.
	 */
	public MenuPage getCurrentPage()
	{
		MenuPage curPage = null;
		synchronized(ANIM_LOCK)
		{
			if (mInTransition)
			{
				curPage = mTransPage;
			}
			else
			{
				curPage = mCurPage;
			}
		}
		return curPage;
	}
	
	/**
	 * Returns selected item or null if no item is currently selected.
	 * @return Selected item or null.
	 */
	public PageItem getSelectedItem()
	{
		MenuPage curPage = getCurrentPage();
		if (curPage != null)
		{
			return curPage.itemAt(curPage.getSelectedIndex());
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Returns index of selected item or -1 if no item is currently selected. 
	 * @return Index of selected item or -1.
	 */
	public int getSelectedItemIndex()
	{
		MenuPage curPage = getCurrentPage();
		if (curPage != null)
		{
			return curPage.getSelectedIndex();
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Sets index of selected item.
	 * @param itemIndex The index of the item to select.
	 */
	public void setSelectedItemIndex(int itemIndex)
	{
		MenuPage curPage = getCurrentPage();
		if (curPage != null)
		{
			PageItem oldItem = curPage.itemAt(curPage.getSelectedIndex());
			curPage.setSelectedIndex(itemIndex);
			PageItem newItem = curPage.itemAt(curPage.getSelectedIndex());
			if (mListener != null)
			{
				mListener.itemSelected(curPage, oldItem, newItem);
			}
			if (mCanvas != null)
			{
				mCanvas.repaint();
			}
		}
	}
	
	/**
	 * Returns the canvas this menu is drawn upon.
	 * @return	The canvas drawing this menu.
	 */
	public Canvas getCanvas()
	{
		return mCanvas;
	}
	/**
	 * Sets the canvas this menu is drawn upon.
	 * @param canvas The canvas drawing this menu.
	 */
	public void setCanvas(Canvas canvas)
	{
		mCanvas = canvas;
	}
	/**
	 * Returns the listener.
	 * @return The menu listener.
	 */
	public IMenuListener getListener()
	{
		return mListener;
	}
	/**
	 * Sets the listener which is reported on menu events.
	 * @param listener A menu listener.
	 */
	public void setListener(IMenuListener listener)
	{
		mListener = listener;
	}
	/**
	 * Returns the painter used to paint the menu.
	 * @return	The painter.
	 */
	public IMenuPainter getPainter()
	{
		return mPainter;
	}
	/**
	 * Sets the painter used to paint the menu.
	 * @param painter		The painter.
	 */
	public void setPainter(IMenuPainter painter)
	{
		mPainter = painter;
	}
	/**
	 * Returns the frame delay in milliseconds.
	 * @return The delay between each frame update.
	 */
	public long getFrameDelay()
	{
		return mFrameDelay;
	}
	/**
	 * Returns number of frames in a page switch.
	 * @return	Number of frames in a page switch.
	 */
	public int getFrames()
	{
		return mFrames;
	}
	/**
	 * Returns height of this menu.
	 * @return	The height.
	 */
	public int getHeight()
	{
		return mHeight;
	}
	/**
	 * Returns start page of this menu.
	 * @return	The start page.
	 */
	public MenuPage getStartPage()
	{
		return mStartPage;
	}
	/**
	 * Returns width of this menu.
	 * @return	The width.
	 */
	public int getWidth()
	{
		return mWidth;
	}
	/**
	 * Returns x offset of this menu.
	 * @return	The x offset.
	 */
	public int getX()
	{
		return mX;
	}
	/**
	 * Returns y offset of this menu.
	 * @return	The y offset.
	 */
	public int getY()
	{
		return mY;
	}
	/**
	 * Sets the location of this menu.
	 * @param x	The x offset.
	 * @param y	The y offset.
	 */
	public void setLocation(int x, int y)
	{
		mX = x;
		mY = y;
	}
	/**
	 * Sets the size of this menu. If width and height are zero,
	 * these values will be collected from the canvas that this
	 * menu is painted upon.
	 * @param width	The width.
	 * @param height	The height.
	 */
	public void setDimensions(int width, int height)
	{
		mWidth = width;
		mHeight = height;
	}
	/**
	 * Sets the values used in a transition between to pages.
	 * A transition consists of a number of frames with a delay
	 * between each frame. A full transition will take
	 * <param>nbrOfFrames</param> * (<param>frameDelay</param> +
	 * time to paint frame) milliseconds.
	 * 
	 * @param nbrOfFrames		Number of frames in a transition.
	 * @param frameDelay		Delay in milliseconds in each transition.
	 */
	public void setFrameData(int nbrOfFrames, long frameDelay)
	{
		mFrames = nbrOfFrames;
		mFrameDelay = frameDelay;
	}
	
	/**
	 * Starts a new transition between pages. If there is an ongoing transition,
	 * this is immediately pushed to its end and the new one is started.
	 * @param fromPage	Transition source page.
	 * @param toPage		Transition destination page.
	 * @param back		True if it is a transition back to destination page, false otherwise.
	 */
	protected void startTransition(MenuPage fromPage, MenuPage toPage, boolean back)
	{
		synchronized(ANIM_LOCK)
		{
			mCurPage = fromPage;
			mTransPage = toPage;
			
			// Wait for ongoing transition to end
			if (mInTransition)
			{
				ANIM_LOCK.notifyAll();        
			}
			while (mInTransition)
			{
				try
				{
					ANIM_LOCK.wait();
				}
				catch (InterruptedException e) {}
			}
			
			mTransitionBack = back;
			
			// Start new transition
			mInTransition  = true;
			ANIM_LOCK.notifyAll();
		}
	}
	
	/**
	 *  <code>Runnable</code> implementation, invokes
	 *  the <code>MenuPainter</code> on transitions.
	 */
	public void run()
	{
		while(mRunning)
		{
			synchronized(ANIM_LOCK)
			{
				// Wait for a transition to be requested
				while (!mInTransition)
				{
					try
					{
						ANIM_LOCK.wait();
					} catch (InterruptedException e) {}
				}
				
				// Transition initiated
				mInTransition = true;
				int frames = mFrames;
				long delay = mFrameDelay;
				MenuPage fromPage = mCurPage;
				MenuPage toPage = mTransPage;
				boolean backFlag = mTransitionBack;
				if (mListener != null)
				{
					mListener.transitionStarted(fromPage, toPage,
							delay, frames, backFlag); 
				}
				
				// Do each frame in transition
				for (; mFrame < frames; mFrame++)
				{
					try
					{
						ANIM_LOCK.wait(delay);
					} catch (InterruptedException e1) {}
					if (mCanvas != null)
					{
						mCanvas.repaint();
					}
					
					// Check if someone changed parameters for transition,
					// in that case interrupt current transition
					if (mCurPage != fromPage || mTransPage != toPage)
					{
						break;
					}
				}
				mFrame = 0;
				mCurPage = toPage;
				if (mCanvas != null) mCanvas.repaint();
				mInTransition = false;
				
				if (mListener != null)
				{
					mListener.transitionStopped(fromPage, toPage); 
				}
				
				if (mCanvas != null)
				{
					mCanvas.repaint();
				}
				
				// Notify that this transition has ended
				ANIM_LOCK.notifyAll();
			}
		}
	}
}
