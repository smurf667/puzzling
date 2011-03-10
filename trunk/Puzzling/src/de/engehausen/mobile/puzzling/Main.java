package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Touch screen based n-puzzle game; requires HVGA (320x480)
 * screen resolution and - obviously - a touch screen.
 */
public class Main extends MIDlet {
	
	private final Display display;
	private final Database db;
	private Displayable current;
	private boolean splashed;
	
	/**
	 * Creates the MIDlet; most initializations will happen
	 * when {@link #startApp()} is called by the platform.
	 */
	public Main() {
		super();
		display = Display.getDisplay(this);
		db = Database.createDatabase();
		db.load();
	}

	/**
	 * Set the displayable to be displayed.
	 * @param displayable the displayable to display
	 * @param change whether or not to immediately show the displayable
	 */
	public void setCurrent(final Displayable displayable, final boolean change) {
		current = displayable;
		if (change) {
			display.setCurrent(displayable);
		}
	}

	// non-javadoc: see superclass
	protected void startApp() throws MIDletStateChangeException {
		if (splashed) {
			display.setCurrent(current);
		} else {
			splashed = true;
			try {
				final Splash splash = new Splash();
				splash.display(this);
				final Image[] buttonImages = splitImages("/buttons.png");
				final Font font = Font.getInstance();
				final MenuCanvas menu = new MenuCanvas(this, db, buttonImages);
				menu.setFullScreenMode(true);
				final InputCanvas ic = new InputCanvas(this, font);
				ic.setFullScreenMode(true);
				ic.setMenuCanvas(menu);
				menu.setInputCanvas(ic);
				final GameCanvas game = new GameCanvas(this, db, font, buttonImages);
				game.setFullScreenMode(true);
				menu.setGameCanvas(game);
				game.setMenuCanvas(menu);
				current = menu;
				splash.terminate();
			} catch (IOException e) {
				throw new MIDletStateChangeException(e.getMessage());
			}
		}
	}

	// non-javadoc: see superclass
	protected void pauseApp() {
		// do nothing
	}

	// non-javadoc: see superclass
	protected void destroyApp(final boolean force) throws MIDletStateChangeException {
		db.save();
//		db.delete();
	}
	
	private static Image[] splitImages(final String src) throws IOException {
		final Image source = Image.createImage(src);
		final Image[] result = new Image[11];
		final int t = source.getWidth();
		final int h = source.getHeight();
		final int w = t/11;
		for (int x = 0; x < t; x+= w) {
			result[x/w] = Image.createImage(source, x, 0, w, h, Sprite.TRANS_NONE);
		}
		return result;
	}

	
}
