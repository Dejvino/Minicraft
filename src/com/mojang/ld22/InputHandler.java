package com.mojang.ld22;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class InputHandler implements KeyListener {
	public class Key {
		public int presses, absorbs;
		public boolean down, clicked;

		public Key() {
			keys.add(this);
		}

		public void toggle(boolean pressed) {
			if (pressed != down) {
				down = pressed;
			}
			if (pressed) {
				presses++;
			}
		}

		public void tick() {
			if (absorbs < presses) {
				absorbs++;
				clicked = true;
			} else {
				clicked = false;
			}
		}
	}

	public List<Key> keys = new ArrayList<Key>();

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key attack = new Key();
	public Key menu = new Key();
	public Key save = new Key();
	public Key load = new Key();

	public void releaseAll() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).down = false;
		}
	}

	public void tick() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).tick();
		}
	}

	public InputHandler(Game game) {
		game.addKeyListener(this);
	}

	public void keyPressed(KeyEvent ke) {
		toggleKE(ke, true);
	}

	public void keyReleased(KeyEvent ke) {
		toggleKE(ke, false);
	}

	private void toggleKE(KeyEvent ke, boolean pressed) {
		int keyCode = ke.getKeyCode();
		if (keyCode == KeyEvent.VK_NUMPAD8) up.toggle(pressed);
		if (keyCode == KeyEvent.VK_NUMPAD2) down.toggle(pressed);
		if (keyCode == KeyEvent.VK_NUMPAD4) left.toggle(pressed);
		if (keyCode == KeyEvent.VK_NUMPAD6) right.toggle(pressed);
		if (keyCode == KeyEvent.VK_W) up.toggle(pressed);
		if (keyCode == KeyEvent.VK_S) down.toggle(pressed);
		if (keyCode == KeyEvent.VK_A) left.toggle(pressed);
		if (keyCode == KeyEvent.VK_D) right.toggle(pressed);
		if (keyCode == KeyEvent.VK_UP) up.toggle(pressed);
		if (keyCode == KeyEvent.VK_DOWN) down.toggle(pressed);
		if (keyCode == KeyEvent.VK_LEFT) left.toggle(pressed);
		if (keyCode == KeyEvent.VK_RIGHT) right.toggle(pressed);

		if (keyCode == KeyEvent.VK_TAB) menu.toggle(pressed);
		//if (keyCode == KeyEvent.VK_ALT) menu.toggle(pressed); // THIS KEY SUCKS!
		if (keyCode == KeyEvent.VK_ALT_GRAPH) menu.toggle(pressed);
		if (keyCode == KeyEvent.VK_SPACE) attack.toggle(pressed);
		if (keyCode == KeyEvent.VK_CONTROL) attack.toggle(pressed);
		if (keyCode == KeyEvent.VK_NUMPAD0) attack.toggle(pressed);
		if (keyCode == KeyEvent.VK_INSERT) attack.toggle(pressed);
		if (keyCode == KeyEvent.VK_ENTER) menu.toggle(pressed);

		if (keyCode == KeyEvent.VK_X) menu.toggle(pressed);
		if (keyCode == KeyEvent.VK_C) attack.toggle(pressed);
		
		if (keyCode == KeyEvent.VK_F5) save.toggle(pressed);
		if (keyCode == KeyEvent.VK_F9) load.toggle(pressed);
	}
	
	public void toggle(int keyCode, boolean pressed) {
		if (keyCode == Keyboard.KEY_NUMPAD8) up.toggle(pressed);
		if (keyCode == Keyboard.KEY_NUMPAD2) down.toggle(pressed);
		if (keyCode == Keyboard.KEY_NUMPAD4) left.toggle(pressed);
		if (keyCode == Keyboard.KEY_NUMPAD6) right.toggle(pressed);
		if (keyCode == Keyboard.KEY_W) up.toggle(pressed);
		if (keyCode == Keyboard.KEY_S) down.toggle(pressed);
		if (keyCode == Keyboard.KEY_A) left.toggle(pressed);
		if (keyCode == Keyboard.KEY_D) right.toggle(pressed);
		if (keyCode == Keyboard.KEY_UP) up.toggle(pressed);
		if (keyCode == Keyboard.KEY_DOWN) down.toggle(pressed);
		if (keyCode == Keyboard.KEY_LEFT) left.toggle(pressed);
		if (keyCode == Keyboard.KEY_RIGHT) right.toggle(pressed);

		if (keyCode == Keyboard.KEY_TAB) menu.toggle(pressed);
		//if (keyCode == Keyboard.KEY_ALT) menu.toggle(pressed); // THIS KEY SUCKS!
		if (keyCode == Keyboard.KEY_SPACE) attack.toggle(pressed);
		if (keyCode == Keyboard.KEY_LCONTROL) attack.toggle(pressed);
		if (keyCode == Keyboard.KEY_RCONTROL) attack.toggle(pressed);
		if (keyCode == Keyboard.KEY_NUMPAD0) attack.toggle(pressed);
		if (keyCode == Keyboard.KEY_INSERT) attack.toggle(pressed);
		if (keyCode == Keyboard.KEY_RETURN) menu.toggle(pressed);

		if (keyCode == Keyboard.KEY_X) menu.toggle(pressed);
		if (keyCode == Keyboard.KEY_C) attack.toggle(pressed);
		
		if (keyCode == Keyboard.KEY_F5) save.toggle(pressed);
		if (keyCode == Keyboard.KEY_F9) load.toggle(pressed);
	}

	public void keyTyped(KeyEvent ke) {
	}
}
