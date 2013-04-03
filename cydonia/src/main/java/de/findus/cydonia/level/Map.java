/**
 * 
 */
package de.findus.cydonia.level;

import java.util.List;

import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class Map {

	private String name;
	
	private List<Flube> flubes;
	
	private List<SpawnPoint> spawnPoints;
	
	private List<Flag> flags;
	
	private float bottomHeight;
	
	public Map() {
		
	}

	public Map(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the flubes
	 */
	public List<Flube> getFlubes() {
		return flubes;
	}

	/**
	 * @param flubes the flubes to set
	 */
	public void setFlubes(List<Flube> flubes) {
		this.flubes = flubes;
	}

	/**
	 * @return the spawnPoints
	 */
	public List<SpawnPoint> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * @param spawnPoints the spawnPoints to set
	 */
	public void setSpawnPoints(List<SpawnPoint> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	/**
	 * @return the targetAreas
	 */
	public List<Flag> getFlags() {
		return flags;
	}

	/**
	 * @param targetAreas the targetAreas to set
	 */
	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	/**
	 * @return the bottomHeight
	 */
	public float getBottomHeight() {
		return bottomHeight;
	}

	/**
	 * @param bottomHeight the bottomHeight to set
	 */
	public void setBottomHeight(float bottomHeight) {
		this.bottomHeight = bottomHeight;
	}
	
}
