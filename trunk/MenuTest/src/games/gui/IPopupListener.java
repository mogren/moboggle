package games.gui;

/**
 * Implementations of this class are reported when users selects
 * an alternative in a popup.
 * @author PK, CM
 */
public interface IPopupListener
{
    /**
     * Called when an selection has been made.
     * @param choice	The index of the alternatives that were chosen.
     * @param timeOut	True if the choice was made because of a time out.
     */
	public void selectedChoice(byte choice, boolean timeOut);
}

