/**
 * 
 */
package de.findus.cydonia.component;

import java.awt.Dimension;

import de.findus.cydonia.entity.EntityBase;

/**
 * @author Findus
 *
 */
public class ComponentEntity extends EntityBase {

	private String name;
	
	protected Dimension size;
	
	private int resistance;
	
	private int flexibility;
	
	
	
	/**
	 * 
	 */
	public ComponentEntity() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the resistance
	 */
	public int getResistance() {
		return resistance;
	}

	/**
	 * @param resistance the resistance to set
	 */
	public void setResistance(int resistance) {
		this.resistance = resistance;
	}

	/**
	 * @return the flexibility
	 */
	public int getFlexibility() {
		return flexibility;
	}

	/**
	 * @param flexibility the flexibility to set
	 */
	public void setFlexibility(int flexibility) {
		this.flexibility = flexibility;
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
