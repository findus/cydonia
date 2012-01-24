/**
 * 
 */
package de.findus.cydonia.server;

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
}
