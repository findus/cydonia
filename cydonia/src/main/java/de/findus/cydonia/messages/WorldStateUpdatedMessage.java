/**
 * 
 */
package de.findus.cydonia.messages;

import java.util.Collection;
import java.util.LinkedList;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
@Serializable
public class WorldStateUpdatedMessage extends AbstractMessage {

	public static WorldStateUpdatedMessage getUpdate(Collection<Player> playerlist, Collection<Bullet> bulletlist) {
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
		
		LinkedList<BulletPhysic> blist = new LinkedList<BulletPhysic>();
		for (Bullet b : bulletlist) {
			BulletPhysic physic = new BulletPhysic();
			physic.setId(b.getId());
			physic.setSourceid(b.getPlayerid());
			physic.setTranslation(b.getControl().getPhysicsLocation());
			physic.setVelocity(b.getControl().getLinearVelocity());
			
			blist.add(physic);
		}
		BulletPhysic[] bulletphys = blist.toArray(new BulletPhysic[0]);
		upd.setBulletPhysics(bulletphys);
		
		return upd;
	}
	
	private PlayerPhysic[] players;
	private BulletPhysic[] bullets;
	
	public WorldStateUpdatedMessage() {
		setReliable(false);
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
