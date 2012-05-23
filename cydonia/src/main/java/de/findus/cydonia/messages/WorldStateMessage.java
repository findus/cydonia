/**
 * 
 */
package de.findus.cydonia.messages;

import java.util.Collection;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
@Serializable
public class WorldStateMessage extends AbstractMessage {

	public static WorldStateMessage getUpdate(Collection<Player> playerlist, Collection<Bullet> bulletlist) {
		WorldStateMessage upd = new WorldStateMessage();
		
		PlayerPhysic[] playerphys = new PlayerPhysic[playerlist.size()];
		int i=0;
		for (Player p : playerlist) {
			PlayerPhysic physic = new PlayerPhysic();
			physic.setId(p.getId());
			physic.setTranslation(p.getControl().getPhysicsLocation());
			physic.setOrientation(p.getControl().getViewDirection());
			
			playerphys[i] = physic;
			i++;
		}
		upd.setPlayerPhysics(playerphys);
		
		BulletPhysic[] bulletphys = new BulletPhysic[bulletlist.size()];
		i=0;
		for (Bullet b : bulletlist) {
			BulletPhysic physic = new BulletPhysic();
			physic.setId(b.getId());
			physic.setSourceid(b.getPlayerid());
			physic.setTranslation(b.getControl().getPhysicsLocation());
			physic.setVelocity(b.getControl().getLinearVelocity());
			
			bulletphys[i] = physic;
			i++;
		}
		upd.setBulletPhysics(bulletphys);
		
		return upd;
	}
	
	private PlayerPhysic[] players;
	private BulletPhysic[] bullets;
	
	public WorldStateMessage() {
		
	}
	
	public void setPlayerPhysics(PlayerPhysic[] p) {
		this.players = p;
	}
	
	public PlayerPhysic[] getPlayerPhysics() {
		return this.players;
	}
	
	public void setBulletPhysics(BulletPhysic[] b) {
		this.bullets = b;
	}
	
	public BulletPhysic[] getBulletPhysics() {
		return this.bullets;
	}
}
