/**
 * 
 */
package de.encala.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class PhaseEvent extends AbstractEvent {

	private int attackerId;

	private int victimId;

	private float damage;

	/**
	 * 
	 */
	public PhaseEvent() {

	}

	/**
	 * @param net
	 */
	public PhaseEvent(int attackerId, int victimId, float damage, boolean net) {
		super(net);
		this.attackerId = attackerId;
		this.victimId = victimId;
		this.damage = damage;
	}

	public int getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(int attackerId) {
		this.attackerId = attackerId;
	}

	public int getVictimId() {
		return victimId;
	}

	public void setVictimId(int victimId) {
		this.victimId = victimId;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

}
