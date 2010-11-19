package de.engehausen.mobile.puzzling;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Buttons used to add/remove an image source on the menu screen.
 */
public class PlusMinusButtons extends AbstractButtonGroup implements Constants {
	
	private final MenuCanvas canvas;
	private final Image[] images;

	/**
	 * Creates the buttons for the given menu canvas
	 * @param parent the menu canvas to operate on, must not be <code>null</code>.
	 * @param buttons a list of button images, must not be <code>null</code>.
	 * @param width the screen width
	 */
	public PlusMinusButtons(final MenuCanvas parent, final Image[] buttons, final int width) {
		super(new Rectangle[] { 
			new Rectangle(width-(5*buttons[0].getWidth())/2, 0, 52, 52),
			new Rectangle(width-(3*buttons[0].getWidth())/2+8, 0, 52, 52)
		});
		canvas = parent;
		images = buttons;
	}

	// non-javadoc: see superclass
	public int getHeight() {
		return images[0].getHeight();
	}

	// non-javadoc: see superclass
	public int getWidth() {
		return 0;
	}

	// non-javadoc: see superclass
	public void button(final int idx) {
		if (idx == 0) {
			canvas.inputURL();
		} else {
			canvas.removeSelection();
		}
	}

	public void paint(final Graphics g) {
		g.drawImage(images[3], rects[0].x, rects[0].y, POSITIONING);
		g.drawImage(images[4], rects[1].x, rects[1].y, POSITIONING);
	}

}
