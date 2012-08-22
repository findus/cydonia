/**
 * 
 */
package de.findus.cydonia.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Findus
 *
 */
public class ComponentDAO {

	private static ComponentDAO instance;
	
	private long counter = 1;
	
	private Map<Long, ComponentEntity> componentList;
	
	public static ComponentDAO getInstance() {
		if(instance == null) {
			instance = new ComponentDAO();
		}
		return instance;
	}
	
	/**
	 * 
	 */
	private ComponentDAO() {
		componentList = new HashMap<Long, ComponentEntity>();
	}
	
	public ComponentEntity getComponent(long id) {
		if(componentList.containsKey(id)) {
			return componentList.get(id);
		}
		return null;
	}
	
	public long saveNew(ComponentEntity component) {
		component.setId(counter++);
		componentList.put(component.getId(), component);
		return component.getId();
	}

}
