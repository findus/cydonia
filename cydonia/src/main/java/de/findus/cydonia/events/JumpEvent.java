/**
 * 
 */
package de.findus.cydonia.events;

/**
 * @author Findus
 *
 */
public class JumpEvent extends AbstractEvent {
	
	private int playerid;

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

}
