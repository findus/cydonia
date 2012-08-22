/**
 * 
 */
package de.findus.cydonia.building;

import de.findus.cydonia.component.ComponentEntity;

/**
 * @author Findus
 *
 */
public class BuilderEntity {

	private ComponentEntity[][] table;
	
	/**
	 * 
	 */
	public BuilderEntity() {
		table = new ComponentEntity[3][3];
	}
	
	public void setComponent(int x, int y, ComponentEntity component) {
		table[x][y] = component;
	}
	
	public ComponentEntity getComponent(int x, int y) {
		return table[x][y];
	}

}
