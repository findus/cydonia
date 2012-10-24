/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.events.Event;

/**
 * @author Findus
 *
 */
@Serializable
public class EventMessage extends AbstractMessage {

	private Event event;
	
	/**
	 * 
	 */
	public EventMessage() {
		setReliable(true);
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}
}
