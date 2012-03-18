package de.findus.cydonia.main;

import de.findus.cydonia.server.GameServer;

/**
 * Game Launcher of Cydonia.
 *
 */
public class CydoniaLauncher 
{
	/**
	 * Starts the game.
	 * @param args commands (unused)
	 */
	public static void main(String[] args) {
        for (String string : args) {
			if(string.equals("-server")) {
				GameServer.main(args);
			}
		}
		GameController controller = new GameController();
        controller.start();
    }
}
