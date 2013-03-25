/**
 * 
 */
package de.findus.cydonia.messages;

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
}
