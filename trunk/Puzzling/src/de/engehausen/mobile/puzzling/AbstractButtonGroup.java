package de.engehausen.mobile.puzzling;

import javax.microedition.lcdui.Graphics;

/**
 * A grouping of buttons. Each button is represented with a rectangular area.
 */
public abstract class AbstractButtonGroup {
	
	protected final Rectangle[] rects;

	/**
	 * Creates the button group.
	 * @param rectangles the rectangle array defining the button
	 * areas; used <i>by reference</i>.
	 */
	public AbstractButtonGroup(final Rectangle[] rectangles) {
		rects = rectangles;
	}

	/**
	 * Paints the buttons in the button group.
	 * @param g the graphics to paint on
	 */
	public abstract void paint(final Graphics g);

	/**
	 * Handles the activation of the button with the given
	 * index (corresponds to the rectangle being clicked).
	 * @param idx the index of the button (0..rectangle.length)
	 */
	public abstract void button(final int idx);

	/**
	 * Returns the height of the button group.
	 * @return the height or zero if unspecified
	 */
	public abstract int getHeight();

	/**
	 * Returns the width of the button group.
	 * @return the width or zero if unspecified
	 */
	public abstract int getWidth();

	/**
	 * Handles the buttons in this group for the click action.
	 * @param x the position of the click
	 * @param y the position of the click
	 */
	public void handleButtons(final int x, final int y) {
		for (int i = rects.length-1; i>=0; i--) {
			if (rects[i].contains(x, y)) {
				button(i);
				return;
			}
		}		
	}
	
}
