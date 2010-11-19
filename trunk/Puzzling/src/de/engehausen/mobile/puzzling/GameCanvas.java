package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

/**
 * The game canvas showing the n*n tiles, the game control
 * buttons and a timer output.
 */
public class GameCanvas extends Canvas implements Constants {

	private static final String WAVE = "/slide.wav";
	private static final String MEDIA_TYPE = "audio/x-wav";

	private static final int BUTTON_HEIGHT = 72;
	
	private final Font font;	
	private final GameButtons button;
	private final int textX, textY;
	private int renderMode;
	private Model model;
	private Timer timer;
	private int px, py; // press x,y
	private int rx, ry; // release x,y
	private final Main main;
	private MenuCanvas next;
	private final int[] scores;
	private Player slideSound;

	/**
	 * Creates the game canvas for the given main control object.
	 * @param aMain the main control object, must not be <code>null</code>
	 * @param db the database, must not be <code>null</code>
	 * @param aFont the font, must not be <code>null</code>
	 * @param buttons the buttons image list, must not be <code>null</code>
	 * @throws IOException in case of error
	 */
	public GameCanvas(final Main aMain, final Database db, final Font aFont, final Image[] buttons) throws IOException {
		super();
		main = aMain;
		font = aFont;
		button = new GameButtons(this, buttons);
		renderMode = GAME_RENDER_MODE_FULL;
		textX = (getWidth() - 5*aFont.getCharWidth())/2;
		textY = (80 - aFont.getCharHeight())/2;
		scores = db.getScores();
	}
	
	/**
	 * Sets the canvas menu (to be shown when returning to the menu).
	 * @param aCanvas the menu canvas, must not be <code>null</code>
	 */
	public void setMenuCanvas(final MenuCanvas aCanvas) {
		next = aCanvas;
	}
	
	/**
	 * Quits the current game.
	 */
	public void quit() {
		if (timer != null) {
			timer.stop();
		}
		model = null;
		main.setCurrent(next, true);
		renderMode = GAME_RENDER_MODE_FULL;
	}

	/**
	 * Resets the game canvas for use with the given model (also
	 * shuffles the tiles for the model)
	 * @param aModel the new model to use, must not be <code>null</code>
	 */
	public void reset(final Model aModel) {
		if (timer != null) {
			timer.stop();
		}
		aModel.reset();
		model = aModel;
		renderMode = GAME_RENDER_MODE_FULL;
		timer = new Timer(this, font);
		new Thread(timer).start();
	}
	
	/**
	 * Resets the game canvas.
	 */
	public void reset() {
		if (model != null) {
			reset(model);
		}
	}
	
	/**
	 * Request a repaint with the given rendering flags.
	 * @param renderModeFlags the rendering flags
	 */
	public void repaint(final int renderModeFlags) {
		renderMode |= renderModeFlags;
		repaint();
	}

	/**
	 * Display the puzzle as solved.
	 */
	public void solve() {
		timer.cancel();
		renderMode |= GAME_RENDER_MODE_SHOW_IMAGE;
		repaint();		
	}

	/**
	 * Invoked when the user touches the screen.
	 * @param x the x position of the touch point
	 * @param y the y position of the touch point
	 */
	protected void pointerPressed(final int x, final int y) {
		px = x;
		py = y;
	}

	/**
	 * Invoked when the user stops touching the screen.
	 * @param x the x position of the touch point
	 * @param y the y position of the touch point
	 */
	protected void pointerReleased(final int x, final int y) {
		rx = x;
		ry = y;
		final int height = getHeight();
		if (py < height-BUTTON_HEIGHT) {
			if (py > BUTTON_HEIGHT) {
				ry -= BUTTON_HEIGHT; // y offset compensation
				py -= BUTTON_HEIGHT;
				handleTileMove();
			}
		} else if (ry > height-BUTTON_HEIGHT) {
			ry -= height-BUTTON_HEIGHT; // y offset compensation
			handleButtons();
		}
	}
	
	/**
	 * Handles a possible tile move by analyzing the two points where the
	 * user touched and stopped touching the screen.
	 */
	protected void handleTileMove() {
		final int t = model.getTileCount();
		final int ts = model.getTileSize();
		final int x = px/ts;
		final int y = py/ts;
		if (model.getTile(x, y) != null) { // activated on a tile...
			final int dx = rx-px;
			final int dy = ry-py;
			if (dx*dy != 0) {
				final int nx, ny;
				if (dx*dx > dy*dy) {
					// horizontal move
					nx = dx>0?x+1:x-1;
					ny = y;
				} else {
					// vertical move
					nx = x;
					ny = dy>0?y+1:y-1;
				}
				if (nx >=0 && ny >=0 && nx < t && ny < t) {
					if (model.getTile(nx, ny) == null) {
						playSlideSound();
						model.moveHole(x, y);
						if (model.isSolved()) {
							timer.stop();
							renderMode |= GAME_RENDER_MODE_SHOW_IMAGE;
							final int time = timer.getTime();
							final int idx = model.getTileCount()-3;
							if (scores[idx]>time) {
								scores[idx] = time;
								renderMode |= GAME_RENDER_MODE_SHOW_RECORD;
							}
						} else {
							renderMode |= GAME_RENDER_MODE_TILE;
						}
						repaint();
					}
				}
			}
		}
	}

	protected void playSlideSound() {
		if (!button.isMuted()) {
			try {
				if (slideSound != null) {
					slideSound.start();
				} else {
					slideSound = Manager.createPlayer(GameCanvas.class.getResourceAsStream(WAVE), MEDIA_TYPE);
					slideSound.start();
				}
			} catch (IOException e) {
				// ignore this - no sound then...
			} catch (MediaException e) {
				// ignore this - no sound then...
			}
		}
	}
	
	protected void handleButtons() {
		button.handleButtons(rx, ry);
	}

	/**
	 * Paints the game canvas. Using several "rendering flags",
	 * the canvas is only painted where needed. This is done
	 * for performance reasons.
	 * @param g graphics instance, must not be <code>null</code>.
	 */
	protected void paint(final Graphics g) {
		g.setColor(BLACK);
		if (renderMode == GAME_RENDER_MODE_FULL) {
			// special case: render all - clear screen
			g.fillRect(0, 0, getWidth(), getHeight());			
		}
		if ( (renderMode&GAME_RENDER_MODE_TIME) != 0) {
			timePaint(g);
		}
		if ( (renderMode&GAME_RENDER_MODE_TILE) != 0) {
			tilesPaint(g);
		}
		if ( (renderMode&GAME_RENDER_MODE_SHOW_IMAGE) != 0) {
			imagePaint(g);
		}
		if ( (renderMode&GAME_RENDER_MODE_BUTTONS) != 0) {
			buttonPaint(g);
		}
		if ( (renderMode&GAME_RENDER_MODE_SHOW_RECORD) != 0) {
			recordPaint(g);
		}
	}
	
	protected void buttonPaint(final Graphics g) {
		final int off = getHeight()-BUTTON_HEIGHT;
		g.translate(0, off);
		try {			
			button.paint(g);
		} finally {
			g.translate(0, -off);
		}
		renderMode ^= GAME_RENDER_MODE_BUTTONS;
	}
	
	protected void imagePaint(final Graphics g) {
		if (model != null) {
			g.drawImage(model.getSourceImage(), 0, BUTTON_HEIGHT-8, POSITIONING);
			renderMode ^= GAME_RENDER_MODE_SHOW_IMAGE;
		}
	}
	
	protected void timePaint(final Graphics g) {
		if (timer != null) {
			timer.timePaint(textX, textY, g);			
		}
		renderMode ^= GAME_RENDER_MODE_TIME;
	}

	protected void recordPaint(final Graphics g) {
		if (timer != null) {
			int px = textX-2;
			int py = textY-3;
			int w = timer.getWidth()+4;
			int h = timer.getHeight()+5;
			int col = 0xf00000;
			for (int i = 0; i < 8; i++) {
				g.setColor(col);
				g.drawRect(px, py, w, h);
				w += 2;
				h += 2;
				px--;
				py--;
				col -= 0x1c0000;
			}
		}
		renderMode ^= GAME_RENDER_MODE_SHOW_RECORD;
	}

	protected void tilesPaint(final Graphics g) {
		if (model != null) {
			final int step = model.getTileSize();
			final int max = model.getTileCount();
			for (int y = 0; y < max; y++) {
				final int yy = BUTTON_HEIGHT-8+y*step;
				for (int x = 0; x < max; x++) {
					if (!model.isPainted(x, y)) {
						model.setPainted(x, y);
						final Image img = model.getTile(x, y);
						if (img != null) {
							g.drawImage(img, x*step, yy, POSITIONING);
							g.drawLine(x*step, yy, x*step, yy+step);
							g.drawLine(x*step, yy, (x+1)*step, yy);
						} else {
							g.fillRect(x*step, yy, step, step);
						}						
					}
				}				
			}
		}		
		renderMode ^= GAME_RENDER_MODE_TILE;
	}
		
}
