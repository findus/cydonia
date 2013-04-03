/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class GameModeEvent extends AbstractEvent {

	private String mode;
	
	/**
	 * 
	 */
	public GameModeEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param net
	 */
	public GameModeEvent(String mode, boolean net) {
		super(net);
		this.setMode(mode);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
