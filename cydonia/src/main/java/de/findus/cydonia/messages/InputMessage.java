/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.InputCommand;

/**
 * @author Findus
 *
 */
@Serializable
public class InputMessage extends AbstractMessage {

	private int playerid;
	
	private InputCommand command;
	
	private boolean value;

	public InputMessage() {
		super(true);
	}

	public InputMessage(int playerid, InputCommand command, boolean value) {
		super(true);
		this.playerid = playerid;
		this.command = command;
		this.value = value;
	}
	
	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}
	

	/**
	 * @return the command
	 */
	public InputCommand getCommand() {
		return command;
	}
	

	/**
	 * @return the value
	 */
	public boolean isValue() {
		return value;
	}
}
