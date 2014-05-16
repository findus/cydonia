/**
 * 
 */
package de.encala.cydonia.share.events;

/**
 * @author encala
 * 
 */
public interface EventListener {

	/**
	 * Called by EventMachine.
	 * 
	 * @param e
	 *            the new event
	 */
	public void newEvent(Event e);
}
