package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The menu canvas shows the images that can be played as
 * thumb nails. Images can be removed or added. The level
 * of difficulty can be set, a game can be started or the
 * whole application can be ended here.
 */
public class MenuCanvas extends Canvas implements Constants {

	private static final int Y_START = 20;
	private static final int PM_OFFSET = 8+Y_START+4*72;
	
	private final PlusMinusButtons plusMinusButtons;
	private final TileButtons tileButtons;
	private final MenuButtons menuButtons;
	private final int menuOffset;
	private final Main main;
	private int mode;
	private int selection;
	private int oldSelection;
	private GameCanvas next;
	private InputCanvas input;
	
	/**
	 * Creates the menu canvas for the given main control object.
	 * @param aMain the main control object, must not be <code>null</code>
	 * @param db the database, must not be <code>null</code>
	 * @param buttons the buttons image list, must not be <code>null</code>
	 * @throws IOException in case of error
	 */
	public MenuCanvas(final Main aMain, final Database db, final Image[] buttons) throws IOException {
		super();
		main = aMain;
		final int width = getWidth();
		mode = MENU_RENDER_FULL;
		tileButtons = new TileButtons(db, this, createRects());
		plusMinusButtons = new PlusMinusButtons(this, buttons, width);
		menuButtons = new MenuButtons(this, buttons, width);
		menuOffset = getHeight()-menuButtons.getHeight()-Y_START+8;
	}

	/**
	 * Sets the game canvas to be displayed when a game is actually on.
	 * @param gameCanvas the game canvas, must not be <code>null</code>.
	 */
	public void setGameCanvas(final GameCanvas gameCanvas) {
		next = gameCanvas;
	}
	
	/**
	 * Sets the input canvas to be displayed when an image source is to be specified.
	 * @param inputCanvas the input canvas, must not be <code>null</code>.
	 */
	public void setInputCanvas(final InputCanvas inputCanvas) {
		input = inputCanvas;
	}
	
	/**
	 * Set the given URL at the currently selected position.
	 * @param url the URL to set.
	 */
	public void setURL(final String url) {
		tileButtons.set(selection, url);
		repaint();
	}
	
	/**
	 * Shows the screen to input an image source.
	 */
	public void inputURL() {		
		input.setURL(tileButtons.getURL(selection));
		main.setCurrent(input, true);
		resetView();
	}

	/**
	 * Play a game.
	 */
	public void play() {
		new Thread((new Runnable() {
			public void run() {
				final Image img = tileButtons.getBigImage(selection);
				if (img != null) {
					try {
						next.reset(new Model(img, menuButtons.getSplit()));
						main.setCurrent(next, true);
						mode = MENU_RENDER_FULL;
						tileButtons.reset();
				} catch (IOException e) {
						// ignore
					}
				}
			}
		})).start();
	}

	/**
	 * Sets the new selection position. Causes a repaint to
	 * indicate the new selection.
	 * @param newSelection the new selection position
	 */
	public void setSelection(final int newSelection) {
		oldSelection = selection;
		selection = newSelection;
		mode |= MENU_RENDER_SELECTION;
		repaint();
	}

	/**
	 * Returns the currently selected index (the index of the
	 * image source).
	 * @return the currently selected index
	 */
	public int getSelection() {
		return selection;
	}

	/**
	 * Request a repaint of the buttons.
	 */
	public void buttonsChanged() {
		mode |= MENU_RENDER_MAINBUTTONS;
		repaint();
	}

	/**
	 * Request a repaint of the tiles.
	 */
	public void tilesChanged() {
		mode |= MENU_RENDER_IMAGES;
		repaint();
	}

	/**
	 * Exit the whole application.
	 */
	public void exit() {
		try {
			main.destroyApp(false);
			main.notifyDestroyed();
		} catch (MIDletStateChangeException e) {
			// nothing we can do, exit with as much grace as is left...
		}
	}

	/**
	 * Remove the image source at the current selection.
	 * Causes a repaint to update the screen.
	 */
	public void removeSelection() {
		tileButtons.remove(selection);
		mode |= MENU_RENDER_IMAGES;
		repaint();
	}

	/**
	 * Resets the view such that on the next paint
	 * everything is repainted.
	 */
	protected void resetView() {
		mode = MENU_RENDER_FULL;
		tileButtons.reset();
	}

	// non-javadoc: see superclass
	protected void showNotify() {
		super.showNotify();
		resetView();
		repaint();
	}

	// non-javadoc: see superclass
	protected void pointerReleased(final int x, final int y) {
		if (y < tileButtons.getHeight()+Y_START) {
			tileButtons.handleButtons(x, y-Y_START); 
		} else if (y < PM_OFFSET+plusMinusButtons.getHeight()) {
			plusMinusButtons.handleButtons(x, y-PM_OFFSET);
		} else if (y < getHeight()-32) {
			menuButtons.handleButtons(x, getHeight()-32-y);
		}
	}
	
	// non-javadoc: see superclass
	protected void paint(final Graphics g) {
		g.setColor(BLACK);
		if (mode == MENU_RENDER_FULL) {
			g.fillRect(0, 0, getWidth(), getHeight());
			g.translate(0, PM_OFFSET);
			try {
				plusMinusButtons.paint(g);				
			} finally {
				g.translate(0, -PM_OFFSET);				
			}
		}
		if ((mode & MENU_RENDER_IMAGES) != 0) {
			mode ^= MENU_RENDER_IMAGES;
			g.translate(0, Y_START);
			try {
				tileButtons.paint(g);
			} finally {
				g.translate(0, -Y_START);
			}
		}
		if ((mode & MENU_RENDER_SELECTION) != 0) {
			mode ^= MENU_RENDER_SELECTION;
			if (oldSelection != selection) {
				paintSelection(g, oldSelection, MENU_SELECTION_BLACK);
				oldSelection = selection;
			}
			paintSelection(g, selection, MENU_SELECTION);
		}
		if ((mode & MENU_RENDER_MAINBUTTONS) != 0) {
			g.translate(0, menuOffset);
			try {
				menuButtons.paint(g);				
			} finally {
				g.translate(0, -menuOffset);
			}
			mode ^= MENU_RENDER_MAINBUTTONS;
		}
	}

	/**
	 * Paint the selection frame
	 * @param g the graphics instance, must not be <code>null</code>.
	 * @param s the selection index
	 * @param cols the selection colors, must not be <code>null</code>.
	 */
	protected void paintSelection(final Graphics g, final int s, final int[] cols) {
		int px = 20+(s%4)*72;
		int py = 20+(s/4)*72;
		int w = 63;
		for (int i = cols.length-1; i>=0; i--) {
			px--;
			py--;
			w += 2;
			g.setColor(cols[i]);
			g.drawRect(px, py, w, w);
		}
	}
	
	private static Rectangle[] createRects() {
		final Rectangle[] result = new Rectangle[16];
		for (int i = 0; i < 16; i++) {
			result[i] = new Rectangle(20+(i%4)*72, (i/4)*72, 64, 64);
		}
		return result;
	}

}
