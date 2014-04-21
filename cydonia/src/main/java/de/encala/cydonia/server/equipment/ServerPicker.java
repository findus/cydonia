/**
 * 
 */
package de.encala.cydonia.server.equipment;

import java.util.LinkedList;
import java.util.List;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.server.world.ServerFlube;
import de.encala.cydonia.share.events.PickupEvent;
import de.encala.cydonia.share.events.PlaceEvent;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.PickerInfo;

/**
 * @author encala
 * 
 */
public class ServerPicker extends AbstractServerEquipment {

	private static final String TYPENAME = "Picker";

	private String name;

	private float range;

	private int capacity;

	private List<ServerFlube> repository = new LinkedList<ServerFlube>();
	
	/**
	 * 
	 */
	public ServerPicker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param gameServer
	 */
	public ServerPicker(String name, float range, int capacity, ServerPlayer player,
			GameServer gameServer) {
		super(player, gameServer);
	}

	@Override
	public void usePrimary(boolean activate) {
		if (!activate)
			return;

		if (this.getRepository().size() > 0) {
			ServerFlube m = this.getRepository().get(0);
			if (m != null) {
				CollisionResult result = getGameServer()
						.getWorldController().pickWorld(
								this.player.getEyePosition(),
								this.player.getViewDir());
				if (result != null
						&& result.getDistance() <= this.getRange()
						&& getGameServer().getWorldController()
								.isPlaceableSurface(result.getGeometry())) {
					Vector3f contactnormal = result.getContactNormal();
					Vector3f contactpos = result.getContactPoint();

					Vector3f loc;
					if (GameServer.FREE_PLACING) {
						loc = contactpos.add(contactnormal.mult(0.5f));
					} else {
						loc = result.getGeometry().getLocalTranslation()
								.add(contactnormal);
					}

					if (!getGameServer().getWorldController().isInFlagArea(
							loc)) {
						PlaceEvent place = new PlaceEvent(this.player.getId(),
								m.getId(), loc, true);
						getGameServer().getEventMachine().fireEvent(place);
					}
				}
			}
		}
	}

	@Override
	public void useSecondary(boolean activate) {
		if (!activate)
			return;

		if (this.getRepository().size() < this.getCapacity()) {
			CollisionResult result = getGameServer().getWorldController()
					.pickWorld(this.player.getEyePosition(),
							this.player.getViewDir());
			if (result != null
					&& canPickup(this.player, result.getGeometry(),
							result.getDistance())) {
				ServerFlube m = getGameServer().getWorldController().getFlube(
						(Long) result.getGeometry().getUserData("id"));

				PickupEvent pickup = new PickupEvent(this.player.getId(),
						m.getId(), true);
				getGameServer().getEventMachine().fireEvent(pickup);
			}
		}
	}

	private boolean canPickup(ServerPlayer p, Spatial g, float distance) {
		if (distance <= this.getRange()) {
			if (p != null && g != null) {
				if (getGameServer().getWorldController().isFlube(g)
						&& g.getUserData("Type") != null) {
					int type = g.getUserData("Type");
					if (type == 0 || type == p.getTeam()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void initGeometry() {

	}
	
	@Override
	public void reset() {
		// this.repository = new LinkedList<ServerFlube>();
	}

	@Override
	public EquipmentInfo getInfo() {
		return new PickerInfo(this);
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the range
	 */
	public float getRange() {
		return range;
	}

	/**
	 * @param range
	 *            the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity
	 *            the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the repository
	 */
	public List<ServerFlube> getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(List<ServerFlube> repository) {
		this.repository = repository;
	}

	@Override
	public Node getGeometry() {
		return null;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTypeName() {
		return TYPENAME;
	}

}
