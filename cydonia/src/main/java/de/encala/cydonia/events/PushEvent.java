/**
 * 
 */
package de.encala.cydonia.events;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class PushEvent extends AbstractEvent {

	private int attackerId;

	private int victimId;

	private Vector3f force;

	/**
	 * 
	 */
	public PushEvent() {

	}

	/**
	 * @param net
	 */
	public PushEvent(int attackerId, int victimId, Vector3f force, boolean net) {
		super(net);
		this.attackerId = attackerId;
		this.victimId = victimId;
		this.force = force;
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

	public Vector3f getForce() {
		return force;
	}

	public void setForce(Vector3f force) {
		this.force = force;
	}

}
