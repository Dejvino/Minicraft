package com.mojang.ld22;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessControlException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.mojang.ld22.screen.TitleMenu;

/**
 * 		Game Container
 * 
 * Main class used as a manager of a currently running game.
 * 
 * Purpose, responsibilities, functions:
 *  - Entry-point for starting the game.
 *  - Creator and owner of the UI (JFrame).
 *  - Singleton interface, handles one "main" game (though the number of games
 *  	running at the same time is theoretically not limited by this).
 *  - Save and load function (+ GUI), swaps loaded game with the active one. 
 * 
 * @author Dejvino
 */
public class GameContainer
{
	private static GameContainer singleton;
	
	private JFrame jFrame;
	
	private Game game;
	private GameSetup setup;
	
	private GameContainer()
	{
		this.setup = new GameSetup();
		// TODO: load setup from file
		
		this.game = new Game();
		this.game.initGraphics();
		this.game.setMenu(new TitleMenu());
	}
	
	/**
	 * Returns a singleton instance of this class.
	 * 
	 * @return singleton
	 */
	public static GameContainer getInstance()
	{
		if (singleton == null) {
			singleton = new GameContainer();
		}
		return singleton;
	}
	
	/**
	 * Returns the currently active game.
	 * 
	 * @return game
	 */
	public Game getGame()
	{
		return this.game;
	}
	
	/**
	 * Changes the currently active game.
	 * 
	 * @param game
	 */
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	/**
	 * Returns the current game setup.
	 * 
	 * @return
	 */
	public GameSetup getSetup()
	{
		return this.setup;
	}
	
	/**
	 * Changes the game setup.
	 * 
	 * @param setup
	 */
	public void setSetup(GameSetup setup)
	{
		this.setup = setup;
	}
	
	/**
	 * Performs a one-time initialization of the environment.
	 * Namely it creates a UI window (JFrame) which is later used to display the game.
	 */
	public void init()
	{
		JFrame frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
		jFrame = frame;
	}
	
	/**
	 * The currently active game is attached to the UI window and then started.
	 */
	public void startGame()
	{
		game.setMinimumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		game.setMaximumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		game.setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		
		jFrame.add(game, BorderLayout.CENTER);
		jFrame.pack();
		
		this.game.start();
	}
	
	/**
	 * Stops the currently active game and removes it from the UI window.
	 */
	public void stopGame()
	{
		this.game.stop();
		this.jFrame.remove(game);
	}
	
	/**
	 * Displays a "Save as" dialog for the user to choose a savegame filename,
	 * then writes the active game to the file.
	 */
	public void saveGame()
	{
		try {
			// create a file chooser
			final JFileChooser fc = new JFileChooser();

			// choose file
			int returnVal = fc.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
	            System.out.println("Game saving canceled by user.\n");
	            return;
	        }
			
			// get file
			File file = fc.getSelectedFile();
	        System.out.println("Saving: " + file.getName() + ".\n");
	        
	        // save game to file
			try {
	            FileOutputStream fileOut = new FileOutputStream(file);
	            ObjectOutputStream out = new ObjectOutputStream(fileOut);
	
	            out.writeObject(this.game);
	            
	            out.close();
	            fileOut.close();
	        } catch(FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} catch (AccessControlException e) {
			// no saving for you!
			e.printStackTrace();
		}
	}
	
	/**
	 * Displays an "Open file" dialog for the user to choose a savegame file,
	 * then deserializes the saved game and swaps it with the current one.
	 * The loaded game is then started.
	 */
	public void loadGame()
	{
		try {
			// create a file chooser
			final JFileChooser fc = new JFileChooser();

			// choose file
			int returnVal = fc.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
	            System.out.println("Game loading canceled by user.\n");
	            return;
	        }
			
			// get file
			File file = fc.getSelectedFile();
	        System.out.println("Opening: " + file.getName() + ".\n");
	        
	        // load game from file
	        Game newGame = null;
			try {
	            FileInputStream fileIn = new FileInputStream(file);
	            ObjectInputStream in = new ObjectInputStream(fileIn);
	
	            newGame = (Game)in.readObject();
	            
	            in.close();
	            fileIn.close();
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	            return;
	        } catch(FileNotFoundException e) {
	            e.printStackTrace();
	            return;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return;
	        }
			
			// swap games
			this.stopGame();
			this.game = newGame;
			this.game.loadGame();
			this.startGame();
		} catch (AccessControlException e) {
			// no loading for you!
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		GameContainer cont = GameContainer.getInstance();
		cont.init();
		cont.startGame();
	}

}
