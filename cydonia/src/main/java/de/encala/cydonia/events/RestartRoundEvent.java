/**
 * 
 */
package de.encala.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class RestartRoundEvent extends AbstractEvent {

	public RestartRoundEvent() {
		setNetworkEvent(false);
	}

	public RestartRoundEvent(boolean forward) {
		setNetworkEvent(forward);
	}
}
