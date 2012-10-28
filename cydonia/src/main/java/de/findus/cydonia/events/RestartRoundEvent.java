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
public class RestartRoundEvent extends AbstractEvent {

	public RestartRoundEvent() {
		setNetworkEvent(false);
	}
	
	public RestartRoundEvent(boolean forward) {
		setNetworkEvent(forward);
	}
}
