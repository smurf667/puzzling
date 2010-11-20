package de.engehausen.mobile.puzzling;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Model representing the tiles of an image.
 * The model also (somewhat uncleanly, but conveniently) tracks
 * what tiles have been painted.
 */
public class Model implements Constants {
	
	private static final int[][] CROSSHAIR = {
		{ -1, 0 }, { 0, -1 }, { 1, 0 }, { 0, 1 }
	};

	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private final Image[][] template;
	private final Image[][] elements;
	private final boolean[][] painted;
	private final Image src;
	private final int tileSize;
	private int holex, holey;

	/**
	 * Creates the model for the given image, splitting it into split x split tiles.
	 * @param image the image to use, must not be <code>null</code>.
	 * @param split the split level (3, 4 or 5)
	 * @throws IOException in case of error
	 */
	public Model(final Image image, final int split) throws IOException {
		src = image;
		template = new Image[split][split];
		elements = new Image[split][split];
		painted = new boolean[split][split];
		tileSize = src.getWidth() / split;
		for (int y = 0; y < split; y++) {
			for (int x = 0; x < split; x++) {
				if (x < split - 1 || y < split - 1) {
					template[y][x] = Image.createImage(src, x*tileSize, y*tileSize, tileSize, tileSize, Sprite.TRANS_NONE);
				}
			}
		}
	}
	
	/**
	 * Returns the original image the tiles of the model base on.
	 * @return the original image the tiles of the model base on.
	 */
	public Image getSourceImage() {
		return src;
	}
	
	/**
	 * The size of a tile (since it is quadratic, this value is enough).
	 * @return the size of a tile
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * The number of tiles per row/column.
	 * @return the number of tiles per row/column.
	 */
	public int getTileCount() {
		return template.length;
	}
	
	/**
	 * Returns the tile at the given position.
	 * @param x the x position
	 * @param y the y position
	 * @return the tile, or <code>null</code> if the hole is at this position.
	 */
	public Image getTile(final int x, final int y) {
		final Image result;
		if (x < template.length && y < template.length) {
			result = elements[y][x];			
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Reset the model state and reshuffle the tiles.
	 */
	public void reset() {
		for (int y = 0; y < template.length; y++) {
			for (int x = 0; x < template.length; x++) {
				elements[y][x] = template[y][x];
				painted[y][x] = false;
			}
		}
		int l = template.length-1;
		holex = holey = l;
		int last = 0;
		l++;
		for (int i = l*l*10; i > 0; i--) {
			last = randomSwap(last);
		}
		l--;
		// move empty field to right, bottom
		while (holex < l) {
			moveHole(holex+1, holey);
		}
		while (holey < l) {
			moveHole(holex, holey+1);
		}
	}
	
	/**
	 * Moves the hole from its current position to the given
	 * new position.
	 * @param sx the new x position of the hole
	 * @param sy the new y position of the hole
	 */
	public void moveHole(final int sx, final int sy) {
		elements[holey][holex] = elements[sy][sx];
		elements[sy][sx] = null;
		painted[sy][sx] = false;
		painted[holey][holex] = false;
		holex = sx;
		holey = sy;
	}
	
	/**
	 * Indicates that the tile at the given position has been painted.
	 * @param x the x position of the tile
	 * @param y the y position of the tile
	 */
	public void setPainted(final int x, final int y) {
		painted[y][x] = true;
	}

	/**
	 * Returns whether the tile at the given position is considered as having been painted.
	 * @param x the x position of the tile
	 * @param y the y position of the tile
	 * @return <code>true</code> if the tile has been painted
	 */
	public boolean isPainted(final int x, final int y) {
		return painted[y][x];
	}

	/**
	 * Check if the puzzle is solved.
	 * @return <code>true</code> if the puzzle has been solved.
	 */
	public boolean isSolved() {
		final int max = template.length-1;
		if (holex == max && holey == max) {
			for (int y = template.length-1; y >= 0; y--) {
				for (int x = template.length-1; x >= 0; x--) {
					if (template[y][x] != elements[y][x]) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Move the hole into a random direction.
	 * @param last the last position index of the hole
	 * @return the last position index of the hole
	 */
	protected int randomSwap(final int last) {
		int move = RANDOM.nextInt(4), sx, sy;
		do {			
			move = (move+1)%4;
			sx = holex+CROSSHAIR[move][0];
			sy = holey+CROSSHAIR[move][1];
		} while (move == last || sx < 0 || sy < 0 || sx == template.length || sy == template.length);
		moveHole(sx, sy);
		return (move+2)%4;
	}

}
