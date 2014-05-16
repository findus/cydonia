/**
 * 
 */
package de.encala.cydonia.server.world;

import java.util.HashMap;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class ServerMap {

	private String name;

	private java.util.Map<Long, ServerFlube> flubes;

	private java.util.Map<Integer, ServerSpawnPoint> spawnPoints;

	private java.util.Map<Integer, ServerFlag> flags;

	private float bottomHeight;

	public ServerMap() {
		this("");
	}

	public ServerMap(String name) {
		this.name = name;

		this.flubes = new HashMap<Long, ServerFlube>();
		this.flags = new HashMap<Integer, ServerFlag>();
		this.spawnPoints = new HashMap<Integer, ServerSpawnPoint>();
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
	public java.util.Map<Long, ServerFlube> getFlubes() {
		return flubes;
	}

	/**
	 * @param flubes
	 *            the flubes to set
	 */
	public void setFlubes(java.util.Map<Long, ServerFlube> serverFlubes) {
		this.flubes = serverFlubes;
	}

	/**
	 * @return the spawnPoints
	 */
	public java.util.Map<Integer, ServerSpawnPoint> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * @param spawnPoints
	 *            the spawnPoints to set
	 */
	public void setSpawnPoints(java.util.Map<Integer, ServerSpawnPoint> serverSpawnPoints) {
		this.spawnPoints = serverSpawnPoints;
	}

	/**
	 * @return the targetAreas
	 */
	public java.util.Map<Integer, ServerFlag> getFlags() {
		return flags;
	}

	/**
	 * @param targetAreas
	 *            the targetAreas to set
	 */
	public void setFlags(java.util.Map<Integer, ServerFlag> serverFlags) {
		this.flags = serverFlags;
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
