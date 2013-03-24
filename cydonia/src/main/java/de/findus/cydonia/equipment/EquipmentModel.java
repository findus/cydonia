/**
 * 
 */
package de.findus.cydonia.equipment;

import com.jme3.scene.Node;

import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public interface EquipmentModel {
	
	public EquipmentController getController(String type, MainController mc);

	public void setPlayer(Player p);
	
	public Player getPlayer();
	
	public Node getGeometry();
	
	public EquipmentInfo getInfo();
}
