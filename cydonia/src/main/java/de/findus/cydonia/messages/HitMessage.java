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
public class HitMessage extends AbstractMessage {

	private int playerid;
	
	private double hitpoints;

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	public double getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(double hitpoints) {
		this.hitpoints = hitpoints;
	}
	
	
}
