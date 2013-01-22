/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.level.WorldController;

/**
 * @author Findus
 *
 */
public abstract class AbstractEquipment implements Equipment {
	
	protected Player player;
	
	protected WorldController worldController;
	
	protected EventMachine eventMachine;
	

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
	
	@Override
	public void setWorldController(WorldController wc) {
		this.worldController = wc;
	}

	@Override
	public void setEventMachine(EventMachine em) {
		this.eventMachine = em;
	}

}
