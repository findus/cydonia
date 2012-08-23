/**
 * 
 */
package de.findus.cydonia.component;

import java.awt.Dimension;

/**
 * @author Findus
 *
 */
public class ComponentGroupEntity {

	private ComponentEntity[][] components;
	
	public void setComponents(ComponentEntity[][] components) {
		this.components = components;
	}
	
	public Dimension getSize() {
		int height = components.length;
		int width = 0;
		for(ComponentEntity[] row : components) {
			if(row.length > width) {
				width = row.length; 
			}
		}
		Dimension size = new Dimension(height, width);
		return size;
	}
}
