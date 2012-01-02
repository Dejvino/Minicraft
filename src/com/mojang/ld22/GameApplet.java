package com.mojang.ld22;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class GameApplet extends Applet {
	private static final long serialVersionUID = 1L;

	Canvas display_parent;

	public void startGame() {
		try {
			Display.setParent(display_parent);
			GameContainer.getInstance().getGame().start();
		} catch (LWJGLException e) {
				e.printStackTrace();
		}
	}
	
	private void stopGame() {
		GameContainer.getInstance().getGame().stop();
	}

	public void init() {
		GameContainer.getInstance().setSaveLoadEnabled(false);
		setLayout(new BorderLayout());
		try {
			display_parent = new Canvas() {
				public final void addNotify() {
					super.addNotify();
					startGame();
				}
				public final void removeNotify() {
					stopGame();
					super.removeNotify();
				}
			};
			// we could do this, but we use hardcoded values
			//display_parent.setSize(getWidth(),getHeight());
			display_parent.setSize(Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
			add(display_parent);
			display_parent.setFocusable(true);
			display_parent.requestFocus();
			display_parent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display");
		}
	}

	public void start() {
	}

	public void stop() {
	}
	
	public void destroy() {
		remove(display_parent);
		super.destroy();
	}
}