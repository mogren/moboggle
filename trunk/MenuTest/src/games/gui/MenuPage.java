package games.gui;

import javax.microedition.lcdui.Image;
import java.util.Vector;

/**
 * A <code>MenuPage</code> class represents a title and a number of choices in a <code>Menu</code>.
 * The <code>MenuPage</code>'s choices are added as <code>PageItem</code>s via the
 * <code>addItem</code> method. 
 * @author PK CM
 */
public class MenuPage
{
	/** Left aligned image, right aligned text */
	public static final int LAYOUT_LEFT = 0;
	/** Right aligned image, left aligned text */
	public static final int LAYOUT_RIGHT = 1;
	
	/** The title of the page */
	protected char[] mTitle;
	/** The title image of the page */
	protected Image mTitleImage;
	/** The alignment of title and image */
	protected int mLayout = LAYOUT_LEFT;
	/** The page items */
	protected Vector mItems = new Vector();
	/** Current selected item index */
	protected int mCurrentIndex = -1;
	
	/**
	 * Creates a new page for a menu.
	 * @param title			The title of the page or null.
	 * @param titleImage		The image of the page or null.
	 */
	public MenuPage(char[] title, Image titleImage)
	{
		setTitle(title);
		setTitleImage(titleImage);
	}
	
	/**
	 * Sets the layout of the image,
	 * any of LAYOUT_LEFT, LAYOUT_RIGHT.
	 * @param layout	Image layout.
	 */
	public void setLayout(int layout)
	{
		mLayout = layout;
	}
	
	/**
	 * Returns the layout of the image,
	 * any of LAYOUT_LEFT, LAYOUT_RIGHT.
	 * @return  Image layout.
	 */
	public int getLayout()
	{
		return mLayout;
	}
	/**
	 * Returns the title of this page.
	 * 
	 * @return	The page title.
	 */
	public char[] getTitle()
	{
		return mTitle;
	}
	/**
	 * Sets the title of this page.
	 * 
	 * @param title	The page title.
	 */
	public void setTitle(char[] title)
	{
		mTitle = title;
	}
	/**
	 * Returns the title of this page.
	 * 
	 * @return	The page title image.
	 */
	public Image getTitleImage()
	{
		return mTitleImage;
	}
	/**
	 * Sets the title image of this page.
	 * 
	 * @param titleImage	The page title iamge.
	 */
	public void setTitleImage(Image titleImage)
	{
		mTitleImage = titleImage;
	}
	/**
	 * Adds an item to this page.
	 * 
	 * @param item	The item to add.
	 */
	public synchronized void addItem(PageItem item)
	{
		mItems.addElement(item);
		item.addedToPage();
		if (mCurrentIndex == -1)
		{
			mCurrentIndex = 0;
		}
	}
	/**
	 * Removes an item from this page.
	 * 
	 * @param item	The item to remove.
	 */
	public synchronized void removeItem(PageItem item)
	{
		mItems.removeElement(item);
		if (size() == 0)
		{
			mCurrentIndex = -1;
		}
	}
	/**
	 * Removes the item at specified index from this page.
	 * 
	 * @param index	The index of the item to remove.
	 */
	public synchronized void removeItem(int index)
	{
		mItems.removeElementAt(index);
	}
	/**
	 * Returns number of items in this page.
	 * 
	 * @return	Number of items.
	 */
	public synchronized int size()
	{
		return mItems.size();
	}
	/**
	 * Returns the currently selected index in this page. In 
	 * special cases this method may return -1, for no selected
	 * items.
	 * 
	 * @return	Selected item index.
	 */
	public synchronized int getSelectedIndex()
	{
		PageItem item = itemAt(mCurrentIndex);
		if (item != null && !item.isEnabled())
		{
			setSelectedIndex(mCurrentIndex+1);
		}
		return mCurrentIndex;
	}
	/**
	 * Sets the currently selected index in this page.
	 * If the index is greater than number of items in the
	 * page, it is wrapped to the first item. If the index
	 * is below zero, it is wrapped to the last item.
	 * If correct index is not possible to set index
	 * will be set to -1. This happens if there are no items,
	 * or all items are disabled.
	 * 
	 * @param index	Index of selected item.
	 */
	public synchronized void setSelectedIndex(int index)
	{
		int size = size();
		boolean dirDown = index - mCurrentIndex > 0;
		boolean allDisabled = true;
		for (int i = 0; allDisabled && i < size; i++)
		{
			allDisabled = !itemAt(i).isEnabled();
		} 
		if (size == 0 || allDisabled)
		{
			index = -1;
		}
		else
		{
			boolean enabled = true;
			do
			{  
				if (index >= size)
				{
					index = 0;
				}
				else if (index < 0)
				{
					index = size - 1;
				}
				enabled = itemAt(index).isEnabled();
				if (!enabled)
				{
					if (dirDown)
					{
						index++;
					}
					else
					{
						index--;
					}
				}
			} while (!enabled);
		}
		mCurrentIndex = index;
	}
	/**
	 * Returns item at specified index.
	 * 
	 * @param index	The index of the idem
	 * @return	The item at specified index, or null if not found.
	 */
	public synchronized PageItem itemAt(int index)
	{
		if (index < 0 || index >= size())
		{
			return null;
		}
		else
		{
			return (PageItem)mItems.elementAt(index);
		}
	}
	/**
	 * Returns the index of specified item. If this item does
	 * not belong to the list, -1 is returned.
	 * 
	 * @param item 	The item whose index to find.
	 * @return index of the item, or -1 if item is not part of the menu.
	 */
	public synchronized int getIndex(PageItem item)
	{
		return mItems.indexOf(item);
	}
	
	/**
	 * Removes all items on this page
	 */
	public synchronized void removeAllItems()
	{
		mItems.removeAllElements();
	}
}
