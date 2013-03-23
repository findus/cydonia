/**
 * 
 */
package de.findus.cydonia.equipment;

import com.jme3.scene.Node;

import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public interface EquipmentModel {

	public void setPlayer(Player p);
	
	public Player getPlayer();
	
	public Node getGeometry();
}
