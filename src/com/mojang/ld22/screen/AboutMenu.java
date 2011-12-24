package com.mojang.ld22.screen;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.ld22.Game;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class AboutMenu extends Menu {
	private Menu parent;

	public static final int MINIGAME_WIDTH = Game.WIDTH >> 4;
	public static final int MINIGAME_HEIGHT = 6;
	private Level miniGame;
	private Screen miniScreen;
	private int tickCount;
	
	public AboutMenu(Menu parent) {
		this.parent = parent;
		this.miniGame = new Level(64, 64, 0, null);
		this.miniGame.trySpawn(10000);
		// simulate some history
		for (int i = 0; i < 1000; i++) {
			miniGame.tick();
		}
		try {
			miniScreen = new Screen(MINIGAME_WIDTH * 16, MINIGAME_HEIGHT * 16, new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/icons.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		tickCount++;
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent);
		}
		miniGame.tick();
		Tile.tickCount++;
	}

	public void render(Screen screen) {
		screen.clear(0);

		int marginX = (screen.w - 160) / 2;
		Font.draw("About Alecraft", screen, 	marginX +  2 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555));
		Font.draw("Originally Minicraft", screen, 	marginX +  0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333));
		Font.draw("by Markus Persson", screen, 	marginX +  0 * 8 + 4, 4 * 8, Color.get(0, 333, 333, 333));
		Font.draw("For the 22'nd ludum", screen, 	marginX +  0 * 8 + 4, 5 * 8, Color.get(0, 333, 333, 333));
		Font.draw("dare competition in", screen, 	marginX +  0 * 8 + 4, 6 * 8, Color.get(0, 333, 333, 333));
		Font.draw("december 2011.", screen, 	marginX +  0 * 8 + 4, 7 * 8, Color.get(0, 333, 333, 333));
		Font.draw("Modded and enhanced", screen, 	marginX +  0 * 8 + 4, 10 * 8, Color.get(0, 333, 333, 333));
		Font.draw("by David Nemecek.", screen, 	marginX +  0 * 8 + 4, 11 * 8, Color.get(0, 333, 333, 333));
		
		int xScroll = (int)(Math.cos((tickCount / 10000.0) * 2*Math.PI) * (miniGame.w * 8) / 2) + (miniGame.w * 8) / 2 + MINIGAME_WIDTH * 16 / 2;
		int yScroll = (int)(Math.sin((tickCount / 10000.0) * 2*Math.PI) * (miniGame.h * 8) / 2) + (miniGame.h * 8) / 2 + MINIGAME_HEIGHT * 16 / 2;
		miniGame.renderBackground(miniScreen, xScroll, yScroll);
		miniGame.renderSprites(miniScreen, xScroll, yScroll);
		miniScreen.copyRect(screen, 5, Game.HEIGHT - MINIGAME_HEIGHT*16 - 5, MINIGAME_WIDTH * 16, MINIGAME_HEIGHT * 16);
	}
}
