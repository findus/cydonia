/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;

import de.findus.cydonia.events.AddEvent;
import de.findus.cydonia.events.RemoveEvent;
import de.findus.cydonia.level.Flag;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.SpawnPoint;
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
			if("flag".equalsIgnoreCase(getObjectType())) {
				loc.addLocal(new Vector3f(0, 0.5f, 0));
			}else if("spawnpoint".equalsIgnoreCase(getObjectType())) {
				loc.addLocal(new Vector3f(0, 0.5f, 0));
			}
			
			long id = -1;
			if("flube".equalsIgnoreCase(getObjectType())) {
				id = getMainController().getWorldController().getFreeFlubeId();
			}else if("flag".equalsIgnoreCase(getObjectType())) {
				id = getMainController().getWorldController().getFreeFlagId();
			}else if("spawnpoint".equalsIgnoreCase(getObjectType())) {
				id = getMainController().getWorldController().getFreeSpawnPointId();
			}

			if(id < 0) {
				System.out.println("no free id found");
			}else {
				AddEvent add = new AddEvent(this.player.getId(), id, this.getObjectType(), this.getObjectSpec(), loc, true);
				getMainController().getEventMachine().fireEvent(add);
			}
		}
	}
	
	@Override
	public void useSecondary(boolean activate) {
		if(!activate) return;
		
			CollisionResult result = getMainController().getWorldController().pickWorld(this.player.getEyePosition(), this.player.getViewDir());
			if(result != null && result.getDistance() <= this.getRange()) {
				if(getMainController().getWorldController().isFlube(result.getGeometry())) {
					Flube m = getMainController().getWorldController().getFlube((Long) result.getGeometry().getUserData("id"));
					RemoveEvent remove = new RemoveEvent(this.player.getId(), m.getId(), "flube", true);
					getMainController().getEventMachine().fireEvent(remove);
				}else if(getMainController().getWorldController().isFlag(result.getGeometry())) {
					Flag f = getMainController().getWorldController().getFlag((Integer) result.getGeometry().getUserData("id"));
					RemoveEvent remove = new RemoveEvent(this.player.getId(), f.getId(), "flag", true);
					getMainController().getEventMachine().fireEvent(remove);
				}else if(getMainController().getWorldController().isSpawnPoint(result.getGeometry())) {
					SpawnPoint sp = getMainController().getWorldController().getSpawnPoint((Integer) result.getGeometry().getUserData("id"));
					RemoveEvent remove = new RemoveEvent(this.player.getId(), sp.getId(), "spawnpoint", true);
					getMainController().getEventMachine().fireEvent(remove);
				}

				
			}
	}

}
