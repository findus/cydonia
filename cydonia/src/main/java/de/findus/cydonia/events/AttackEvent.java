/**
 * 
 */
package de.findus.cydonia.events;


/**
 * @author Findus
 *
 */
public class AttackEvent extends AbstractEvent {
	
	private int playerid;
	
	private int bulletid;
	
	public AttackEvent() {
		setNetworkEvent(false);
	}

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	/**
	 * @return the bulletid
	 */
	public int getBulletid() {
		return bulletid;
	}

	/**
	 * @param bulletid the bulletid to set
	 */
	public void setBulletid(int bulletid) {
		this.bulletid = bulletid;
	}
}
