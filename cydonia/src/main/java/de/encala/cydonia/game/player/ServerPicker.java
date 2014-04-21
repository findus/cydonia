/**
 * 
 */
package de.encala.cydonia.game.player;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import de.encala.cydonia.game.level.Flube;
import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.share.MainController;
import de.encala.cydonia.share.events.PickupEvent;
import de.encala.cydonia.share.events.PlaceEvent;

/**
 * @author encala
 * 
 */
public class ServerPicker extends Picker {

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
	 * @param mainController
	 */
	public ServerPicker(String name, float range, int capacity, Player player,
			MainController mainController) {
		super(name, range, capacity, player, mainController);
	}

	@Override
	public void usePrimary(boolean activate) {
		if (!activate)
			return;

		if (this.getRepository().size() > 0) {
			Flube m = this.getRepository().get(0);
			if (m != null) {
				CollisionResult result = getMainController()
						.getWorldController().pickWorld(
								this.player.getEyePosition(),
								this.player.getViewDir());
				if (result != null
						&& result.getDistance() <= this.getRange()
						&& getMainController().getWorldController()
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

					if (!getMainController().getWorldController().isInFlagArea(
							loc)) {
						PlaceEvent place = new PlaceEvent(this.player.getId(),
								m.getId(), loc, true);
						getMainController().getEventMachine().fireEvent(place);
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
			CollisionResult result = getMainController().getWorldController()
					.pickWorld(this.player.getEyePosition(),
							this.player.getViewDir());
			if (result != null
					&& canPickup(this.player, result.getGeometry(),
							result.getDistance())) {
				Flube m = getMainController().getWorldController().getFlube(
						(Long) result.getGeometry().getUserData("id"));

				PickupEvent pickup = new PickupEvent(this.player.getId(),
						m.getId(), true);
				getMainController().getEventMachine().fireEvent(pickup);
			}
		}
	}

	private boolean canPickup(Player p, Spatial g, float distance) {
		if (distance <= this.getRange()) {
			if (p != null && g != null) {
				if (getMainController().getWorldController().isFlube(g)
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

}
