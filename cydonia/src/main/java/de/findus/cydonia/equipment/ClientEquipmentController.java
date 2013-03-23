/**
 * 
 */
package de.findus.cydonia.equipment;

import java.awt.image.BufferedImage;

import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public interface ClientEquipmentController extends EquipmentController {

	public BufferedImage getHUDImage(EquipmentModel e);
	
	public void loadInfo(EquipmentModel e, EquipmentInfo info);
}
