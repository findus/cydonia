/**
 * 
 */
package de.encala.cydonia.events;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class PlaceEvent extends AbstractEvent {

	private int playerid;

	private long moveableid;

	private Vector3f location;

	/**
	 * 
	 */
	public PlaceEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param playerid
	 * @param moveableid
	 * @param forward
	 */
	public PlaceEvent(int playerid, long moveableid, Vector3f loc,
			boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
		this.setMoveableid(moveableid);
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
	 * @param playerid
	 *            the playerid to set
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
	 * @param moveableid
	 *            the moveableid to set
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
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
	}

}
