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
        boolean server = false;
        boolean window = false;
		
		for(String arg : args) {
        	if("--server".equalsIgnoreCase(arg)) {
        		server = true;
        	}else if("--window".equalsIgnoreCase(arg)) {
        		window = true;
        	}
        }
		
		if(server) {
			startServer(window);
		}else {
			startClient();
		}
    }
	
	private static void startServer(final boolean window) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				GameServer server = new GameServer(window);
				server.start();
			}
		}).start();
	}
	
	private static void startClient() {
		ServerBrowser sb = new ServerBrowser();
		sb.show();
	}
}
