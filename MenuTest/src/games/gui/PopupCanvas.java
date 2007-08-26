package games.gui;

import javax.microedition.lcdui.Canvas;

/**
 * Represents a canvas that can show popups.
 * @author Peter Andersson
 */
public abstract class PopupCanvas extends Canvas
{
  	/** The popup in this canvas */
	protected Popup m_popup;
	
	/**
	 * Returns the popup in this canvas or null if popup.
	 * @return	The current popup or null.
	 */
	public Popup getPopup()
	{
		return m_popup;
	}
	
	/**
	 * Sets the popup in this canvas.
	 * @param p	The popup.
	 */
	public void setPopup(Popup p)
	{
		m_popup = p;
	}
}