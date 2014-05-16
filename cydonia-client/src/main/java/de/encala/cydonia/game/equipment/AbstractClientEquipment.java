/**
 * 
 */
package de.encala.cydonia.game.equipment;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.player.Player;

/**
 * @author encala
 * 
 */
public abstract class AbstractClientEquipment implements ClientEquipment {

	protected Player player;

	private GameController gameController;

	public AbstractClientEquipment() {

	}

	public AbstractClientEquipment(GameController gameController) {
		this.gameController = gameController;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the gameController
	 */
	public GameController getGameController() {
		return gameController;
	}

	/**
	 * @param gameController
	 *            the gameController to set
	 */
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

}
