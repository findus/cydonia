/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.game.player.InputCommand;

/**
 * @author encala
 * 
 */
@Serializable
public class InputEvent extends AbstractEvent {

	private int playerid;

	private InputCommand command;

	private boolean value;

	public InputEvent() {
		super();
	}

	public InputEvent(int playerid, InputCommand command, boolean value,
			boolean forward) {
		this.playerid = playerid;
		this.command = command;
		this.value = value;
		this.network = forward;
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
