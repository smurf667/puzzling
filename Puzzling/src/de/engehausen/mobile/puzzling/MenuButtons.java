package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * The buttons used to control the main functions of
 * the menu (play, set difficulty and exit the game).
 */
public class MenuButtons extends AbstractButtonGroup implements Constants {
	
	private final Image[] images;
	private final MenuCanvas canvas;
	private final int xoff;
	private final int offset[] = {
		6, 0, 5
	};

	/**
	 * Creates the buttons for the given menu canvas
	 * @param parent the menu canvas to operate on, must not be <code>null</code>.
	 * @param buttons a list of button images, must not be <code>null</code>.
	 * @param width the screen width
	 * @throws IOException in case of error
	 */
	public MenuButtons(final MenuCanvas parent, final Image[] buttons, final int width) throws IOException {
		super(new Rectangle[3]);
		canvas = parent;
		images = buttons;
		xoff = width/6;
		rects[0] = new Rectangle(xoff, 0, 52, 52);   // play
		rects[1] = new Rectangle(8+52+xoff, 0, 52, 52);   // difficulty
		rects[2] = new Rectangle(width-52-xoff, 0, 52, 52);   // exit
	}

	// non-javadoc: see interface
	public int getHeight() {
		return images[0].getHeight();
	}

	// non-javadoc: see interface
	public int getWidth() {
		return 0;
	}

	/**
	 * Returns the "difficulty" level of the game to play.
	 * @return 3 = normal, 4 = hard, 5 = veeeery hard
	 */
	public int getSplit() {
		return 3+offset[1];
	}

	// non-javadoc: see superclass
	public void button(final int idx) {
		switch (idx) {
			case 0:
				canvas.play();
				break;
			case 1:
				offset[1] = (offset[1]+1)%3;
				canvas.buttonsChanged();
				break;
			default:
				canvas.exit();
				break;
		}
	}

	// non-javadoc: see superclass
	public void paint(final Graphics g) {
		for (int i = rects.length-1; i>=0; i--) {
			g.drawImage(images[offset[i]], rects[i].x, rects[i].y, POSITIONING);
		}
	}

}
