package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Custom font to paint strings to a graphics instance.
 * The font has a very limited set of characters (see {@link #CHARS}).
 * The font is mono spaced, and each character is 24x24 pixels.
 */
public final class Font implements Constants {
	
	private final Image[] characters;
	private final int charWidth;
	private final int charHeight;

	/**
	 * The available characters of the font. Please note
	 * that ~, &lt; and &gt; are special symbols. Whitespace
	 * is supported although not contained in this string.
	 */
	public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:./~><abcdefghijklmnopqrstuvwxyz";
	
	private static Font instance;

	/**
	 * Returns a font instance.
	 * @return a font instance, never <code>null</code>
	 * @throws IOException in case of error
	 */
	public static Font getInstance() throws IOException {
		if (instance != null) {
			return instance;
		} else {
			return instance = loadFont();
		}
	}
	
	private static Font loadFont() throws IOException {
		return new Font("/font.png", 24, 24, CHARS);
	}
	
	private Font(final String file, final int width, final int height, final String chars) throws IOException {
		final Image image = Image.createImage(file);
		if (image == null) {
			throw new IOException(file);
		}
		charWidth = width;
		charHeight = height;
		int max = 0;
		for (int i = chars.length()-1; i>=0; i--) {
			final int c = chars.charAt(i);
			if (c > max) {
				max = c;
			}
		}
		characters = new Image[max+1];
		dissect(image, width, height, chars);
	}
	
	private void dissect(final Image image, final int width, final int height, final String chars) {
		int px = 0;
		int py = 0;
		final int s = chars.length();
		final int mx = image.getWidth();
		for (int i = 0; i < s; i++) {
			final int pos = chars.charAt(i);
			characters[pos] = Image.createImage(image, px, py, width, height, Sprite.TRANS_NONE);
			px += width;
			if (px>=mx) {
				px = 0;
				py += height;
			}
		}
	}
	
	/**
	 * Paints the given string to the graphics instance at the
	 * given coordinates.
	 * @param text the text to paint, must not be <code>null</code>
	 * @param x the beginning position of the text
	 * @param y the beginning position of the text
	 * @param graphics the graphics instance, must not be <code>null</code>
	 */
	public void paint(final String text, final int x, final int y, final Graphics graphics) {
		final int mx = text.length();
		int cx = x;
		for (int i = 0; i < mx; i++) {
			final int c = text.charAt(i);
			if (c < characters.length) {
				final Image img = characters[c];
				if (img != null) {
					graphics.drawImage(characters[c], cx, y, POSITIONING);
				}
				cx += charWidth;
			}
		}
	}

	/**
	 * Paints the given character to the graphics instance at the
	 * given coordinates.
	 * @param c the character to paint
	 * @param x the position of the character
	 * @param y the position of the character
	 * @param graphics the graphics instance, must not be <code>null</code>
	 */
	public void paint(final char c, final int x, final int y, final Graphics graphics) {
		final Image img = characters[c];
		if (img != null) {
			graphics.drawImage(characters[c], x, y, POSITIONING);
		}
	}

	/**
	 * The character width.
	 * @return the character width.
	 */
	public int getCharWidth() {
		return charWidth;
	}

	/**
	 * The character height.
	 * @return the character height.
	 */
	public int getCharHeight() {
		return charHeight;
	}

}
