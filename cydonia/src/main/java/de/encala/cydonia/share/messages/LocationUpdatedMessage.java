/**
 * 
 */
package de.encala.cydonia.share.messages;

import java.util.Collection;
import java.util.LinkedList;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.server.player.ServerPlayer;

/**
 * @author encala
 * 
 */
@Serializable
public class LocationUpdatedMessage extends AbstractMessage {

	public static LocationUpdatedMessage getUpdate(Collection<ServerPlayer> playerlist) {
		LocationUpdatedMessage upd = new LocationUpdatedMessage();

		LinkedList<PlayerPhysic> plist = new LinkedList<PlayerPhysic>();
		for (ServerPlayer p : playerlist) {
			PlayerPhysic physic = new PlayerPhysic();
			physic.setId(p.getId());
			physic.setTranslation(p.getControl().getPhysicsLocation());
			physic.setOrientation(p.getViewDir());
			physic.setHealthpoints(p.getHealthpoints());

			plist.add(physic);
		}
		PlayerPhysic[] playerphys = plist.toArray(new PlayerPhysic[0]);
		upd.setPlayerPhysics(playerphys);

		return upd;
	}

	private PlayerPhysic[] players;

	public LocationUpdatedMessage() {
		setReliable(false);
	}

	public void setPlayerPhysics(PlayerPhysic[] p) {
		this.players = p;
	}

	public PlayerPhysic[] getPlayerPhysics() {
		return this.players;
	}
}
