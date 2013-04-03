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

	private boolean connectionAccepted;
	
	private String text;
	
	private String level;
	
	public ConnectionInitMessage() {
		setReliable(true);
	}

	/**
	 * @return the connectionAccepted
	 */
	public boolean isConnectionAccepted() {
		return connectionAccepted;
	}

	/**
	 * @param connectionAccepted the connectionAccepted to set
	 */
	public void setConnectionAccepted(boolean connectionAccepted) {
		this.connectionAccepted = connectionAccepted;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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
