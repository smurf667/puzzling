package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * Canvas for showing the input for image sources.
 */
public class InputCanvas extends Canvas implements Constants {
	
	private final Font font;
	private int mode;
	private final Keys keys;
	private final int keyOffset;	
	private final Main main;
	private MenuCanvas menuCanvas;
	private final StringBuffer buffer;
	private static final String DEFAULT = "http://";

	/**
	 * 
	 * @param aMain the main control object, must not be <code>null</code>
	 * @param aFont the font, must not be <code>null</code>
	 * @throws IOException in case of error
	 */
	public InputCanvas(final Main aMain, final Font aFont) throws IOException {
		super();
		font = aFont;
		main = aMain;
		keys = new Keys(aFont, this, getWidth());
		keyOffset = getHeight()-keys.getHeight()-8;
		buffer = new StringBuffer(128);
		buffer.append(DEFAULT);
		mode = INPUT_RENDER_FULL;
	}
	
	protected void reset() {
		mode = INPUT_RENDER_FULL;
		buffer.setLength(0);
		buffer.append(DEFAULT);
	}
	
	/**
	 * Sets the menu canvas to display after finishing
	 * or canceling the input.
	 * @param canvas the main menu canvas, must not be <code>null</code>.
	 */
	public void setMenuCanvas(final MenuCanvas canvas) {
		menuCanvas = canvas;
	}

	/**
	 * Handle a touch. If no button is activated, this will
	 * toggle between upper case and lower case - not so
	 * user friendly, but heck it works.
	 * @param x the x position of the touch point
	 * @param y the y position of the touch point
	 */
	protected void pointerReleased(final int x, final int y) {
		if (y >= keyOffset) {
			keys.handleButtons(x, y-keyOffset);
		} else {
			keys.toggleLayout();
			mode |= INPUT_RENDER_KEYS;
			repaint();
		}
	}

	protected void paint(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		if (mode == INPUT_RENDER_FULL) {
			g.setColor(BLACK);
			g.fillRect(0, 0, width, height);
		}
		if ((mode & INPUT_RENDER_KEYS) != 0) {
			mode ^= INPUT_RENDER_KEYS;
			g.translate(0, keyOffset);
			try {
				keys.paint(g);				
			} finally {
				g.translate(0, -keyOffset);
			}
			font.paint("URL", 8, 16, g);
		}
		if ((mode & INPUT_RENDER_URL) != 0) {
			mode ^= INPUT_RENDER_URL;
			g.setColor(DARK_RED);
			final int max = buffer.length();
			final int fw = font.getCharWidth();
			final int lines = 1+(max-1)/(width/fw);
			final int start = 24+font.getCharHeight();
			final int bheight = lines*font.getCharHeight()+4;
			g.fillRect(0, start, width, bheight);
			g.setColor(BLACK);
			g.fillRect(0, start+bheight, width, font.getCharHeight()+4);
			
			int y = 24+font.getCharHeight()+2;
			int x = 4;
			final int mw = width-4;
			for (int i = 0; i < max; i++) {
				font.paint(buffer.charAt(i), x, y, g);
				x += fw;
				if (x>=mw) {
					x = 4;
					y += font.getCharHeight();
				}
			}
		}
	}

	/**
	 * Adds the given character as input to the current image source
	 * string.
	 * @param c the character to add
	 */
	public void append(final char c) {
		buffer.append(c);
		mode |= INPUT_RENDER_URL;
		repaint();
	}

	/**
	 * Removes the last character from the current image source string.
	 */
	public void remove() {
		final int l = buffer.length()-1;
		if (l>=0) {
			buffer.setLength(l);
			mode |= INPUT_RENDER_URL;
			repaint();
		}
	}

	/**
	 * Confirms the current input and returns to the menu screen.
	 */
	public void ok() {
		mode = INPUT_RENDER_FULL;
		main.setCurrent(menuCanvas, true);
		menuCanvas.setURL(buffer.toString());
	}

	/**
	 * Cancels the current input and returns to the menu screen.
	 */
	public void cancel() {
		mode = INPUT_RENDER_FULL;
		main.setCurrent(menuCanvas, true);
	}

	/**
	 * Sets the image source string to be displayed to the
	 * given string.
	 * @param url the source URL, must not be code <code>null</code>.
	 */
	public void setURL(final String url) {
		buffer.setLength(0);
		if (url != null) {
			buffer.append(url);
		} else {
			buffer.append(DEFAULT);			
		}
	}

}
