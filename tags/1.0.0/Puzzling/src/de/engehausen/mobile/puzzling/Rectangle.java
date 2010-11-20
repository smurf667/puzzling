package de.engehausen.mobile.puzzling;

/**
 * A rectangle. Also allows checking if a given point
 * is contained in the rectangle or not.
 */
public class Rectangle {
	
	protected final int x;
	protected final int y;
	
	private final int ex,ey;

	/**
	 * Creates the given rectangle at the given position and with
	 * the given width and height.
	 * @param px starting position of the rectangle
	 * @param py starting position of the rectangle
	 * @param w the rectangle width
	 * @param h the rectangle height
	 */
	public Rectangle(final int px, final int py, final int w, final int h) {
		x = px;
		y = py;
		ex = px+w;
		ey = py+h;
	}

	/**
	 * Checks if the given coordinate lie inside of the rectangle.
	 * @param px the x position
	 * @param py the y position
	 * @return <code>true</code> if the given coordinate is inside the rectangle.
	 */
	public boolean contains(final int px, final int py) {
		return px>=x && px<ex && py>=y && py<ey;
	}
		
}