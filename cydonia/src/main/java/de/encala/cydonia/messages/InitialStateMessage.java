/**
 * 
 */
package de.encala.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.level.WorldState;

/**
 * @author encala
 * 
 */
@Serializable
public class InitialStateMessage extends AbstractMessage {

	private WorldState worldState;

	private int partcount;

	public InitialStateMessage() {
		setReliable(true);
	}

	public WorldState getWorldState() {
		return worldState;
	}

	public void setWorldState(WorldState worldState) {
		this.worldState = worldState;
	}

	public int getPartcount() {
		return partcount;
	}

	public void setPartcount(int partcount) {
		this.partcount = partcount;
	}
}
