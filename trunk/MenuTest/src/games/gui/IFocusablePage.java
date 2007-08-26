package games.gui;

/**
 * An extended behaviour for a <code>MenuPage</code>. Enables
 * callback methods when entering and leaving a page.
 * 
 * @author PK CM
 */
public interface IFocusablePage {
	/**
	 * Called when page is entered.
	 */
	public void onEnter();
	/**
	 * Called when page is left.
	 */
	public void onLeave();
}
