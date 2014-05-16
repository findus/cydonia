/**
 * 
 */
package de.encala.cydonia.share.messages;

import java.util.Collection;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class LocationUpdatedMessage extends AbstractMessage {

	private Collection<PlayerPhysic> playerphysics;

	public LocationUpdatedMessage() {
		setReliable(false);
	}

	public void setPlayerPhysics(Collection<PlayerPhysic> p) {
		this.playerphysics = p;
	}

	public Collection<PlayerPhysic> getPlayerPhysics() {
		return this.playerphysics;
	}
}
