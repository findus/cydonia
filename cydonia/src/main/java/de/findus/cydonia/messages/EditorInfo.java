package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Editor;

@Serializable
public class EditorInfo implements EquipmentInfo {

	private String name;
	private float range;
	private int objectType;
	private int playerid;
	private String typeName;
	
	public EditorInfo() {

	}
	
	public EditorInfo(Editor editor) {
		this.name = editor.getName();
		this.range = editor.getRange();
		this.objectType = editor.getObjectType();
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

	public int getObjectType() {
		return objectType;
	}

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}
	
}