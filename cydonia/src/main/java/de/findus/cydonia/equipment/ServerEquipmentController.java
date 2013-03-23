/**
 * 
 */
package de.findus.cydonia.equipment;

import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public interface ServerEquipmentController extends EquipmentController {
	
	public EquipmentInfo getInfo(EquipmentModel e);
	
}
