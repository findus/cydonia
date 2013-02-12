/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Beamer;

/**
 * @author Findus
 *
 */
@Serializable
public class BeamerInfo implements EquipmentInfo {

	private String name;
	private float range;
	private int playerid;
	
	public BeamerInfo() {
		
	}
	
	public BeamerInfo(Beamer beamer) {
		this.name = beamer.getName();
		this.range = beamer.getRange();
		this.playerid = beamer.getPlayer().getId();
	}
	
	@Override
	public String getClassName() {
		return Beamer.class.getName();
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
