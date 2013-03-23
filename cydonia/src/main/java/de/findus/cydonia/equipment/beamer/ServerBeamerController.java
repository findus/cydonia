/**
 * 
 */
package de.findus.cydonia.equipment.beamer;

import java.security.InvalidParameterException;

import de.findus.cydonia.equipment.EquipmentModel;
import de.findus.cydonia.equipment.ServerEquipmentController;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public class ServerBeamerController implements ServerEquipmentController {

	private MainController maincontroller;

	/**
	 * 
	 */
	public ServerBeamerController() {
		// TODO Auto-generated constructor stub
	}
	
	public void usePrimary(EquipmentModel e, boolean activate) {
		if(!(e instanceof BeamerModel)) return;
		
		BeamerModel beamer = (BeamerModel) e;
		beamer.setBeaming(activate);
	}
	
	public void useSecondary(EquipmentModel e, boolean activate) {
		
	}
	
	@Override
	public void setActive(EquipmentModel e, boolean active) {
		
	}

	@Override
	public void reset(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		
		BeamerModel beamer = (BeamerModel) e;
		beamer.beaming = false;
	}

	@Override
	public EquipmentInfo getInfo(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		BeamerModel beamer = (BeamerModel) e;
		return new BeamerInfo(beamer);
	}

	@Override
	public void setMainController(MainController mc) {
		this.maincontroller = mc;
	}
}
