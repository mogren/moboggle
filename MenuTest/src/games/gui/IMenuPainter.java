package games.gui;

import javax.microedition.lcdui.Graphics;

/**
 * The <code>MenuPainter</code> interface defines high-level methods
 * when drawing contents of a <code>Menu</code>, which consists of
 * <code>MenuPage</code>s and <code>PageItem</code>s. An implementation
 * of this interface is provided when constructing a <code>Menu</code>.
 * @author PK CM
 */
public interface IMenuPainter
{
	/**
	 * Paints a static menu.
	 * 
	 * @param g		The graphics context to draw to.
	 * @param menu	The menu to draw.
	 * @param x		Offset x coordinate.
	 * @param y		Offset y coordinate.
	 * @param width	The width of the menu.
	 * @param height	The height of the menu.
	 */
	public void paintMenu(Graphics g, MenuPage menu, int x, int y, int width, int height);
	
	/**
	 * Paints a transition between menus.
	 * 
	 * @param g			The graphics context to draw to.
	 * @param fromMenu	Source menu.
	 * @param toMenu		Destination menu.
	 * @param x			Offset x coordinate.
	 * @param y			Offset y coordinate.
	 * @param width		The width of the menu.
	 * @param height		The height of the menu.
	 * @param frame		Current frame (0 - frames).
	 * @param frames		Maximum frames in this transition.
	 * @param back		True if going back to a page, false otherwise.
	 */
	public void paintTransition(Graphics g, MenuPage fromMenu, MenuPage toMenu, int x, int y, 
								int width, int height, int frame, int frames, boolean back);
}
