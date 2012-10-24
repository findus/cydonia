/**
 * 
 */
package de.findus.cydonia.main;

import de.findus.cydonia.events.AbstractEvent;

/**
 * @author Findus
 *
 */
public class PlayerQuitEvent extends AbstractEvent {

	private int playerid;
	
	public PlayerQuitEvent() {
		setNetworkEvent(true);
	}

	/**
	 * @return the id
	 */
	public int getPlayerId() {
		return playerid;
	}

	/**
	 * @param id the id to set
	 */
	public void setPlayerId(int id) {
		this.playerid = id;
	}
}
