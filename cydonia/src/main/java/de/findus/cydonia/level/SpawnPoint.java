/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.math.Vector3f;

/**
 * @author Findus
 *
 */
public class SpawnPoint {

	private Vector3f position;
	
	private int team;

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

}
