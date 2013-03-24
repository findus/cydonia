/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.image.BufferedImage;

import com.jme3.scene.Node;

import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public interface Equipment {
	
	public String getTypeName();
	
	public void initGeometry();
	
	public void usePrimary(boolean activate);
	
	public void useSecondary(boolean activate);
	
	public void reset();
	
	public BufferedImage getHUDImage();
	
	public void setMainController(MainController mc);
	
	public void setPlayer(Player p);
	
	public EquipmentInfo getInfo();
	
	public void loadInfo(EquipmentInfo info);
	
	public Node getGeometry();
	
	public void setActive(boolean active);
}
