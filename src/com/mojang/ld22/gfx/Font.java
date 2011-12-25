package com.mojang.ld22.gfx;

public class Font {
	private static final String EMPTY_CHAR = "b";
	private static String chars = "" + //
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ      " + //
			"0123456789.,!?'\"-+=/\\%()<>:;  \n"+EMPTY_CHAR + //
			"";

	public static final int CHAR_WIDTH = 8;
	public static final int LINE_HEIGHT = CHAR_WIDTH + 2; 
	public static int DEFAULT_BACKGROUND_COLOR = Color.get(5, 5, 5, 5);
	public static int DEFAULT_BORDER_COLOR = Color.get(-1, 1, 5, 445);
	public static int DEFAULT_TEXT_COLOR = Color.get(-1, 555, 555, 555);
	public static int DEFAULT_TITLE_COLOR = Color.get(5, 5, 5, 550);
	
	public static void draw(String msg, Screen screen, int x, int y, int col) {
		msg = msg.toUpperCase();
		for (int i = 0; i < msg.length(); i++) {
			int ix = chars.indexOf(msg.charAt(i));
			if (ix < 0) {
				ix = chars.indexOf(EMPTY_CHAR);
			}
			if (ix >= 0) {
				screen.render(x + i * 8, y, ix + 30 * 32, col, 0);
			}
		}
	}
	
	public static void drawFitted(String msg, Screen screen, int x, int y, int w, int h, int col) {
		msg.toUpperCase();

		int wc = w / CHAR_WIDTH;
		int xt = x;
		int yt = y;
		while (msg.length() > 0) {
			// prepare line string
			int thisLineEnd;
			int nextLineStart;
			// a) end of text
			thisLineEnd = nextLineStart = (wc > msg.length()) ? msg.length() : wc;
			// b) end of line
			int newLine = msg.indexOf("\n");
			if (newLine >= 0 && newLine < thisLineEnd) {
				thisLineEnd = newLine;
				nextLineStart = newLine+1;
			}
			// c) line can be built from some of the words
			int spacePos = msg.indexOf(" ");
			if (spacePos != -1 && spacePos <= wc && thisLineEnd == wc) {
				String[] words = msg.split(" ");
				String lastWord = words[0];
				int lineLength = lastWord.length();
				for (int i = 1; i < words.length; i++) {
					// grow the line
					lastWord = words[i];
					lineLength += lastWord.length() + 1;
					// end the growth on overflow
					if (lineLength > wc) {
						// step back
						lineLength -= lastWord.length() + 1;
						// done!
						thisLineEnd = lineLength;
						nextLineStart = lineLength+1;
						break;
					}
				}
			}
			// d) even the first word is too long => split it
			// done automatically
			
			// get the line
			String line = msg.substring(0, thisLineEnd);
			msg = msg.substring(nextLineStart);
			
			// draw the line
			draw(line, screen, xt, yt, col);
			
			// move to another line
			yt += LINE_HEIGHT;
			
			// finished on overflow
			if (yt >= y+h) {
				break;
			}
		}
	}

	public static void renderFrame(Screen screen, String title, int x0, int y0, int x1, int y1)
	{
		renderFrame(screen, title, x0, y0, x1, y1, DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR, DEFAULT_TITLE_COLOR);
	}
	
	public static void renderFrame(Screen screen, String title, int x0, int y0,
			int x1, int y1, int colBackground, int colBorder, int colTitle) {
		for (int y = y0; y <= y1; y++) {
			for (int x = x0; x <= x1; x++) {
				if (x == x0 && y == y0)
					screen.render(x * 8, y * 8, 0 + 13 * 32, colBorder, 0);
				else if (x == x1 && y == y0)
					screen.render(x * 8, y * 8, 0 + 13 * 32, colBorder, 1);
				else if (x == x0 && y == y1)
					screen.render(x * 8, y * 8, 0 + 13 * 32, colBorder, 2);
				else if (x == x1 && y == y1)
					screen.render(x * 8, y * 8, 0 + 13 * 32, colBorder, 3);
				else if (y == y0)
					screen.render(x * 8, y * 8, 1 + 13 * 32, colBorder, 0);
				else if (y == y1)
					screen.render(x * 8, y * 8, 1 + 13 * 32, colBorder, 2);
				else if (x == x0)
					screen.render(x * 8, y * 8, 2 + 13 * 32, colBorder, 0);
				else if (x == x1)
					screen.render(x * 8, y * 8, 2 + 13 * 32, colBorder, 1);
				else
					screen.render(x * 8, y * 8, 2 + 13 * 32, colBackground, 1);
			}
		}

		draw(title, screen, x0 * 8 + 8, y0 * 8, colTitle);

	}
}