package games.gui;

import java.util.Hashtable;
import javax.microedition.lcdui.Image;

/**
 * The <code>PageItem</code> defines a choice in a <code>MenuPage</code>.
 * An item can have a subpage and/or an <code>ItemAction</code> implementation.
 * When an item is selected by user, the <code>ItemAction</code> implementation is
 * invoked, and/or a transition to the subpage is started.
 * <code>PageItem</code>s can also have key/value pairs typed as int/Object.
 * When there is both a subpage and an action; pressing fire on such an
 * item will first start a new thread invoking the action, then the menu will navigate
 * to the subpage. If the user presses right on such an item, the action is not executed
 * but the subpage is navigated to.
 * @author PK CM
 */
public class PageItem
{
	/** Centered item, image to the left of text (Default layout) */
	public static final int LAYOUT_CENTERED_LEFT = 0;
	/** Centered item, image to the right of text */
	public static final int LAYOUT_CENTERED_RIGHT = 1;
	/** Left aligned image, right aligned text */
	public static final int LAYOUT_ALIGN_LEFT = 2;
	/** Right aligned image, left aligned text */
	public static final int LAYOUT_ALIGN_RIGHT = 3;
	
	/** The label of the item */
	protected char[] mLabel;
	/** The action of the item */
	protected IItemAction mAction;
	/** The next page to goto if this item is selected */
	protected MenuPage mSubPage;
	/** The image of this item */
	protected Image mImage;
	/** Layout of image */
	protected int mLayout = LAYOUT_CENTERED_LEFT;
	/** Enabled / disabled flag */
	protected boolean mEnabled = true;
	/** Properties table */
	protected Hashtable mProps = null;
	/** Item id */
	protected int mId = Integer.MIN_VALUE;
	
	/**
	 * Creates an item that is to be inserted into a MenuPage.
	 * Item graphical elements are the label and the image, and logical
	 * elements are action and subpage. 
	 * 
	 * @param label		The text label of this item or null if no text.
	 * @param image		The image of this item or null if no image.
	 * @param action		The action that is called when activating this item,
	 * 					or null if no action.
	 * @param subPage		The page that will be navigated to when activating this item,
	 * 					or null of no such page.
	 */
	public PageItem(char[] label, Image image, IItemAction action, MenuPage subPage)
	{
		setLabel(label);
		setImage(image);
		setAction(action);
		setSubPage(subPage);
	}
	
	/**
	 * Creates an item that is to be inserted into a <code>MenuPage</code>.
	 * Item graphical elements are the label and the image, and logical
	 * elements are action and subpage. 
	 * 
	 * @param label		The text label of this item or null if no text.
	 * @param image		The image of this item or null if no image.
	 * @param action		The action that is called when activating this item,
	 * 					or null if no action.
	 * @param subPage		The page that will be navigated to when activating this item,
	 * 					or null of no such page.
	 * @param id			The id of this item.
	 */
	public PageItem(char[] label, Image image, IItemAction action, MenuPage subPage, int id)
	{
		this(label, image, action, subPage);
		mId = id;
	}
	
	/**
	 * Returns ID of this item, or <code>Integer.MIN_VALUE</code> if not set.
	 * @return The item id
	 */
	public int getId()
	{
		return mId;
	}
	
	/**
	 * Called when this item is added to a page.
	 * Default implementation does nothinhg.
	 */
	public void addedToPage() {}
	
	/**
	 * Returns the label of this item.
	 * 
	 * @return	The text label of this item.
	 */
	public char[] getLabel()
	{
		return mLabel;
	}
	/**
	 * Sets the text label of this item.
	 * 
	 * @param label	The text label of this item.
	 */
	public void setLabel(char[] label)
	{
		mLabel = label;
	}
	/**
	 * Returns the subpage that will be displayed when this
	 * item is activated, or null if no such page.
	 * 
	 * @return	The sub page or null.
	 */
	public MenuPage getSubPage()
	{
		return mSubPage;
	}
	/**
	 * Sets the subpage that will be displayed when this
	 * item is activated, or null if no such page.
	 * 
	 * @param page  The sub page or null.
	 */
	public void setSubPage(MenuPage page)
	{
		mSubPage = page;
	}
	/**
	 * Returns action which is called upon item activation.
	 * 
	 * @return The action of this item.
	 */
	public IItemAction getAction()
	{
		return mAction;
	}
	/**
	 * Sets the action which is called upon item activation.
	 * 
	 * @param action The action of this item.
	 */
	public void setAction(IItemAction action)
	{
		mAction = action;
	}
	/**
	 * Returns true if this item is enabled, false otherwise.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled()
	{
		return mEnabled;
	}
	/**
	 * Enables or disables this item.
	 * 
	 * @param enabled true to enable, false to disable
	 */
	public void setEnabled(boolean enabled)
	{
		mEnabled = enabled;
	}
	/**
	 * Returns image of this item, or null if none set.
	 * 
	 * @return	The image or null.
	 */
	public Image getImage()
	{
		return mImage;
	}
	/**
	 * Sets the image of this item.
	 * 
	 * @param image	The image or null.
	 */
	public void setImage(Image image)
	{
		mImage = image;
	}
	
	/**
	 * Sets the layout of the image,
	 * any of LAYOUT_CENTERED_LEFT, 
	 * LAYOUT_CENTERED_RIGHT, LAYOUT_ALIGN_LEFT, LAYOUT_ALIGN_RIGHT.
	 * @param layout	Image layout.
	 */
	public void setLayout(int layout)
	{
		mLayout = layout;
	}
	
	/**
	 * Returns the layout of the image,
	 * any of LAYOUT_CENTERED_LEFT, 
	 * LAYOUT_CENTERED_RIGHT, LAYOUT_ALIGN_LEFT, LAYOUT_ALIGN_RIGHT.
	 * @return  Image layout.
	 */
	public int getLayout()
	{
		return mLayout;
	}
	
	/**
	 * Returns value of a generic property on this item
	 * or null if not set.
	 * 
	 * @param key		The key of the property.
	 * @return		The value of the property or null.
	 */
	public Object getProperty(int key)
	{
		if (mProps == null)
			return null;
		else
			return mProps.get(new Integer(key));
	}
	/**
	 * Sets a value to a generic property on this item, or
	 * resets it by giving <code>null</code> as a value.
	 * 
	 * @param key		The key of the property.
	 * @param value	The value of the property, or null to reset it.
	 */
	public void setProperty(int key, Object value)
	{
		if (mProps == null)
		{
			mProps = new Hashtable();
		}
		if (value == null)
		{
			mProps.remove(new Integer(key));
		}
		else
		{
			mProps.put(new Integer(key), value);
		}
	}
}