/**
 * 
 */
package de.findus.cydonia.component;

import java.util.HashMap;
import java.util.Map;

import de.findus.cydonia.component.basics.Box;

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
		
		ComponentEntity com = new Box();
		com.setName("Box1");
		com.setFlexibility(1);
		com.setResistance(1);
		this.saveNew(com);
		
		com = new Box();
		com.setName("Box2");
		com.setFlexibility(1);
		com.setResistance(1);
		this.saveNew(com);
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
