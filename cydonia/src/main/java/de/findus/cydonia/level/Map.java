/**
 * 
 */
package de.findus.cydonia.level;

import java.util.List;

/**
 * @author Findus
 *
 */
public class Map {

	private String name;
	
	private List<Flube> flubes;
	
	private List<SpawnPoint> spawnPoints;
	
	private List<TargetArea> targetAreas;

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
	public List<TargetArea> getTargetAreas() {
		return targetAreas;
	}

	/**
	 * @param targetAreas the targetAreas to set
	 */
	public void setTargetAreas(List<TargetArea> targetAreas) {
		this.targetAreas = targetAreas;
	}
	
}
