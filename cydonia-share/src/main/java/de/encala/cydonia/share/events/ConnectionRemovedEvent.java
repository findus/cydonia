/**
 * 
 */
package de.encala.cydonia.share.events;

/**
 * @author encala
 * 
 */
public class ConnectionRemovedEvent extends AbstractEvent {

	private int clientid;

	/**
	 * @return the clientid
	 */
	public int getClientid() {
		return clientid;
	}

	/**
	 * @param clientid
	 *            the clientid to set
	 */
	public void setClientid(int clientid) {
		this.clientid = clientid;
	}
}
