package de.encala.cydonia.share.messages;

import java.util.List;

import com.jme3.network.serializing.Serializable;

@Serializable
public class PickerInfo implements EquipmentInfo {

	private String name;
	private float range;
	private int capacity;
	private List<Long> repository;
	private int playerid;
	private String typeName;

	public PickerInfo() {

	}

	public PickerInfo(String name, float range, int capacity, List<Long> repository, int playerid, String typeName) {
		this.name = name;
		this.range = range;
		this.capacity = capacity;
		this.repository = repository;
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

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @return the repository
	 */
	public List<Long> getRepository() {
		return repository;
	}

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

}