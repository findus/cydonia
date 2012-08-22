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
public class BulletPhysic {
	
	private long id; 
	private int sourceid;
	private Vector3f translation;
	private Vector3f velocity;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the sourceid
	 */
	public int getSourceid() {
		return sourceid;
	}
	/**
	 * @param sourceid the sourceid to set
	 */
	public void setSourceid(int sourceid) {
		this.sourceid = sourceid;
	}
	public Vector3f getTranslation() {
		return translation;
	}
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}
	public Vector3f getVelocity() {
		return velocity;
	}
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

}
