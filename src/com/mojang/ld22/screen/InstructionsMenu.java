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

		int marginX = (screen.w - 160) / 2;
		Font.draw("HOW TO PLAY", screen, 	marginX + 4 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555));
		Font.draw("Move your character", screen, 	marginX +  0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333));
		Font.draw("with the arrow keys", screen, 	marginX +  0 * 8 + 4, 4 * 8, Color.get(0, 333, 333, 333));
		Font.draw("press C to attack", screen, 	marginX +  0 * 8 + 4, 5 * 8, Color.get(0, 333, 333, 333));
		Font.draw("and X to open the", screen, 	marginX +  0 * 8 + 4, 6 * 8, Color.get(0, 333, 333, 333));
		Font.draw("inventory and to", screen, 	marginX +  0 * 8 + 4, 7 * 8, Color.get(0, 333, 333, 333));
		Font.draw("use items.", screen, 	marginX +  0 * 8 + 4, 8 * 8, Color.get(0, 333, 333, 333));
		Font.draw("Select an item in", screen, 	marginX +  0 * 8 + 4, 9 * 8, Color.get(0, 333, 333, 333));
		Font.draw("the inventory to", screen, 	marginX +  0 * 8 + 4, 10 * 8, Color.get(0, 333, 333, 333));
		Font.draw("equip it.", screen, 	marginX +  0 * 8 + 4, 11 * 8, Color.get(0, 333, 333, 333));
		Font.draw("Kill the air wizard", screen, 	marginX +  0 * 8 + 4, 12 * 8, Color.get(0, 333, 333, 333));
		Font.draw("and steal his beer!", screen, 	marginX +  0 * 8 + 4, 13 * 8, Color.get(0, 333, 333, 333));
	}
}
