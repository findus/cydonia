/**
 * 
 */
package de.findus.cydonia.equipment.picker;

import java.security.InvalidParameterException;
import java.util.LinkedList;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import de.findus.cydonia.equipment.EquipmentModel;
import de.findus.cydonia.equipment.ServerEquipmentController;
import de.findus.cydonia.events.PickupEvent;
import de.findus.cydonia.events.PlaceEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PickerInfo;
import de.findus.cydonia.player.Player;
import de.findus.cydonia.server.GameServer;

/**
 * @author Findus
 *
 */
public class ServerPickerController implements ServerEquipmentController {

	private MainController maincontroller;

	/**
	 * 
	 */
	public ServerPickerController() {
		// TODO Auto-generated constructor stub
	}
	
	public void usePrimary(EquipmentModel e, boolean activate) {
		if(!activate || !(e instanceof PickerModel)) return;
		
		PickerModel picker = (PickerModel) e;
		
		if(picker.getRepository().size() > 0) {
			Flube m = picker.getRepository().get(0);
			if(m != null) {
				CollisionResult result = maincontroller.getWorldController().pickWorld(picker.getPlayer().getEyePosition(), picker.getPlayer().getViewDir());
				if(result != null && result.getDistance() <= picker.getRange() && maincontroller.getWorldController().isPlaceableSurface(result.getGeometry())) {
					Vector3f contactnormal = result.getContactNormal();
					Vector3f contactpos = result.getContactPoint();

					Vector3f loc;
					if(GameServer.FREE_PLACING) {
						loc = contactpos.add(contactnormal.mult(0.5f));
					}else {
						loc = result.getGeometry().getLocalTranslation().add(contactnormal);
					}

					PlaceEvent place = new PlaceEvent(picker.getPlayer().getId(), m.getId(), loc, true);
					maincontroller.getEventMachine().fireEvent(place);
				}
			}
		}
	}
	
	public void useSecondary(EquipmentModel e, boolean activate) {
		if(!activate || !(e instanceof PickerModel)) return;
		
		PickerModel picker = (PickerModel) e;
		
		if(picker.getRepository().size() < picker.getCapacity()) {
			CollisionResult result = maincontroller.getWorldController().pickWorld(picker.getPlayer().getEyePosition(), picker.getPlayer().getViewDir());
			if(result != null && canPickup(picker.getPlayer(), result.getGeometry(), result.getDistance(), picker.getRange())) {
				Flube m = maincontroller.getWorldController().getFlube((Long) result.getGeometry().getUserData("id"));

				PickupEvent pickup = new PickupEvent(picker.getPlayer().getId(), m.getId(), true);
				maincontroller.getEventMachine().fireEvent(pickup);
			}
		}
	}
	
	private boolean canPickup(Player p, Spatial g, float distance, float range) {
		if(distance <= range) {
			if(p != null && g != null) {
				if(maincontroller.getWorldController().isFlube(g) && g.getUserData("Type") != null) {
					int type = g.getUserData("Type");
					if(type == 0 || type == p.getTeam()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void setActive(EquipmentModel e, boolean active) {
		
	}

	@Override
	public void reset(EquipmentModel e) {
		if(!(e instanceof PickerModel)) {
			throw new InvalidParameterException("e must be of type " + PickerModel.class.getName());
		}

		PickerModel picker = (PickerModel) e;
		picker.repository = new LinkedList<Flube>();
	}

	@Override
	public EquipmentInfo getInfo(EquipmentModel e) {
		if(!(e instanceof PickerModel)) {
			throw new InvalidParameterException("e must be of type " + PickerModel.class.getName());
		}
		PickerModel picker = (PickerModel) e;
		return new PickerInfo(picker);
	}

	@Override
	public void setMainController(MainController mc) {
		this.maincontroller = mc;
	}
}
