/**
 * 
 */
package de.findus.cydonia.server;

import java.util.Collection;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class WorldStateUpdate extends AbstractMessage {

	public static WorldStateUpdate getUpdateForPlayers(Collection<Player> players) {
		PlayerPhysic[] array = new PlayerPhysic[players.size()];
		int i=0;
		for (Player p : players) {
			PlayerPhysic physic = new PlayerPhysic();
			physic.setId(p.getId());
			physic.setTranslation(p.getControl().getPhysicsLocation());
			physic.setOrientation(p.getControl().getViewDirection());
//			System.out.println(p.getControl().getPhysicsLocation());
			
			array[i] = physic;
			i++;
		}
		WorldStateUpdate upd = new WorldStateUpdate();
		upd.setPlayerPhysics(array);
		return upd;
	}
	
	private PlayerPhysic[] array;
	
	public WorldStateUpdate() {
		
	}
	
	public void setPlayerPhysics(PlayerPhysic[] p) {
		this.array = p;
	}
	
	public PlayerPhysic[] getPlayerPhysics() {
		return this.array;
	}
}
