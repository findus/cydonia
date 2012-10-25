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
public class RespawnEvent extends AbstractEvent {

	private int playerid;
	
	public RespawnEvent() {
		super();
	}
	
	/**
	 * 
	 */
	public RespawnEvent(int playerid, boolean forward) {
		this.playerid = playerid;
		this.network = forward;
	}
	
	
	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}
}
