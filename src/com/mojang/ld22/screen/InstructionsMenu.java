package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class InstructionsMenu extends Menu {
	private Menu parent;

	public InstructionsMenu(Menu parent) {
		this.parent = parent;
	}

	public void tick() {
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		int marginX = 20;
		Font.draw("HOW TO PLAY", screen, 	marginX + 4 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555));
		Font.drawFitted("Move your character with the arrow keys, " +
			"press C to attack and X to open the inventory and to use items.\n" +
			"Select an item in the inventory to equip it.\n" +
			"\n" +
			"Kill the Ale wizard and steal his epic beer to win!",
			screen,
			marginX +  0 * 8 + 4, 3 * 8,
			screen.w - 2*marginX, screen.h - 100,
			Color.get(0, 333, 333, 333));
	}
}
