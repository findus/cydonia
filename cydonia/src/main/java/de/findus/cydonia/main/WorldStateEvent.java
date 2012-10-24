/**
 * 
 */
package de.findus.cydonia.main;

import java.util.Collection;

import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.events.AbstractEvent;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public class WorldStateEvent extends AbstractEvent {
	
	
	public static WorldStateEvent getUpdate(Collection<Player> playerlist, Collection<Bullet> bulletlist) {
		WorldStateEvent upd = new WorldStateEvent();
		
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
	
	public WorldStateEvent() {
		
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
