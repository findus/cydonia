/**
 * 
 */
package de.findus.cydonia.messages;

import java.util.Collection;
import java.util.LinkedList;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
@Serializable
public class WorldStateUpdatedMessage extends AbstractMessage {

	public static WorldStateUpdatedMessage getUpdate(Collection<Player> playerlist) {
		WorldStateUpdatedMessage upd = new WorldStateUpdatedMessage();
		
		LinkedList<PlayerPhysic> plist = new LinkedList<PlayerPhysic>();
		for (Player p : playerlist) {
			PlayerPhysic physic = new PlayerPhysic();
			physic.setId(p.getId());
			physic.setTranslation(p.getControl().getPhysicsLocation());
			physic.setOrientation(p.getViewDir());
			
			plist.add(physic);
		}
		PlayerPhysic[] playerphys = plist.toArray(new PlayerPhysic[0]);
		upd.setPlayerPhysics(playerphys);
		
		return upd;
	}
	
	private PlayerPhysic[] players;
	
	public WorldStateUpdatedMessage() {
		setReliable(false);
	}
	
	public void setPlayerPhysics(PlayerPhysic[] p) {
		this.players = p;
	}
	
	public PlayerPhysic[] getPlayerPhysics() {
		return this.players;
	}
}
