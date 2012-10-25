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
public class HitEvent extends AbstractEvent {
	
	private int victimPlayerid;
	
	private int attackerPlayerid;
	
	private double hitpoints;

	public HitEvent() {
		super();
	}
	
	public HitEvent(int victimid, int attackerid, double hitpoints, boolean forward) {
		this.victimPlayerid = victimid;
		this.attackerPlayerid = attackerid;
		this.hitpoints = hitpoints;
		this.network = forward;
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

	public int getAttackerPlayerid() {
		return attackerPlayerid;
	}

	public void setAttackerPlayerid(int attackerPlayerid) {
		this.attackerPlayerid = attackerPlayerid;
	}
}
