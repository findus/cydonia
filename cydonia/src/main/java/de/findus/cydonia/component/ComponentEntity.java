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
	
	public static final byte LINK_NONE = 0x00;
	public static final byte LINK_FRONT = 0x01;
	public static final byte LINK_BACK = 0x02;
	public static final byte LINK_LEFT = 0x04;
	public static final byte LINK_RIGHT = 0x08;
	public static final byte LINK_TOP = 0x10;
	public static final byte LINK_BOTTOM = 0x20;
	
	protected String name;
	
	protected Dimension size;
	
	protected int resistance;
	
	protected int flexibility;
	
	protected byte links = LINK_NONE;
	
	
	
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

	/**
	 * @return the links
	 */
	public byte getLinks() {
		return links;
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(byte links) {
		this.links = links;
	}

}
