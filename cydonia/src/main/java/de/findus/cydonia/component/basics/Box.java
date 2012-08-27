/**
 * 
 */
package de.findus.cydonia.component.basics;

import java.awt.Dimension;

import de.findus.cydonia.component.ComponentEntity;

/**
 * @author Findus
 *
 */
public class Box extends ComponentEntity {
	
	public Box() {
		this.size = new Dimension(1, 1);
		this.links = LINK_FRONT & LINK_BACK & LINK_LEFT & LINK_RIGHT & LINK_TOP & LINK_BOTTOM;
	}
}
