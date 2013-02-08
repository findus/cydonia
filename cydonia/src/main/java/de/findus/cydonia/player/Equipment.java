/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.image.BufferedImage;

import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public interface Equipment {
	
	public void usePrimary();
	
	public void useSecondary();
	
	public void reset();
	
	public BufferedImage getHUDImage();
	
	public void setMainController(MainController mc);
	
	public EquipmentInfo getInfo();
	
	public void loadInfo(EquipmentInfo info);
}
