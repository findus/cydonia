/**
 * 
 */
package de.findus.cydonia.player;

import java.util.HashMap;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class EquipmentFactory {

	public enum ServiceType {
		SERVER, CLIENT
	}

	private ServiceType serviceType;
	private MainController mainController;
	private HashMap<String, EquipmentDescription> descriptions;
	
	/**
	 * 
	 */
	public EquipmentFactory(ServiceType st, MainController mc) {
		this.serviceType = st;
		this.mainController = mc;
		
		this.descriptions = new HashMap<String, EquipmentDescription>();
		loadDefaults();
	}
	
	private void loadDefaults() {
		EquipmentDescription d = new EquipmentDescription();
		d.typeName = "Beamer";
		d.clientClass = ClientBeamer.class;
		d.serverClass = ServerBeamer.class;
		this.descriptions.put(d.typeName, d);
		
		d = new EquipmentDescription();
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
		d.typeName = "Phaser";
		d.clientClass = Phaser.class;
		d.serverClass = ServerPhaser.class;
		this.descriptions.put(d.typeName, d);
		
		d = new EquipmentDescription();
		d.typeName = "Pusher";
		d.clientClass = Pusher.class;
		d.serverClass = ServerPusher.class;
		this.descriptions.put(d.typeName, d);
	}
	
	public Equipment create(String equipmentType) {
		EquipmentDescription desc = this.descriptions.get(equipmentType);
		if(desc == null) return null;
		Equipment e = null;
		try {
			if(this.serviceType == ServiceType.SERVER) {
				e = desc.serverClass.newInstance();
			}else if(this.serviceType == ServiceType.CLIENT) {
				e = desc.clientClass.newInstance();
			}
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		if(e != null) {
			e.setMainController(mainController);
			e.initGeometry();
		}
		
		return e;
	}
	
	private class EquipmentDescription {
		private String typeName;
		private Class<? extends Equipment> serverClass;
		private Class<? extends Equipment> clientClass;
	}
}
