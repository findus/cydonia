/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.level.Flube;

/**
 * @author Findus
 *
 */
@Serializable
public class MoveableInfo {

	private long id;
	
	private boolean inWorld;
	
	private Vector3f location;
	
	private int type;
	
	private Vector3f origin;
	
	public MoveableInfo() {
		
	}
	
	public MoveableInfo(long id, boolean inWorld, Vector3f location, int type, Vector3f origin) {
		this.id = id;
		this.inWorld = inWorld;
		this.location = location;
		this.type = type;
		this.origin = origin;
	}

	public MoveableInfo(Flube m) {
		this.id = m.getId();
		this.inWorld = m.getModel().getParent() != null;
		this.location = m.getControl().getPhysicsLocation();
		this.type = m.getType();
		this.origin = m.getOrigin();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the inWorld
	 */
	public boolean isInWorld() {
		return inWorld;
	}

	/**
	 * @param inWorld the inWorld to set
	 */
	public void setInWorld(boolean inWorld) {
		this.inWorld = inWorld;
	}

	/**
	 * @return the location
	 */
	public Vector3f getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Vector3f getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3f origin) {
		this.origin = origin;
	}
	
}
