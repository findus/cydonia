/**
 * 
 */
package de.encala.cydonia.game.level;

import java.util.HashMap;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class Map {

	private String name;

	private java.util.Map<Long, Flube> flubes;

	private java.util.Map<Integer, SpawnPoint> spawnPoints;

	private java.util.Map<Integer, Flag> flags;

	private float bottomHeight;

	public Map() {
		this("");
	}

	public Map(String name) {
		this.name = name;

		this.flubes = new HashMap<Long, Flube>();
		this.flags = new HashMap<Integer, Flag>();
		this.spawnPoints = new HashMap<Integer, SpawnPoint>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the flubes
	 */
	public java.util.Map<Long, Flube> getFlubes() {
		return flubes;
	}

	/**
	 * @param flubes
	 *            the flubes to set
	 */
	public void setFlubes(java.util.Map<Long, Flube> flubes) {
		this.flubes = flubes;
	}

	/**
	 * @return the spawnPoints
	 */
	public java.util.Map<Integer, SpawnPoint> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * @param spawnPoints
	 *            the spawnPoints to set
	 */
	public void setSpawnPoints(java.util.Map<Integer, SpawnPoint> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	/**
	 * @return the targetAreas
	 */
	public java.util.Map<Integer, Flag> getFlags() {
		return flags;
	}

	/**
	 * @param targetAreas
	 *            the targetAreas to set
	 */
	public void setFlags(java.util.Map<Integer, Flag> flags) {
		this.flags = flags;
	}

	/**
	 * @return the bottomHeight
	 */
	public float getBottomHeight() {
		return bottomHeight;
	}

	/**
	 * @param bottomHeight
	 *            the bottomHeight to set
	 */
	public void setBottomHeight(float bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

}
