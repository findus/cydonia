/**
 * 
 */
package de.encala.cydonia.game.equipment;

import java.util.HashMap;
import java.util.Map;

import de.encala.cydonia.game.GameController;

/**
 * @author encala
 * 
 */
public class ClientEquipmentFactory {

	private GameController gameController;
	
	private Map<String, Class<? extends ClientEquipment>> descriptions;

	/**
	 * 
	 */
	public ClientEquipmentFactory(GameController gameController) {
		super();
		
		this.gameController = gameController;
		
		setDefaults();
	}
	
	private void setDefaults() {
		this.descriptions = new HashMap<String, Class<? extends ClientEquipment>>();
		this.descriptions.put(ClientPicker.TYPENAME, ClientPicker.class);
		this.descriptions.put(ClientSwapper.TYPENAME, ClientSwapper.class);
		this.descriptions.put(ClientEditor.TYPENAME, ClientEditor.class);
	}

	public ClientEquipment create(String equipmentType) {
		ClientEquipment e = null;
		try {
			e = descriptions.get(equipmentType).newInstance();
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
