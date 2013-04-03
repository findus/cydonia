/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import de.findus.cydonia.events.AddEvent;
import de.findus.cydonia.events.RemoveEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.server.GameServer;

/**
 * @author Findus
 *
 */
public class ServerEditor extends Editor {

	/**
	 * 
	 */
	public ServerEditor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param mainController
	 */
	public ServerEditor(String name, float range, String objectType, int objectSpec, Player player,
			MainController mainController) {
		super(name, range, objectType, objectSpec, player, mainController);
	}

	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		CollisionResult result = getMainController().getWorldController().pickWorld(this.player.getEyePosition(), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange() && getMainController().getWorldController().isFlube(result.getGeometry())) {
			Vector3f contactnormal = result.getContactNormal();
			Vector3f contactpos = result.getContactPoint();

			Vector3f loc;
			if(GameServer.FREE_PLACING) {
				loc = contactpos.add(contactnormal.mult(0.5f));
			}else {
				loc = result.getGeometry().getLocalTranslation().add(contactnormal);
			}
			long flubeid = getMainController().getWorldController().getFreeFlubeId();
			if(flubeid < 0) {
				System.out.println("no free flube id");
			}else {
				AddEvent add = new AddEvent(this.player.getId(), flubeid, this.getObjectType(), this.getObjectSpec(), loc, true);
				getMainController().getEventMachine().fireEvent(add);
			}
		}
	}
	
	@Override
	public void useSecondary(boolean activate) {
		if(!activate) return;
		
			CollisionResult result = getMainController().getWorldController().pickWorld(this.player.getEyePosition(), this.player.getViewDir());
			if(result != null && canPickup(this.player, result.getGeometry(), result.getDistance())) {
				Flube m = getMainController().getWorldController().getFlube((Long) result.getGeometry().getUserData("id"));

				RemoveEvent remove = new RemoveEvent(this.player.getId(), m.getId(), this.getObjectType(), true);
				getMainController().getEventMachine().fireEvent(remove);
			}
	}
	
	private boolean canPickup(Player p, Spatial g, float distance) {
		if(distance <= this.getRange()) {
			if(p != null && g != null) {
				if(getMainController().getWorldController().isFlube(g)) {
					return true;
				}
			}
		}
		return false;
	}

}
