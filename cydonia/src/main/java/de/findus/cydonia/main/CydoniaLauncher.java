package de.findus.cydonia.main;

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
        GameController controller = new GameController();
        controller.start();
    }
}
