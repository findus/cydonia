/**
 * 
 */
package de.encala.cydonia.events;

/**
 * @author encala
 * 
 */
public abstract class AbstractEvent implements Event {

	transient protected boolean network;

	/**
	 * Default constuctor. Network forwarding is set to false.
	 */
	public AbstractEvent() {
		network = false;
	}

	/**
	 * Constructor sets network forwarding to specified value.
	 * 
	 * @param net
	 *            network forwarding
	 */
	public AbstractEvent(boolean net) {
		network = net;
	}

	@Override
	public boolean isNetworkEvent() {
		return network;
	}

	@Override
	public void setNetworkEvent(boolean net) {
		network = net;
	}

}
