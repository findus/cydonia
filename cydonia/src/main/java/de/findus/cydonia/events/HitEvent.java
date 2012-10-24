/**
 * 
 */
package de.findus.cydonia.events;

/**
 * @author Findus
 *
 */
public class HitEvent extends AbstractEvent {
	
	private int victimPlayerid;
	
	private int attackerPlayerid;
	
	private double hitpoints;

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
