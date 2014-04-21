/**
 * 
 */
package de.encala.cydonia.game.equipment;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.share.equipment.AbstractEquipmentFactory;

/**
 * @author encala
 * 
 */
public class ClientEquipmentFactory extends AbstractEquipmentFactory {

	private GameController gameController;

	/**
	 * 
	 */
	public ClientEquipmentFactory(GameController gameController) {
		super();
		
		this.gameController = gameController;
	}

	public ClientEquipment create(String equipmentType) {
		EquipmentDescription desc = this.descriptions.get(equipmentType);
		if (desc == null)
			return null;
		ClientEquipment e = null;
		try {
			e = desc.getClientClass().newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		if (e != null) {
			e.setGameController(gameController);
			e.initGeometry();
		}

		return e;
	}

}
