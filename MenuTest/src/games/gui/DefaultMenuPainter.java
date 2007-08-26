package games.gui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import javax.microedition.lcdui.Graphics;

import games.res.Resources;

/**
 * A default implementation of <code>MenuPainter</code>. Transitions are implemented
 * as an horizontal movement. Items a rendered using normal MIDP text
 * methods.
 * @author PK CM
 */
public class DefaultMenuPainter implements IMenuPainter {
	
	protected Font mTitleFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);	/** Font of the page title */
	protected Font mItemFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);	/** Font of items */
	
	protected int mTitleColor = Resources.COLOR_TITLE_FG;			/** Page title color */
	protected int mTitleBackColor = Resources.COLOR_TITLE_BG;		/** Page title back color */ 
	protected int mItemColor = Resources.COLOR_ITEM_LBL;			/** Enabled item label color */ 
	protected int mItemColorDisabled = Resources.COLOR_ITEM_DIS;	/** Disabled item label color */
	protected int mSelItemColor = Resources.COLOR_ITEM_SEL;			/** Selected item label color */
	protected int mImgPadding = 4;	/** Image padding */
	
	public void paintMenu(Graphics g, MenuPage menu, int x, int y, int width, int height)
	{
		paintMenu(g, menu, x, y, width, height, false, false);
	}
	
	/**
	 * Paints a menu.
	 * 
	 * @param g		The graphics context to draw to.
	 * @param menu	The menu to draw.
	 * @param x		Offset x coordinate.
	 * @param y		Offset y coordinate.
	 * @param width	The width of the menu.
	 * @param height	The height of the menu.
	 * @param to		True if transition to this page, false if transition from
	 * 				or no transition.
	 * @param from	True if transition from this page, false if transition to
	 * 				or no transition.
	 */
	protected void paintMenu(Graphics g, MenuPage page, int x, int y, int width, int height, boolean to, boolean from)
	{
		if (page == null)
			return;
		//int tHeight = mTitleFont.getHeight();
		//int iHeight = mItemFont.getHeight();
		paintTitle(g, page, x + width / 2, y, width);
		paintItems(g, page, x, y + getTitleHeight(page) + 8, width, height,	to, from);
	}
	
	/**
	 * Calls paintMenu twice with the two pages, plus an x offset for each menu
	 * to visualize the transition.
	 */
	public void paintTransition(Graphics g, MenuPage fromMenu, MenuPage toMenu, int x, int y, int width, int height, int frame, int frames, boolean back)
	{
		double percent = (double) frame / (double) frames;
		int dx = (int) (width * Math.sin(Math.PI / 2d * percent));
		int sign = back ? -1 : 1;
		paintMenu(g, fromMenu, -dx * sign, y, width, height, false, true);
		paintMenu(g, toMenu, sign * (width - dx), y, width, height, true, false);
	}
	
	/**
	 * Draws a page title horizontally centered around specified coordinates.
	 * 
	 * @param g		Graphics context to paint on.
	 * @param page	The page with the title.
	 * @param x		X coordinate of title, title is centered around this.
	 * @param y		Y coordinate of title, denoting top of text.
	 * @param w		Width of the menu.
	 */
	protected void paintTitle(Graphics g, MenuPage page, int x, int y, int w)
	{
		char[] title = page.getTitle();
		Image img = page.getTitleImage();
		int imgW = 0;
		int imgH = 0;
		int txtW = 0;
		int txtH = mItemFont.getHeight();
		int layout = page.getLayout();
		boolean imgLeft = layout == MenuPage.LAYOUT_LEFT;
		
		if (title != null)
		{
			txtW = mItemFont.charsWidth(title, 0, title.length);
		}
		if (img != null)
		{
			imgW = img.getWidth();
			imgH = img.getHeight();
		}
		
		int totW = imgW + txtW + ((imgW == 0 || txtW == 0) ? 0 : mImgPadding);
		int totH = Math.max(imgH, txtH);
		
		// draw image if any
		if (img != null)
		{
			int dx = imgLeft ? (-totW/2 - imgW) : (txtW/2);
			g.drawImage(img, x + dx, y + (totH - imgH) / 2,	Graphics.TOP | Graphics.LEFT);
			imgW += mImgPadding;
		}
		
		// draw title if any
		if (title != null)
		{
			int dx = imgLeft ? (imgW/2) : (-imgW/2);
			g.setFont(mTitleFont);
			g.setColor(mTitleBackColor);
			g.drawChars(title, 0, title.length, x + dx + 1, y + (totH - txtH) / 2 + 1, Graphics.TOP | Graphics.HCENTER);
			g.setColor(mTitleColor);
			g.drawChars(title, 0, title.length,	x + dx, y + (totH - txtH) / 2, Graphics.TOP | Graphics.HCENTER);
		}
	}
	
	/**
	 * Paints all items in a page. This methods checks height of each
	 * item and sorts out exactly what items to paint according to
	 * currently selected item. Calls <code>getItemHeight</code> for 
	 * items to determine how to fit items, and then calls <code>paintItem</code>
	 * for those items it seems fit to draw.
	 * 
	 * @param g		Graphics context to draw to.
	 * @param page	The page containing all items.
	 * @param x		X offset coordinate.
	 * @param y		Y offset coordinate.
	 * @param w		Width of menu, to determine how to horizontally centralize items.
	 * @param h		Height of menu, to determine how many items to draw.
	 * @param to		True if transition to this item, false if transition from
	 * 				or no transition.
	 * @param from	True if transition from this item, false if transition to
	 * 				or no transition.
	 */
	protected void paintItems(Graphics g, MenuPage page, int x, int y, int w, int h,
			boolean to, boolean from)
	{
		int selectedIndex = page.getSelectedIndex();
		PageItem selectedItem = page.itemAt(selectedIndex);
		PageItem itemBelow = page.itemAt(selectedIndex + 1);
		itemBelow = itemBelow == null ? selectedItem : itemBelow;
		
		// Find number of items that we can show in the viewport,
		// try having the selected near middle
		int startIndex = selectedIndex < 0 ? 0 : selectedIndex;
		int size = page.size();
		if (selectedItem != null)
		{
			int topIndex = selectedIndex;
			int endIndex = selectedIndex;
			int yLeft = h - y - getItemHeight(selectedItem) - getItemHeight(itemBelow);
			// check item under and above
			// until viewport is filled or all items are added
			do
			{
				if (endIndex < size - 1)
				{
					endIndex++;
					yLeft -= getItemHeight(page.itemAt(endIndex));
				}
				if (yLeft > 0 && topIndex > 0)
				{
					topIndex--;
					yLeft -= getItemHeight(page.itemAt(topIndex));
				}
			} while (yLeft > 0 && (topIndex > 0 || endIndex < size - 1));
			
			startIndex = topIndex;
		}
		
		// Find max width
		int iMaxW = 0;
		for (int i = startIndex < 0 ? 0 : startIndex; i < size; i++)
		{
			iMaxW = Math.max(iMaxW, getItemWidth(page.itemAt(i)));
		}
		// Draw items
		for (int i = startIndex < 0 ? 0 : startIndex; i < size; i++)
		{
			boolean selected = i == selectedIndex;
			PageItem item = page.itemAt(i); 
			paintItem(g, item, selected, x + w / 2, y, w, iMaxW, to, from);
			y += getItemHeight(item);
			if (y >= h)
			{
				break;
			}
		}
	}
	
	/**
	 * Paints one single item. If the item has an image, the image will
	 * be painted to the left of the label text. 
	 * 
	 * @param g			The graphics context to draw to.
	 * @param item		The item to paint.
	 * @param selected	If this item is selected or not.
	 * @param x 			X coordinate, item will be centered around this.
	 * @param y			Y coordinate, denoting top of item
	 * @param w			Width of menu, to determine center.
	 * @param iMaxW		Maximum width of all visible items, for alignment.
	 * @param to			True if transition to this item, false if transition from
	 * 					or no transition.
	 * @param from		True if transition from this item, false if transition to
	 * 					or no transition.
	 */
	protected void paintItem(Graphics g, PageItem item, boolean selected, int x,
			int y, int w, int iMaxW, boolean to, boolean from)
	{
		char[] label = item.getLabel();
		boolean enabled = item.isEnabled();
		Image img = item.getImage();
		int imgW = 0;
		int imgH = 0;
		int txtW = 0;
		int txtH = mItemFont.getHeight();
		int layout = item.getLayout();
		boolean align = (layout == PageItem.LAYOUT_ALIGN_LEFT | layout == PageItem.LAYOUT_ALIGN_RIGHT);
		boolean imgLeft = (layout == PageItem.LAYOUT_CENTERED_LEFT | layout == PageItem.LAYOUT_ALIGN_LEFT);
		
		if (label != null)
			txtW = mItemFont.charsWidth(label, 0, label.length);
		if (img != null)
		{
			imgW = img.getWidth();
			imgH = img.getHeight();
		}
		
		int totW = imgW + txtW + ((imgW == 0 || txtW == 0) ? 0 : mImgPadding);
		int totH = Math.max(imgH, txtH);
		
		// draw image if any
		if (img != null)
		{
			int dx = 0;
			if (align)
				dx = imgLeft ? (-iMaxW/2) : (iMaxW/2 - imgW);
			else
				dx = imgLeft ? (-totW/2 - imgW) : (txtW/2);
				
			g.drawImage(img, x + dx, y + (totH - imgH) / 2,	Graphics.TOP | Graphics.LEFT);
			imgW += mImgPadding;
		}
		
		// draw label if any
		if (label != null)
		{
			if (!selected)
				g.setColor(enabled ? mItemColor : mItemColorDisabled);
			else
				g.setColor(mSelItemColor);
			
			int dx = 0;
			if (align)
				dx = imgLeft ? ((iMaxW - txtW)/2) : (-(iMaxW - txtW)/2);
			else
				dx = imgLeft ? (imgW/2) : (-imgW/2);
				
			g.setFont(mItemFont);
			g.drawChars(label, 0, label.length,	x + dx, y + (totH - txtH) / 2, Graphics.TOP | Graphics.HCENTER);
		}
	}
	
	/**
	 * Returns the height of a title. Simply returns height of 
	 * title font.
	 * @param menu	The menu with title whose height to return.
	 * @return		The title height.
	 */
	protected int getTitleHeight(MenuPage menu)
	{
		return mTitleFont.getHeight();
	}
	
	/**
	 * Returns the height of an item. Takes the maximum value of
	 * height of image and height of item font.
	 * @param item	The item whose height to return.
	 * @return		The height of the item.
	 */
	protected int getItemHeight(PageItem item)
	{
		int fHeight = mItemFont.getHeight();
		Image image = item.getImage();
		int iHeight = (image == null ? 0 : image.getHeight());
		return Math.max(fHeight, iHeight);
	}
	/**
	 * Returns the width of an item. Summarizes image width,
	 * padding and label width.
	 * @param item	The item whose width to return.
	 * @return		The width of the item.
	 */
	protected int getItemWidth(PageItem item)
	{
		Image image = item.getImage();
		char[] text = item.getLabel();
		int w = (image == null ? 0 : image.getWidth());
		w += (text == null ? 0 : mItemFont.charsWidth(text, 0, text.length));
		w += (image == null || text == null) ? 0 : mImgPadding;
		return w;
	}
}
