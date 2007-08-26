package games.gui;

/**
 * Command pattern interface that is called from Menu when user activates an PageItem. 
 * @author PK CM
 */
public interface IItemAction
{
	/**
	 * Called when user selects an item in a page.
	 * 
	 * @param page	The page this action is called from.
	 * @param item	The item this action is called from.
	 */
	public void itemAction(MenuPage page, PageItem item);
}
