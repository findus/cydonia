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
	
	public void usePrimary(boolean activate);
	
	public void useSecondary(boolean activate);
	
	public void reset();
	
	public BufferedImage getHUDImage();
	
	public void setMainController(MainController mc);
	
	public void setPlayer(Player p);
	
	public EquipmentInfo getInfo();
	
	public void loadInfo(EquipmentInfo info);
}
