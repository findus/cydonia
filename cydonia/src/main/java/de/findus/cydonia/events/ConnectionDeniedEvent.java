/**
 * 
 */
package de.findus.cydonia.events;


/**
 * @author Findus
 *
 */
public class ConnectionDeniedEvent extends AbstractEvent {

	private String reason;
	
	public ConnectionDeniedEvent() {
		
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
}
