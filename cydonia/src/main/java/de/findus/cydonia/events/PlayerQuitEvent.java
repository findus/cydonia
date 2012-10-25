/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.network.serializing.Serializable;


/**
 * @author Findus
 *
 */
@Serializable
public class PlayerQuitEvent extends AbstractEvent {

	private int playerid;
	
	public PlayerQuitEvent() {
		super();
	}
	
	public PlayerQuitEvent(int playerid, boolean forward) {
		this.playerid = playerid;
		this.network = forward;
	}

	/**
	 * @return the id
	 */
	public int getPlayerId() {
		return playerid;
	}
}
