/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class PlayerPhysic {
	private int id;
	private Vector3f translation;
	private Vector3f orientation;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Vector3f getTranslation() {
		return translation;
	}
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}
	public Vector3f getOrientation() {
		return orientation;
	}
	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
	}
}
