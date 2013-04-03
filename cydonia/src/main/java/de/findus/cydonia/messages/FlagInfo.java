/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.level.Flag;

/**
 * @author Findus
 *
 */
@Serializable
public class FlagInfo {

	private int id;
	
	private int playerid;
	
	private boolean inBase;
	
	private Vector3f origin;
	
	private int team;
	
	/**
	 * 
	 */
	public FlagInfo() {

	}

	public FlagInfo(Flag f) {
		this.id = f.getId();
		this.inBase = f.isInBase();
		if(f.getPlayer() != null) {
			this.playerid = f.getPlayer().getId();
		}else {
			this.playerid = -1;
		}
		this.origin = f.getOrigin();
		this.team = f.getTeam();
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

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	/**
	 * @param playerid the playerid to set
	 */
	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	/**
	 * @return the inBase
	 */
	public boolean isInBase() {
		return inBase;
	}

	/**
	 * @param inBase the inBase to set
	 */
	public void setInBase(boolean inBase) {
		this.inBase = inBase;
	}

	public Vector3f getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3f origin) {
		this.origin = origin;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
}
