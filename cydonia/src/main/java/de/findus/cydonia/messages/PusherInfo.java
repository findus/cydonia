/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Pusher;

/**
 * @author Findus
 *
 */
@Serializable
public class PusherInfo implements EquipmentInfo {

	private String name;
	private float force;
	private long interval;
	private int playerid;
	private String typeName;
	
	public PusherInfo() {
		
	}
	
	public PusherInfo(Pusher pusher) {
		this.name = pusher.getName();
		this.force = pusher.getForce();
		this.interval = pusher.getInterval();
		this.playerid = pusher.getPlayer().getId();
		this.typeName = pusher.getTypeName();
	}
	
	public String getName() {
		return name;
	}

	public float getForce() {
		return force;
	}

	public long getInterval() {
		return interval;
	}

	public int getPlayerid() {
		return playerid;
	}

	/* (non-Javadoc)
	 * @see de.findus.cydonia.messages.EquipmentInfo#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return typeName;
	}

}
