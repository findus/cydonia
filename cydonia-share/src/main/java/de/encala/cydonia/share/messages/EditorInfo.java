package de.encala.cydonia.share.messages;

import com.jme3.network.serializing.Serializable;

@Serializable
public class EditorInfo implements EquipmentInfo {

	private String name;
	private float range;
	private String objectType;
	private int objectSpec;
	private int playerid;
	private String typeName;

	public EditorInfo() {

	}

	public EditorInfo(String name, float range, String objectType, int objectSpec, int playerid, String typeName) {
		this.name = name;
		this.range = range;
		this.objectType = objectType;
		this.objectSpec = objectSpec;
		this.playerid = playerid;
		this.typeName = typeName;
	}

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

	public String getObjectType() {
		return objectType;
	}

	public int getObjectSpec() {
		return objectSpec;
	}

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

}