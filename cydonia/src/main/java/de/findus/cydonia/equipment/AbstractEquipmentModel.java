/**
 * 
 */
package de.findus.cydonia.equipment;

import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public abstract class AbstractEquipmentModel implements EquipmentModel {

	protected Player player;
	
	public AbstractEquipmentModel() {
		
	}
	
	public AbstractEquipmentModel(Player p) {
		this.player = p;
	}

	@Override
	public void setPlayer(Player p) {
		this.player = p;
	}
	
	@Override
	public Player getPlayer() {
		return this.player;
	}
}
