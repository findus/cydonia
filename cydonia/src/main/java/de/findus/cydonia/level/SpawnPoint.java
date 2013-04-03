/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.messages.SpawnPointInfo;

/**
 * @author Findus
 *
 */
@Serializable
public class SpawnPoint {

	private int id;
	
	private Vector3f position;
	
	private int team;

	public SpawnPoint() {
		
	}
	
	public SpawnPoint(int id, Vector3f position, int team) {
		this.id = id;
		this.position = position;
		this.team = team;
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public SpawnPointInfo getInfo() {
		return new SpawnPointInfo(this);
	}

}
