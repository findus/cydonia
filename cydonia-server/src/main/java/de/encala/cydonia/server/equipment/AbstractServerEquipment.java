/**
 * 
 */
package de.encala.cydonia.server.equipment;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.player.ServerPlayer;

/**
 * @author encala
 * 
 */
public abstract class AbstractServerEquipment implements ServerEquipment {

	protected ServerPlayer player;

	private GameServer gameServer;

	public AbstractServerEquipment() {

	}

	public AbstractServerEquipment(ServerPlayer player, GameServer gameServer) {
		this.player = player;
		this.gameServer = gameServer;
	}

	/**
	 * @return the player
	 */
	public ServerPlayer getServerPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setServerPlayer(ServerPlayer player) {
		this.player = player;
	}

	/**
	 * @return the gameServer
	 */
	public GameServer getGameServer() {
		return gameServer;
	}

	/**
	 * @param gameServer
	 *            the gameServer to set
	 */
	public void setGameServer(GameServer gameServer) {
		this.gameServer = gameServer;
	}

}
