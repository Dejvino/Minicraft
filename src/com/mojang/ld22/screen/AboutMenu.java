package com.mojang.ld22.screen;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	private AtomicBoolean miniLoaded;
	
	public AboutMenu(Menu parent) {
		this.parent = parent;
		
		// generate a game in the background
		miniLoaded = new AtomicBoolean();
		miniLoaded.set(false);
		Thread thread = new Thread() {
			public void run() {
				miniGame = new Level(128, 128, 0, null);
				miniGame.trySpawn(10000);
				// simulate some history
				for (int i = 0; i < 1000; i++) {
					miniGame.tick();
				}
				miniLoaded.set(true);
			};
		};
		thread.start();
		
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
		if (miniLoaded.get()) {
			miniGame.tick();
			Tile.tickCount++;
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		int marginX = 10;
		Font.draw("About Alecraft", screen, 	marginX + 4 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555));
		Font.drawFitted("Based on code of Minicraft by\n" +
				" Markus \"Notch\" Persson, 2011.\n\n" +
				"Modded and enhanced by\n David \"Dejvino\" Nemecek, 2011-2012",
			screen,
			marginX +  0 * 8 + 4, 3 * 8,
			screen.w - 2*marginX, screen.h - 100,
			Color.get(0, 333, 333, 333));
		
		if (miniLoaded.get()) {
			int xScroll = (int)(Math.cos((tickCount / 10000.0) * 2*Math.PI) * (miniGame.w * 8) / 2) + (miniGame.w * 8) / 2 + MINIGAME_WIDTH * 16 / 2;
			int yScroll = (int)(Math.sin((tickCount / 10000.0) * 2*Math.PI) * (miniGame.h * 8) / 2) + (miniGame.h * 8) / 2 + MINIGAME_HEIGHT * 16 / 2;
			miniGame.renderBackground(miniScreen, xScroll, yScroll);
			miniGame.renderSprites(miniScreen, xScroll, yScroll);
			miniScreen.copyRect(screen, 5, Game.HEIGHT - MINIGAME_HEIGHT*16 - 5, MINIGAME_WIDTH * 16, MINIGAME_HEIGHT * 16);
		} else {
			String dots = "";
			for (int i = 0; i < tickCount/40 % 4; i++) {
				dots += ".";
			}
			Font.draw("generating world"+dots, screen, 	marginX +  0 * 8 + 4, 19 * 8, Color.get(0, 522, 522, 522));
		}
	}
}
