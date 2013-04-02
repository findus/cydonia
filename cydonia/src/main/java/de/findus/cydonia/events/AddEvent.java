/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class AddEvent extends AbstractEvent {

	private int playerid;

	private long moveableid;
	
	private int objectType;
	
	private Vector3f location;

	/**
	 * 
	 */
	public AddEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param playerid
	 * @param moveableid
	 * @param forward
	 */
	public AddEvent(int playerid, long moveableid, int type, Vector3f loc, boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
		this.setMoveableid(moveableid);
		this.setObjectType(type);
		this.setLocation(loc);
		this.network = forward;
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
	 * @return the moveableid
	 */
	public long getMoveableid() {
		return moveableid;
	}

	/**
	 * @param moveableid the moveableid to set
	 */
	public void setMoveableid(long moveableid) {
		this.moveableid = moveableid;
	}

	/**
	 * @return the location
	 */
	public Vector3f getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}

}
