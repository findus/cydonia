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
public class AddEvent extends AbstractEvent {

	private int playerid;

	private long objectid;

	private String objectType;

	private int objectSpec;

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
	public AddEvent(int playerid, long objectid, String type, int spec,
			Vector3f loc, boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
		this.setObjectid(objectid);
		this.setObjectType(type);
		this.setObjectSpec(spec);
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
	public long getObjectid() {
		return objectid;
	}

	/**
	 * @param moveableid
	 *            the moveableid to set
	 */
	public void setObjectid(long objectid) {
		this.objectid = objectid;
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

	public int getObjectSpec() {
		return objectSpec;
	}

	public void setObjectSpec(int objectSpec) {
		this.objectSpec = objectSpec;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

}
