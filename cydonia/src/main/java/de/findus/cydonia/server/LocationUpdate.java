/**
 * 
 */
package de.findus.cydonia.server;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class LocationUpdate extends AbstractMessage {

	private Vector3f location;
	private Vector3f orientation;
	
	public Vector3f getLocation() {
		return this.location;
	}
	
	public Vector3f getOrientation() {
		return this.orientation;
	}
}
