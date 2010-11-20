package de.engehausen.mobile.puzzling;

import javax.microedition.lcdui.Graphics;

/**
 * Commonly used constants in the game.
 * No further documentation as the fields speak for themselves.
 */
public interface Constants {
	
	int GAME_RENDER_MODE_TIME = 1;
	int GAME_RENDER_MODE_BUTTONS = 2;
	int GAME_RENDER_MODE_TILE = 4;
	int GAME_RENDER_MODE_SHOW_IMAGE = 8;
	int GAME_RENDER_MODE_SHOW_RECORD = 16;

	int GAME_RENDER_MODE_FULL = GAME_RENDER_MODE_TIME|GAME_RENDER_MODE_BUTTONS|GAME_RENDER_MODE_TILE;

	int MENU_RENDER_IMAGES = 1;
	int MENU_RENDER_MAINBUTTONS = 2;
	int MENU_RENDER_SELECTION = 4;
	
	int MENU_RENDER_FULL = MENU_RENDER_IMAGES|MENU_RENDER_MAINBUTTONS|MENU_RENDER_SELECTION;
	
	int MENU_SELECTION_BLACK[] = {
			0x0, 0x0, 0x0
	};

	int MENU_SELECTION[] = {
			0x1c2e9b, 0x405af2, 0x1ce29b
	};

	int BLACK = 0x000000;
	int GRAY = 0x303030;
	int WHITE = 0xffffff;
	int DARK_RED = 0x500000;

	int INPUT_RENDER_KEYS = 1;
	int INPUT_RENDER_URL = 2;
	int INPUT_RENDER_FULL = INPUT_RENDER_KEYS|INPUT_RENDER_URL;
	
	int POSITIONING = Graphics.TOP|Graphics.LEFT;

}
