/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.network.serializing.Serializable;


/**
 * @author Findus
 *
 */
@Serializable
public class PlayerJoinEvent extends AbstractEvent {

	private int playerid;
	
	private String name;
	
	public PlayerJoinEvent() {
		super();
	}
	
	public PlayerJoinEvent(int playerid, String playername, boolean forward) {
		this.playerid = playerid;
		this.name = playername;
		this.network = forward;
	}

	/**
	 * @return the id
	 */
	public int getPlayerId() {
		return playerid;
	}

	/**
	 * @return the name
	 */
	public String getPlayername() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setPlayername(String playername) {
		this.name = playername;
	}
}
