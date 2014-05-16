/**
 * 
 */
package de.encala.cydonia.server.equipment;

import java.util.HashMap;
import java.util.Map;

import de.encala.cydonia.server.GameServer;

/**
 * @author encala
 * 
 */
public class ServerEquipmentFactory {

	private GameServer gameServer;
	
	private Map<String, Class<? extends ServerEquipment>> descriptions;

	/**
	 * 
	 */
	public ServerEquipmentFactory(GameServer gameServer) {
		super();
		
		this.gameServer = gameServer;
		
		setDefaults();
	}
	
	private void setDefaults() {
		this.descriptions = new HashMap<String, Class<? extends ServerEquipment>>();
		this.descriptions.put(ServerPicker.TYPENAME, ServerPicker.class);
		this.descriptions.put(ServerSwapper.TYPENAME, ServerSwapper.class);
		this.descriptions.put(ServerEditor.TYPENAME, ServerEditor.class);
	}

	public ServerEquipment create(String equipmentType) {
		ServerEquipment e = null;
		try {
			e = descriptions.get(equipmentType).newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		if (e != null) {
			e.setGameServer(gameServer);
			e.initGeometry();
		}

		return e;
	}
}
