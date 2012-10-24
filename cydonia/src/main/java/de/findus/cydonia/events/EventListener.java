/**
 * 
 */
package de.findus.cydonia.events;

/**
 * @author Findus
 *
 */
public interface EventListener {

	/**
	 * Called by EventMachine.
	 * @param e the new event
	 */
	public void newEvent(Event e);
}
