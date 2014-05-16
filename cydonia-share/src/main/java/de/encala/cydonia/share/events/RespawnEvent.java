/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
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
