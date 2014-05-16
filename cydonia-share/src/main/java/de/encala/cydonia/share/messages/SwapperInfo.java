/**
 * 
 */
package de.encala.cydonia.share.messages;

import com.jme3.network.serializing.Serializable;

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

	public SwapperInfo(String name, float range, int playerid, String typeName) {
		this.name = name;
		this.range = range;
		this.playerid = playerid;
		this.typeName = typeName;
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
