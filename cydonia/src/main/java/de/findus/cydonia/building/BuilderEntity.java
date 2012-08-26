/**
 * 
 */
package de.findus.cydonia.building;

import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;

import de.findus.cydonia.component.ComponentEntity;

/**
 * @author Findus
 *
 */
public class BuilderEntity {
	
	private HashMap<String, ComponentEntity> table;
	
	private Dimension size;
	
	/**
	 * 
	 */
	public BuilderEntity() {
		table = new HashMap<String, ComponentEntity>();
		size = new Dimension(3, 3);
	}
	
	/**
	 * Inserts the given component at the specified field of this builders table.
	 * If the specified coordinates are not valid (not in range) nothing is done.
	 * If component is null, the object at the specified field is removed.
	 * 
	 * @param x x-coordinate of the field (horizontal from left; starting with 0)
	 * @param y y-coordinate of the field (vertical from top; starting with 0)
	 * @param component the component or null 
	 */
	public void setComponent(int x, int y, ComponentEntity component) {
		if(x<0 || x>size.getWidth()) {
			return;
		}
		if(y<0 || y>size.getHeight()) {
			return ;
		}
		
		if(component == null) {
			table.remove("x" + x + "y" + y);
		}
		
		table.put("x" + x + "y" + y, component);
	}
	
	public ComponentEntity getComponent(int x, int y) {
		return table.get("x" + x + "y" + y);
	}
	
	public Collection<ComponentEntity> getComponentList() {
		return table.values();
	}

	/**
	 * @return the size
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

}
