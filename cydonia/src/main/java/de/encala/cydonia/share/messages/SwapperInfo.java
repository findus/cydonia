/**
 * 
 */
package de.encala.cydonia.share.messages;

import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.server.equipment.ServerSwapper;

/**
 * @author encala
 * 
 */
@Serializable
public class SwapperInfo implements EquipmentInfo {

	private String name;
	private float range;
	private int playerid;
	private String typeName;

	/**
	 * 
	 */
	public SwapperInfo() {

	}

	public SwapperInfo(ServerSwapper swapper) {
		this.name = swapper.getName();
		this.range = swapper.getRange();
		this.playerid = swapper.getServerPlayer().getId();
		this.typeName = swapper.getTypeName();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	public String getName() {
		return name;
	}

	public int getPlayerid() {
		return playerid;
	}

	public float getRange() {
		return range;
	}

}
