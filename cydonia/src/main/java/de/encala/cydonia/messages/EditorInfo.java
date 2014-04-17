package de.encala.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.player.Editor;

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

	public EditorInfo(Editor editor) {
		this.name = editor.getName();
		this.range = editor.getRange();
		this.objectType = editor.getObjectType();
		this.objectSpec = editor.getObjectSpec();
		this.playerid = editor.getPlayer().getId();
		this.typeName = editor.getTypeName();
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

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	public int getObjectSpec() {
		return objectSpec;
	}

}