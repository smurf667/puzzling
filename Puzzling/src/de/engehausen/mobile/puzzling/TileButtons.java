package de.engehausen.mobile.puzzling;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * A tile matrix with the images that can be "played".
 */
public class TileButtons extends AbstractButtonGroup implements Constants {

	private static final String HTTP_PREFIX = "http:";
	
	private final String[] sources;
	private final Image[] images;
	private final WeakReference[] bigimages;
	private final boolean[] painted;
	private final MenuCanvas canvas;
	private final int height;
	private final Image wait;

	/**
	 * Creates the tile buttons.
	 * @param aDatabase the database providing the image sources, must not be <code>null</code>.
	 * @param parent the menu canvas using the tile buttons, must not be <code>null</code>.
	 * @param rectangles the rectangles for the tile buttons, must not be <code>null</code>.
	 * @throws IOException in case of error
	 */
	public TileButtons(final Database aDatabase, final MenuCanvas parent, final Rectangle[] rectangles) throws IOException {
		super(rectangles);
		canvas = parent;
		sources = aDatabase.getSources();
		
		images = new Image[rectangles.length];
		bigimages = new WeakReference[rectangles.length];
		painted = new boolean[rectangles.length];
		
		int tmp = 0;
		for (int i = rectangles.length-1; i>=0; i--) {
			if (rectangles[i].y > tmp) {
				tmp = rectangles[i].y;
			}
		}
		height = tmp+64;
		wait = Image.createImage("/clock.png");
	}

	/**
	 * Removes the image source at the given index. This normally
	 * <i>writes through to the "database"</i>.
	 * @param idx the index of the image source. 
	 */
	public void remove(final int idx) {
		set(idx, null);
	}

	/**
	 * Reset the state of the buttons.
	 */
	public void reset() {
		for (int i = painted.length-1; i>=0; i--) {
			painted[i] = false;
		}
	}

	/**
	 * Sets the image source at the given index. This normally
	 * <i>writes through to the "database"</i>.
	 * @param idx the index of the image source. 
	 * @param source the image source string
	 */
	public void set(final int idx, final String source) {
		sources[idx] = source;
		images[idx] = null;
		bigimages[idx] = null;
		painted[idx] = false;
	}

	// non-javadoc: see superclass
	public int getHeight() {
		return height;
	}

	// non-javadoc: see superclass
	public int getWidth() {
		return 0;
	}

	// non-javadoc: see superclass
	public void button(final int idx) {
		canvas.setSelection(idx);
	}

	/**
	 * Returns the image source URL at the given index
	 * @param idx the index of the image source
	 * @return the image source (may be <code>null</code>).
	 */
	public String getURL(final int idx) {
		return sources[idx];
	}

	// non-javadoc: see superclass
	public void paint(final Graphics g) {
		for (int i = 0; i < 16; i++) {
			if (!painted[i]) {
				if (sources[i] != null) {
					final Image img = getImage(i);
					if (img != null) {
						g.drawImage(img, 20+(i%4)*72, (i/4)*72, Sprite.TRANS_NONE);	
						painted[i] = (img != wait); // if wait img then still loading...
					}
				} else {
					g.setColor(BLACK);
					final int x = 20+(i%4)*72;
					final int y = (i/4)*72;
					g.fillRect(x, y, 64, 64);
					g.setColor(GRAY);
					g.drawRect(x, y, 64, 64);
				}
			}
		}
	}

	/**
	 * Returns the big image used for playing the tiles.
	 * @param idx the index of the image
	 * @return the big image
	 */
	public Image getBigImage(final int idx) {
		Image result = (Image) (bigimages[idx]!=null?bigimages[idx].get():null);
		if (result == null && sources[idx] != null) {
			try {
				final InputStream stream = getImageInputStream(sources[idx]);
				final Image img;
				try {
					img = Image.createImage(stream);
				} finally {
					stream.close();
				}
				result = scaleImage(img, 320, 320);
				bigimages[idx]= new WeakReference(result);
			} catch (IOException e) {
				result = scaleImage(wait, 320, 320);
				bigimages[idx]= new WeakReference(result);
			} catch (SecurityException e) {
				result = scaleImage(wait, 320, 320);
				bigimages[idx]= new WeakReference(result);
			}
		}
		return result;
	}
	
	private InputStream getImageInputStream(final String source) throws IOException {
		final InputStream result;
		if (source.charAt(0) == '/') {
			result = getClass().getResourceAsStream(source);
		} else if (source.startsWith(HTTP_PREFIX)) {
			result = getHttpInputStream(source);
		} else {
			// unknown, let the connector try it...
			result = Connector.openInputStream(source);
		}
		return result;
	}
	
	/**
	 * Retrieve a resource via HTTP. The method tries to deal with
	 * redirects.
	 * @param url the URL of the resource to fetch
	 * @return the input stream with the resources' data
	 * @throws IOException in case of error
	 */
	private InputStream getHttpInputStream(final String url) throws IOException {
		try {
			final HttpConnection conn = (HttpConnection) Connector.open(url);
			try {
				final int status = conn.getResponseCode();
				final InputStream result;
				if (status == HttpConnection.HTTP_OK) {
					result = conn.openInputStream();
				} else if (status >= HttpConnection.HTTP_MULT_CHOICE && status < HttpConnection.HTTP_BAD_REQUEST) {
					final String loc = conn.getHeaderField("Location");
					if (loc != null) {
						result = getHttpInputStream(loc);
					} else {
						throw new IOException();
					}
				} else {
					throw new IOException();
				}			
				return result;
			} finally {
				conn.close();			
			}			
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Returns a thumb nail image of a playable image.
	 * @param idx the index of the thumb nail image.
	 * @return a thumb nail image; may be the "sand clock image" if
	 * the image has to be loaded first.
	 */
	protected Image getImage(final int idx) {
		Image result = images[idx];
		if (result == null && sources[idx] != null) {
			new Thread(new ImageGetter(canvas, this, idx)).start();
			images[idx] = wait;
			result = wait;
		}
		return result;
	}

	protected Image loadImage(final int idx) {
		final Image img = getBigImage(idx);
		final Image result;
		if (img != null) {
			if (images[idx] == wait || images[idx] == null) {
				result = scaleImage(img, 64, 64);
				images[idx] = result;
			} else {
				result = images[idx];
			}
		} else {
			result = null;
		}
		return result;
	}		

	/**
	 * Bresenham image scaling.
	 * @param source the source image, must not be <code>null</code>.
	 * @param width the new image width
	 * @param height the new image height
	 * @return the scaled image
	 */
	private static Image scaleImage(final Image source, final int width, final int height) {
		final int sheight = source.getHeight();
		final int swidth = source.getWidth();
		final Image result;
		if (sheight != height || swidth != width) {
			final int[] rawInput = new int[sheight*swidth];
			source.getRGB(rawInput, 0, swidth, 0, 0, swidth, sheight);

			final int[] rawOutput = new int[width*height];

			final int yd = (sheight / height) * swidth - swidth;
			final int yr = sheight % height;
			final int xd = swidth / width;
			final int xr = swidth % width;
			int outOffset = 0;
			int inOffset = 0;

			for (int y = height, ye = 0; y > 0; y--) {
				for (int x = width, xe = 0; x > 0; x--) {
					rawOutput[outOffset++] = rawInput[inOffset];
					inOffset += xd;
					xe += xr;
					if (xe >= width) {
						xe -= width;
						inOffset++;
					}
				}
				inOffset += yd;
				ye += yr;
				if (ye >= height) {
					ye -= height;
					inOffset += swidth;
				}
			}
			result = Image.createRGBImage(rawOutput, width, height, false);			
		} else {
			result = source;
		}
		return result;
	}

	private static class ImageGetter implements Runnable {
		
		private final MenuCanvas canvas;
		private final TileButtons buttons;
		private final int idx;
		
		public ImageGetter(final MenuCanvas aCanvas, final TileButtons aButtons, final int index) {
			canvas = aCanvas;
			buttons = aButtons;
			idx = index;
		}

		public void run() {
			buttons.loadImage(idx);
			canvas.tilesChanged();
		}
		
	}

}
