package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class DialogMenu extends Menu {

	public static int DEFAULT_TEXT_COLOR = Color.get(-1, 555, 555, 555);
	public static int DEFAULT_TITLE_COLOR = Color.get(112, 445, 445, 445);
	public static int DEFAULT_BACKGROUND_COLOR = Color.get(112, 112, 112, 112);
	public static int DEFAULT_BORDER_COLOR = Color.get(-1, 2, 112, 445);
	
	private int inputDelay = 60;

	protected String text;
	protected String title;
	
	public DialogMenu() {
	}
	
	public DialogMenu(String text) {
		this.text = text;
	}
	
	public DialogMenu(String text, String title) {
		this.text = text;
		this.title = title;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	public void tick() {
		if (inputDelay > 0)
			inputDelay--;
		else if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(null);
		}
	}

	public void render(Screen screen)
	{
		renderTextBox(text, screen, 16, 16, screen.w-32, screen.h-48);	
	}
	
	protected void renderTextBox(String msg, Screen screen, int x, int y,
			int w, int h)
	{
		int padding = 8;
		int x0 = x;
		int y0 = y;
		int x1 = x+w;
		int y1 = y+h;
		Font.renderFrame(screen, title, x0/8, y0/8, x1/8-1, y1/8,
				DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR,
				DEFAULT_TITLE_COLOR);
		Font.drawFitted(text, screen, x0+padding, y0+padding + 5,
				x1-x0-padding*2, y1-y0-padding*2-10, DEFAULT_TEXT_COLOR);
	}
	
}
