/**
 * 
 */
package de.encala.cydonia.share.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class JoinMessage extends AbstractMessage {

	private int playerid;

	private String name;

	public JoinMessage() {
		super(true);
	}

	public JoinMessage(int playerid, String playername) {
		super(true);
		this.setPlayerid(playerid);
		this.setPlayername(playername);
	}

	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	/**
	 * @param playerid
	 *            the playerid to set
	 */
	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	/**
	 * @return the name
	 */
	public String getPlayername() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setPlayername(String name) {
		this.name = name;
	}
}
