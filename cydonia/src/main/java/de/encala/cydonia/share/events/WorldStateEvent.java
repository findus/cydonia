/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.share.messages.WorldState;

/**
 * @author encala
 * 
 */
@Serializable
public class WorldStateEvent extends AbstractEvent {

	private WorldState worldState;

	/**
	 * 
	 */
	public WorldStateEvent() {

	}

	/**
	 * @param net
	 */
	public WorldStateEvent(WorldState worldState, boolean net) {
		super(net);
		this.setWorldState(worldState);
	}

	public WorldState getWorldState() {
		return worldState;
	}

	public void setWorldState(WorldState worldState) {
		this.worldState = worldState;
	}
}
