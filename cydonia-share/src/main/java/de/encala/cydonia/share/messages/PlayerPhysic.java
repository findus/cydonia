/**
 * 
 */
package de.encala.cydonia.share.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class PlayerPhysic {
	private int id;
	private Vector3f location;
	private Vector3f orientation;

	public PlayerPhysic() {
		
	}
	
	public PlayerPhysic (int id, Vector3f location, Vector3f orientation) {
		this.id = id;
		this.location = location;
		this.orientation = orientation;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Vector3f getLocation() {
		return location;
	}

	public void setLocation(Vector3f translation) {
		this.location = translation;
	}

	public Vector3f getOrientation() {
		return orientation;
	}

	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
	}
}
