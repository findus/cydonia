/**
 * 
 */
package de.encala.cydonia.server.equipment;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.share.equipment.AbstractEquipmentFactory;

/**
 * @author encala
 * 
 */
public class ServerEquipmentFactory extends AbstractEquipmentFactory {

	private GameServer gameServer;

	/**
	 * 
	 */
	public ServerEquipmentFactory(GameServer gameServer) {
		super();
		
		this.gameServer = gameServer;
	}

	public ServerEquipment create(String equipmentType) {
		EquipmentDescription desc = this.descriptions.get(equipmentType);
		if (desc == null)
			return null;
		ServerEquipment e = null;
		try {
			e = desc.getServerClass().newInstance();
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
