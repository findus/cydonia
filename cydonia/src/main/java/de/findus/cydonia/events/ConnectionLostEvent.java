/**
 * 
 */
package de.findus.cydonia.events;

/**
 * @author Findus
 *
 */
public class ConnectionLostEvent extends AbstractEvent {

	private String reason;
	
	/**
	 * 
	 */
	public ConnectionLostEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param net
	 */
	public ConnectionLostEvent(String reason, boolean net) {
		super(net);
		this.reason = reason;
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
