package de.encala.cydonia;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.ServerBrowser;
import de.encala.cydonia.server.GameServer;

/**
 * Game Launcher of Cydonia.
 * 
 */
public class CydoniaLauncher {

	/**
	 * Starts the game.
	 * 
	 * @param args
	 *            commands (unused)
	 */
	public static void main(String[] args) {
		boolean server = false;
		boolean window = false;
		boolean instantConnect = false;
		String ip = null;

		for (int i = 0; i < args.length; i++) {
			if ("--server".equalsIgnoreCase(args[i])) {
				server = true;
			} else if ("--window".equalsIgnoreCase(args[i])) {
				window = true;
			} else if (("--ic".equalsIgnoreCase(args[i]) || "--instantconnect"
					.equalsIgnoreCase(args[i])) && args.length > i) {
				instantConnect = true;
				ip = args[++i];
			}
		}

		if (server) {
			startServer(window);
		} else if (instantConnect) {
			startClient(ip);
		} else {
			startServerBrowser();
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

	private static void startServerBrowser() {
		ServerBrowser sb = new ServerBrowser();
		sb.show();
	}

	private static void startClient(String ip) {
		GameController game = new GameController();
		game.start(ip);
	}
}
