package de.engehausen.mobile.puzzling;

import javax.microedition.lcdui.Graphics;

/**
 * Buttons as alphabetic input key, upper case and lower case -
 * as available by {@link Font}.
 */
public class Keys extends AbstractButtonGroup implements Constants {

	private static final String[] CHAR_LAYOUT_UPPER = {
		"QWERTYUIOP",
		"ASDFGHJKL",
		"ZXCVBNM"
	};

	private static final String[] CHAR_LAYOUT_LOWER = {
		"qwertyuiop",
		"asdfghjkl",
		"zxcvbnm",
	};

	private static final String[] REST_LAYOUT = {
		"0123456789",
		":./",
		"<~>"
	};
		
	private static final int SPACE = 6;
	
	private static final String ALL_LOWER = "qwertyuiopasdfghjklzxcvbnm0123456789:./<~>";
	private static final String ALL_UPPER = "QWERTYUIOPASDFGHJKLZXCVBNM0123456789:./<~>";

	protected Font font;
	private final int height;
	private final InputCanvas canvas;
	private String[] layout;
	private String all;

	/**
	 * Creates the keys buttons.
	 * @param f the font used to show the keys.
	 * @param parent the input canvas using the button group.
	 * @param width the screen width
	 */
	public Keys(final Font f, final InputCanvas parent, final int width) {
		super(new Rectangle[ALL_LOWER.length()]);
		canvas = parent;
		layout = CHAR_LAYOUT_LOWER;
		all = ALL_LOWER;
		final int step = f.getCharHeight()+SPACE;
		final int w = f.getCharWidth();
		final int h = f.getCharHeight();
		int pos = 0;
		for (int i = 0; i < layout.length; i++) {
			pos += fillCenter(rects, w, h, width, step*i, layout[i], pos);
		}

		int y = 8+layout.length*step;
		pos += fillCenter(rects, w, h, width, y, REST_LAYOUT[0], pos);
		
		y += step+8;
		pos += fillLeft(rects, w, h, y, REST_LAYOUT[1], pos);
		pos += fillRight(rects, w, h, width, y, REST_LAYOUT[1], pos);
		
		font = f;
		height = y+step;
	}

	/**
	 * Toggles the keys between uppercase and lowercase.
	 */
	public void toggleLayout() {
		if (layout == CHAR_LAYOUT_UPPER) {
			layout = CHAR_LAYOUT_LOWER;
			all = ALL_LOWER;
		} else {
			layout = CHAR_LAYOUT_UPPER;			
			all = ALL_UPPER;
		}
	}

	// non-javadoc: see superclass
	public int getHeight() {
		return height;
	}

	// non-javadoc: see superclass
	public int getWidth() {
		return 0;
	}

	private int fillCenter(final Rectangle[] r, final int w, final int h, final int W, final int y, final String chars, final int pos) {
		final int max = chars.length();
		final int x = (W-(w+SPACE)*max)/2;
		for (int i = 0; i < max; i++) {
			r[pos+i] = new Rectangle(x+i*(w+SPACE), y, w, h);
		}
		return max;
	}
	
	private int fillLeft(final Rectangle[] r, final int w, final int h, final int y, final String chars, final int pos) {
		final int max = chars.length();
		for (int i = 0; i < max; i++) {
			r[pos+i] = new Rectangle(32+i*(w+SPACE), y, w, h);
		}
		return max;		
	}

	private int fillRight(final Rectangle[] r, final int w, final int h, final int W, final int y, final String chars, final int pos) {
		final int max = chars.length();
		final int x = W-32-max*(w+SPACE);
		for (int i = 0; i < max; i++) {
			r[pos+i] = new Rectangle(x+i*(w+SPACE), y, w, h);
		}
		return max;
	}

	public void button(final int idx) {
		final char cmd = all.charAt(idx);
		if (idx < all.length()-3) {
			canvas.append(cmd);
		} else {
			// command
			if (cmd == '<') {
				canvas.remove();
			} else if (cmd == '>') {
				canvas.ok();
			} else if (cmd == '~') {
				canvas.cancel();				
			}
		}
	}

	// non-javadoc: see superclass
	public void paint(final Graphics g) {
		g.setColor(BLACK);
		g.fillRect(0, 0, canvas.getWidth(), height);
		g.setColor(GRAY);
		for (int i = rects.length-1; i>=0; i--) {
			g.fillRect(rects[i].x-1, rects[i].y-1, 26, 26);
			font.paint(all.charAt(i), rects[i].x, rects[i].y, g);
		}
	}

}
