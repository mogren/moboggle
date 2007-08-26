package games.gui;

/**
 * This interface is used by applications which need to receive high-level events 
 * concerning a <code>Menu</code>. An instance of <code>MenuListener</code> is specified
 * to a <code>Menu</code> using the <code>setListener</code> method in <code>Menu</code>.
 * @author PK CM
 */
public interface IMenuListener{
	/**
	 * Called when a new page is shown.
	 * @param fromPage	Old page or null.
	 * @param toPage		New page.
	 * @param back		true if user steps back, false if active choice forwards.
	 */
	public void newPage(MenuPage fromPage, MenuPage toPage, boolean back);
	/**
	 * Called when a item is selected in a page.
	 * @param page		The page in which the item is selected.
	 * @param oldItem		The item that is deselected or null if no such item.
	 * @param newItem		The item that is selected.
	 */
	public void itemSelected(MenuPage page, PageItem oldItem, PageItem newItem);
	/**
	 * Called when an ItemAction is invoked.
	 * @param page	The page in which the action was invoked.
	 * @param item	The item the action belongs to.
	 * @param action	The action.
	 */
	public void actionCalled(MenuPage page, PageItem item, IItemAction action);
	/**
	 * Called when a transition is started on a new page selection.
	 * @param fromPage	Old page or null.
	 * @param toPage		New page.
	 * @param delay		Delay between each transition frame in milliseconds.
	 * @param frames		Number of frames in this transition.
	 * @param back		True if this is transition back to a previous page.
	 */
	public void transitionStarted(MenuPage fromPage, MenuPage toPage, long delay, int frames, boolean back);
	/**
	 * Called when a transition is finished.
	 * @param fromPage	Old page or null.
	 * @param toPage		New page.
	 */
	public void transitionStopped(MenuPage fromPage, MenuPage toPage);
}
