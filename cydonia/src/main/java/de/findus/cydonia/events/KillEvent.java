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
public class KillEvent extends AbstractEvent {

	private int playerid;
	
	/**
	 * 
	 */
	public KillEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param net
	 */
	public KillEvent(int playerid, boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
	}
	
	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	/**
	 * @param playerid the playerid to set
	 */
	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

}
