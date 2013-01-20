/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public interface Equipment {
	
	public void usePrimary();
	
	public void useSecondary();
	
	public void reset();
	
	public String getImagePath();
	
	public EquipmentInfo getInfo();
	
	public void loadInfo(EquipmentInfo info);
}
