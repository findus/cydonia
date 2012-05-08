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

	private int victimPlayerid;
	
	private int sourcePlayerid;
	
	private double hitpoints;
	
	public HitMessage() {
		this.setReliable(true);
	}

	public int getVictimPlayerid() {
		return victimPlayerid;
	}

	public void setVictimPlayerid(int playerid) {
		this.victimPlayerid = playerid;
	}

	public double getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(double hitpoints) {
		this.hitpoints = hitpoints;
	}

	public int getSourcePlayerid() {
		return sourcePlayerid;
	}

	public void setSourcePlayerid(int sourcePlayerid) {
		this.sourcePlayerid = sourcePlayerid;
	}
	
	
}
