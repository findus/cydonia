/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class PickupEvent extends AbstractEvent {

	private int playerid;

	private long moveableid;

	/**
	 * 
	 */
	public PickupEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param playerid
	 * @param moveableid
	 * @param forward
	 */
	public PickupEvent(int playerid, long moveableid, boolean forward) {
		super(forward);
		this.setPlayerid(playerid);
		this.setMoveableid(moveableid);
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

}
