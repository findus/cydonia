package de.findus.cydonia.messages;

import java.util.LinkedList;
import java.util.List;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.level.Flube;
import de.findus.cydonia.player.Picker;

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
	
	public PickerInfo(Picker picker) {
		this.name = picker.getName();
		this.range = picker.getRange();
		this.capacity = picker.getCapacity();
		this.repository = new LinkedList<Long>();
		for (Flube f : picker.getRepository()) {
			this.repository.add(f.getId());
		}
		this.playerid = picker.getPlayer().getId();
		this.typeName = picker.getTypeName();
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