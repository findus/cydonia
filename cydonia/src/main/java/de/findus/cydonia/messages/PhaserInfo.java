/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Phaser;

/**
 * @author Findus
 *
 */
@Serializable
public class PhaserInfo implements EquipmentInfo {

	private String name;
	private float damage;
	private long interval;
	private int playerid;
	private String typeName;
	
	public PhaserInfo() {
		
	}
	
	public PhaserInfo(Phaser phaser) {
		this.name = phaser.getName();
		this.damage = phaser.getDamage();
		this.interval = phaser.getInterval();
		this.playerid = phaser.getPlayer().getId();
		this.typeName = phaser.getTypeName();
	}
	
	public String getName() {
		return name;
	}

	public float getDamage() {
		return damage;
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
