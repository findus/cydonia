/**
 * 
 */
package de.findus.cydonia.events;


/**
 * @author Findus
 *
 */
public class ConnectionInitEvent extends AbstractEvent {

	private String message;
	
	private String level;
	
	public ConnectionInitEvent() {
		setNetworkEvent(false);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
}
