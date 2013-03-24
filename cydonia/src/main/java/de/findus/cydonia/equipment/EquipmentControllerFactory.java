/**
 * 
 */
package de.findus.cydonia.equipment;

import java.security.InvalidParameterException;
import java.util.HashMap;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class EquipmentControllerFactory {

	private static HashMap<String, EquipmentControllerFactory> instances = new HashMap<String, EquipmentControllerFactory>();
	
	public static void addType(String type, MainController mc) {
		EquipmentControllerFactory instance = new EquipmentControllerFactory(type, mc);
		instances.put(type, instance);
	}
	
	public static EquipmentControllerFactory getInstance(String type) {
		EquipmentControllerFactory instance = instances.get(type);
		if(instance == null) {
			throw new InvalidParameterException("No instance for type '" + type + "' is available.");
		}
		return instance;
	}
	
	private HashMap<String, EquipmentController> controller;
	private MainController maincontroller;
	private String type;
	
	/**
	 * 
	 */
	public EquipmentControllerFactory(String type, MainController mc) {
		this.maincontroller = mc;
		this.type = type;
		controller = new HashMap<String, EquipmentController>();
	}
	
	public EquipmentController getController(EquipmentModel e) {
		EquipmentController con = controller.get(e);
		if(con == null) {
			try {
				String classname = type + e.getControllerName();
				con = (EquipmentController) Class.forName(classname).newInstance();
				con.setMainController(maincontroller);
				controller.put(classname, con);
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return con;
	}

}
