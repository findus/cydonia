/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class RemoveEvent extends AbstractEvent {

	private int playerid;

	private long objectid;

	private String objectType;

	/**
	 * 
	 */
	public RemoveEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param playerid
	 * @param moveableid
	 * @param forward
	 */
	public RemoveEvent(int playerid, long objectid, String type, boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
		this.setObjectid(objectid);
		this.setObjectType(type);
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

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

}
