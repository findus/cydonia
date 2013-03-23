/**
 * 
 */
package de.findus.cydonia.equipment;

import de.findus.cydonia.main.MainController;


/**
 * @author Findus
 *
 */
public interface EquipmentController {
	
	public void setMainController(MainController mc);
	
	public void setActive(EquipmentModel e, boolean active);
	
	public void usePrimary(EquipmentModel e, boolean activate);
	
	public void useSecondary(EquipmentModel e, boolean activate);
	
	public void reset(EquipmentModel e);
}
