/**
 * 
 */
package de.findus.cydonia.server;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * A <code>InputUpdate</code> is message containing an input state.
 * @author Findus
 *
 */
@Serializable
public class InputUpdate extends AbstractMessage {

	private PlayerInputState inputs;
	private Vector3f viewDir;
	private int playerId;

	/**
	 * Returns the input state.
	 * @return the input state
	 */
	public PlayerInputState getInputs() {
		return inputs;
	}

	/**
	 * Sets the input state.
	 * @param inputs the input state
	 */
	public void setInputs(PlayerInputState inputs) {
		this.inputs = inputs;
	}

	public Vector3f getViewDir() {
		return viewDir;
	}

	public void setViewDir(Vector3f viewDir) {
		this.viewDir = viewDir;
	}

	/**
	 * Returns the Id of the Player this input state belongs to.
	 * @return the Id
	 */
	public int getPlayerId() {
		return this.playerId;
	}
	
	/**
	 * Sets the PlayerId.
	 * @param id the Id
	 */
	public void setPlayerId(int id) {
		this.playerId = id;
	}
}
