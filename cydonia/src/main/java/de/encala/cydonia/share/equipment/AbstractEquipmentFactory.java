/**
 * 
 */
package de.encala.cydonia.share.equipment;

import java.util.HashMap;

import de.encala.cydonia.game.equipment.ClientEditor;
import de.encala.cydonia.game.equipment.ClientEquipment;
import de.encala.cydonia.game.equipment.ClientPicker;
import de.encala.cydonia.game.equipment.ClientSwapper;
import de.encala.cydonia.server.equipment.ServerEditor;
import de.encala.cydonia.server.equipment.ServerEquipment;
import de.encala.cydonia.server.equipment.ServerPicker;
import de.encala.cydonia.server.equipment.ServerSwapper;

/**
 * @author encala
 * 
 */
public abstract class AbstractEquipmentFactory {

	protected HashMap<String, EquipmentDescription> descriptions;

	/**
	 * 
	 */
	public AbstractEquipmentFactory() {
		this.descriptions = new HashMap<String, EquipmentDescription>();
		loadDefaults();
	}

	private void loadDefaults() {
		EquipmentDescription d = new EquipmentDescription();
		d.typeName = "Picker";
		d.clientClass = ClientPicker.class;
		d.serverClass = ServerPicker.class;
		this.descriptions.put(d.typeName, d);

		d = new EquipmentDescription();
		d.typeName = "Editor";
		d.clientClass = ClientEditor.class;
		d.serverClass = ServerEditor.class;
		this.descriptions.put(d.typeName, d);

		d = new EquipmentDescription();
		d.typeName = "Swapper";
		d.clientClass = ClientSwapper.class;
		d.serverClass = ServerSwapper.class;
		this.descriptions.put(d.typeName, d);
	}

	protected class EquipmentDescription {
		private String typeName;
		private Class<? extends ServerEquipment> serverClass;
		private Class<? extends ClientEquipment> clientClass;
		
		public Class<? extends ServerEquipment> getServerClass() {
			return this.serverClass;
		}
		
		public Class<? extends ClientEquipment> getClientClass() {
			return this.clientClass;
		}
	}
}
