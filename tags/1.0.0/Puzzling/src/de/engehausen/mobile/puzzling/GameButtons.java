package de.engehausen.mobile.puzzling;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * The game control buttons (solve, shuffle, toggle sound and
 * quite - in this order).
 */
public class GameButtons extends AbstractButtonGroup implements Constants {

	private final GameCanvas canvas;
	private final Image[] buttons;
	private final int offset[] = {
		7, 8, 9, 5
	};

	/**
	 * Creates the buttons for the given canvas.
	 * @param aCanvas the parent canvas using the buttons, must not be <code>null</code>.
	 * @param images an array of button images, must not be <code>null</code>.
	 */
	public GameButtons(final GameCanvas aCanvas, final Image[] images) {
		super(new Rectangle[] {
			new Rectangle(28, 0, 52, 52), // solve
			new Rectangle(28+1*70, 0, 52, 52), // shuffle
			new Rectangle(28+2*70, 0, 52, 52), // toggle sound
			new Rectangle(28+3*70, 0, 52, 52)  // quit				
			});
		canvas = aCanvas;
		buttons = images;
	}

	// non-javadoc: see superclass
	public int getHeight() {
		return buttons[0].getHeight();
	}

	// non-javadoc: see superclass
	public int getWidth() {
		return 0;
	}	

	// non-javadoc: see superclass
	public void paint(final Graphics g) {
		for (int i = 0; i < 4; i++) {
			g.drawImage(buttons[offset[i]], rects[i].x, rects[i].y, POSITIONING);
		}
	}

	/**
	 * Indicates whether the sound button is shown in
	 * disabled mode (muted).
	 * @return <code>true</code> if sound is muted, <code>false</code> otherwise
	 */
	public boolean isMuted() {
		return offset[2]!=9;
	}

	// non-javadoc: see superclass
	public void button(final int idx) {
		switch (idx) {
		case 0:
			canvas.solve();
			break;
		case 1:
			canvas.reset();
			canvas.repaint();
			break;
		case 2:
			if (isMuted()) {
				offset[2] = 9;
			} else {
				offset[2] = 10;
			}
			canvas.repaint(Constants.GAME_RENDER_MODE_BUTTONS);
			break;
		case 3:
			canvas.quit();
			break;
		default:
			break;				
		}
	}

}
