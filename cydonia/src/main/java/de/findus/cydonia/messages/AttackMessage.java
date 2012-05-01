/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.server.BulletPhysic;

/**
 * @author Findus
 *
 */
@Serializable
public class AttackMessage extends AbstractMessage {

	private int playerid;
	private BulletPhysic physic;
	
	/**
	 * Sets reliable to true.
	 */
	public AttackMessage() {
		setReliable(true);
	}
	
	public AttackMessage(int playerid) {
		this.playerid = playerid;
	}

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	public BulletPhysic getPhysic() {
		return physic;
	}

	public void setPhysic(BulletPhysic physic) {
		this.physic = physic;
	}

}
