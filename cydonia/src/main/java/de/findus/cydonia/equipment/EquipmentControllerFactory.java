/**
 * 
 */
package de.findus.cydonia.equipment;

import java.util.HashMap;

/**
 * @author Findus
 *
 */
public class EquipmentControllerFactory {

	private static EquipmentControllerFactory instance;
	
	private HashMap<Class<? extends EquipmentModel>, EquipmentController> controller;
	
	public static EquipmentControllerFactory getInstance() {
		if(instance == null) {
			instance = new EquipmentControllerFactory();
		}
		return instance;
	}
	/**
	 * 
	 */
	public EquipmentControllerFactory() {
		controller = new HashMap<Class<? extends EquipmentModel>, EquipmentController>();
	}
	
	public EquipmentController getController(EquipmentModel e, String type) {
		EquipmentController con = controller.get(e);
		if(con == null) {
			try {
				con = (EquipmentController) Class.forName(type + e.getControllerName()).newInstance();
				controller.put(e.getClass(), con);
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
