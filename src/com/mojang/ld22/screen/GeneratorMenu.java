package com.mojang.ld22.screen;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import com.mojang.ld22.Game;
import com.mojang.ld22.GameContainer;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class GeneratorMenu extends Menu {
	private Menu parent;

	private static AtomicBoolean generatorLocked = new AtomicBoolean();
	private AtomicBoolean loaded;
	
	private int ticks;
	
	public GeneratorMenu(Menu parent) {
		this.parent = parent;
		
		// generate a game in the background
		loaded = new AtomicBoolean();
		loaded.set(false);
		parent.game.setSetup(GameContainer.getInstance().getSetup());
		Thread thread = new Thread() {
			public void run() {
				// generate a new world
				game.resetGame();
				
				loaded.set(true);
				generatorLocked.set(false);
			};
		};
		
		// start only one generator
		if (generatorLocked.compareAndSet(false, true)) {
			thread.start();
		}
	}

	public void tick() {
		ticks++;
		if (loaded.get()) {
			DialogMenu welcome = new DialogMenu();
			welcome.setTitle("Introduction");
			welcome.setText("You have awakened.\n\n" +
				"Why? Where?\nYou have no idea.\n" +
				"\nThe sun seems to be rising up " +
				"and your dwarven stomach is in need of some ale.\n" +
				"\nBut it will be dark again soon so you better find a place to stay. " +
				"Dark creatures come out at night. Dangerous creatures.");
			game.setMenu(welcome);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		int marginX = (screen.w - 160) / 2;
		
		{ // game title
			int h = 2;
			int w = 13;
			int titleColor = Color.get(0, 010, 131, 551);
			int xo = (screen.w - w * 8) / 2;
			int yo = 24;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0);
				}
			}
		}
		
		{ // logo image
			int h = 8;
			int w = 7;
			int titleColor = Color.get(200, 421, 551, 0);
			int xo = (screen.w - w * 8) / 2;
			int yo = 48;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					screen.render(xo + x * 8, yo + y * 8, (x+16) + (y + 6) * 32, titleColor, 0);
				}
			}
		}
		
		String dots = "";
		for (int i = 0; i < ticks/40 % 4; i++) {
			dots += ".";
		}
		Font.draw("generating world" + dots, screen, 	marginX +  0 * 8 + 4, 18 * 8, Color.get(0, 522, 522, 522));
	}
}
