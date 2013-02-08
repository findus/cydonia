/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public abstract class AbstractEquipment implements Equipment {
	
	protected Player player;
	
	private MainController mainController;

	public AbstractEquipment() {
		
	}
	
	public AbstractEquipment(MainController mainController) {
		this.mainController = mainController;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}

	/**
	 * @param mainController the mainController to set
	 */
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
