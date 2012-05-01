/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class RespawnMessage extends AbstractMessage {

	
	private int playerid;
	
	/**
	 * 
	 */
	public RespawnMessage() {
		this.setReliable(true);
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
