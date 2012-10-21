/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class ConnectionInitMessage extends AbstractMessage {

	
	private boolean denied;
	
	private String reason;
	
	private String level;
	
	/**
	 * 
	 */
	public ConnectionInitMessage() {
		setReliable(true);
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

	/**
	 * @return the denied
	 */
	public boolean isDenied() {
		return denied;
	}

	/**
	 * @param denied the denied to set
	 */
	public void setDenied(boolean denied) {
		this.denied = denied;
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
