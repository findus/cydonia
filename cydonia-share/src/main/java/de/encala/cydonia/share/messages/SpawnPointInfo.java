/**
 * 
 */
package de.encala.cydonia.share.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class SpawnPointInfo {

	private int id;

	private Vector3f position;

	private int team;

	/**
	 * 
	 */
	public SpawnPointInfo() {

	}

	public SpawnPointInfo(int id, Vector3f position, int team) {
		this.id = id;
		this.position = position;
		this.team = team;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
}
