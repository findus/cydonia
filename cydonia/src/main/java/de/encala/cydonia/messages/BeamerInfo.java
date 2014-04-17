/**
 * 
 */
package de.encala.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.player.Beamer;

/**
 * @author encala
 * 
 */
@Serializable
public class BeamerInfo implements EquipmentInfo {

	private String name;
	private float range;
	private int playerid;
	private String typeName;

	public BeamerInfo() {

	}

	public BeamerInfo(Beamer beamer) {
		this.name = beamer.getName();
		this.range = beamer.getRange();
		this.playerid = beamer.getPlayer().getId();
		this.typeName = beamer.getTypeName();
	}

	@Override
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the range
	 */
	public float getRange() {
		return range;
	}

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

}
