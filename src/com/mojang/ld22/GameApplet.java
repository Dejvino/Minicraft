package com.mojang.ld22;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;

import com.mojang.ld22.screen.TitleMenu;

public class GameApplet extends Applet {
	private static final long serialVersionUID = 1L;

	public void init() {
		// create a new game
		Game game = new Game();
		game.initGraphics();
		game.setMenu(new TitleMenu());
		GameContainer.getInstance().setGame(game);
		
		// init design
		setLayout(new BorderLayout());
		add(game, BorderLayout.CENTER);
		setSize(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
	}

	public void start() {
		GameContainer.getInstance().getGame().start();
	}

	public void stop() {
		GameContainer.getInstance().getGame().stop();
	}
	
	
}