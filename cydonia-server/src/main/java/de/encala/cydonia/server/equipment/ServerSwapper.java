/**
 * 
 */
package de.encala.cydonia.server.equipment;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Node;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.server.world.ServerFlube;
import de.encala.cydonia.server.world.ServerWorldObject;
import de.encala.cydonia.share.events.MarkEvent;
import de.encala.cydonia.share.events.SwapEvent;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.SwapperInfo;

/**
 * @author encala
 * 
 */
public class ServerSwapper extends AbstractServerEquipment {

	static final String TYPENAME = "Swapper";

	private String name;

	private float range;

	private Node geom = new Node("Swapper");
	
	ServerWorldObject[] markers;

	/**
	 * 
	 */
	public ServerSwapper() {
		super();
		markers = new ServerWorldObject[2];
	}

	/**
	 * @param gameServer
	 */
	public ServerSwapper(ServerPlayer player, GameServer gameServer) {
		super(player, gameServer);
		markers = new ServerWorldObject[2];
	}

	@Override
	public void usePrimary(boolean activate) {
		if (!activate)
			return;

		CollisionResults results = getGameServer().getWorldController()
				.pickRootList(
						this.player.getEyePosition().add(
								player.getViewDir().normalize().mult(0.3f)),
						this.player.getViewDir());
		for (CollisionResult result : results) {
			if (result != null && result.getDistance() <= this.getRange()) {
				if ((result.getGeometry().getParent() != null
						&& result.getGeometry().getParent().getName() != null && result
						.getGeometry().getParent().getName()
						.startsWith("player"))) {
					ServerPlayer target = getGameServer()
							.getPlayerController()
							.getPlayer(
									Integer.valueOf(result.getGeometry()
											.getParent().getName().substring(6)));
					if (target == player) {
						continue;
					}
					mark(target);
					break;
				} else if (getGameServer().getWorldController().isFlube(
						result.getGeometry())) {
					ServerFlube target = getGameServer().getWorldController()
							.getFlube(
									(long) result.getGeometry().getUserData(
											"id"));
					if (getGameServer().getWorldController()
							.isSwapableFlube(target)) {
						mark(target);
					}
					break;
				}
			} else {
				break;
			}
		}
	}

	@Override
	public void useSecondary(boolean activate) {
		if (!activate)
			return;

		swap();
	}

	private void swap() {
		if (this.markers[0] != null && this.markers[1] != null) {
			int pA = -1, pB = -1;
			long fA = 0, fB = 0;

			if (this.markers[0] instanceof ServerPlayer) {
				pA = ((ServerPlayer) this.markers[0]).getId();
			} else if (this.markers[0] instanceof ServerFlube) {
				fA = ((ServerFlube) this.markers[0]).getId();
			}

			if (this.markers[1] instanceof ServerPlayer) {
				pB = ((ServerPlayer) this.markers[1]).getId();
			} else if (this.markers[1] instanceof ServerFlube) {
				fB = ((ServerFlube) this.markers[1]).getId();
			}

			this.markers[0] = null;
			this.markers[1] = null;

			SwapEvent event = new SwapEvent(pA, pB, fA, fB, true);
			getGameServer().getEventMachine().fireEvent(event);
		}
	}

	private void mark(ServerWorldObject obj) {
		for (int i = 0; i < markers.length; i++) {
			if (markers[i] == obj) {
				markers[i] = null;
				unmark(obj);
				return;
			}
		}

		for (int i = 0; i < markers.length; i++) {
			if (markers[i] == null) {
				markers[i] = obj;
				int playerid = -1;
				long flubeid = 0;
				if (obj instanceof ServerPlayer) {
					playerid = ((ServerPlayer) obj).getId();
				} else if (obj instanceof ServerFlube) {
					flubeid = ((ServerFlube) obj).getId();
				}
				obj.addMark(this.player);
				MarkEvent ev = new MarkEvent(this.player.getId(), false,
						flubeid, playerid, true);
				getGameServer().getEventMachine().fireEvent(ev);
				break;
			}
		}
	}

	private void unmark(ServerWorldObject obj) {
		int playerid = -1;
		long flubeid = 0;
		if (obj instanceof ServerPlayer) {
			playerid = ((ServerPlayer) obj).getId();
		} else if (obj instanceof ServerFlube) {
			flubeid = ((ServerFlube) obj).getId();
		}
		obj.removeMark(this.player);
		MarkEvent ev = new MarkEvent(this.player.getId(), true, flubeid,
				playerid, true);
		getGameServer().getEventMachine().fireEvent(ev);
	}

	public void resetMark(ServerWorldObject obj) {
		for (int i = 0; i < markers.length; i++) {
			if (markers[i] == obj) {
				markers[i] = null;
			}
		}
	}

	@Override
	public void reset() {
		for (int i = 0; i < markers.length; i++) {
			if (markers[i] != null) {
				unmark(markers[i]);
				markers[i] = null;
			}
		}
	}
	
	@Override
	public String getTypeName() {
		return TYPENAME;
	}


	@Override
	public void initGeometry() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new SwapperInfo(this.name, this.range, this.player.getId(), TYPENAME);
	}

	@Override
	public Node getGeometry() {
		return this.geom;
	}

	@Override
	public void setActive(boolean active) {

	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}
}
