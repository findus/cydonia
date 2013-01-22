/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.level.WorldController;
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
	
	public void setWorldController(WorldController wc);
	
	public void setEventMachine(EventMachine em);
	
	public EquipmentInfo getInfo();
	
	public void loadInfo(EquipmentInfo info);
}
