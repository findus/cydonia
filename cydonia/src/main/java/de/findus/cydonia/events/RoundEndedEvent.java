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
public class RoundEndedEvent extends AbstractEvent {

	public RoundEndedEvent() {
		setNetworkEvent(false);
	}
	
	public RoundEndedEvent(boolean forward) {
		setNetworkEvent(forward);
	}
}
