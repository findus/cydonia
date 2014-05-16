/**
 * 
 */
package de.encala.cydonia.share.events;

/**
 * @author encala
 * 
 */
public class TargetReachedEvent extends AbstractEvent {

	private int playerid;

	public TargetReachedEvent() {
		super();
	}

	public TargetReachedEvent(int playerid, boolean forward) {
		this.setPlayerid(playerid);
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
}
