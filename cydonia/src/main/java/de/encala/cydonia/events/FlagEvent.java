/**
 * 
 */
package de.encala.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class FlagEvent extends AbstractEvent {

	public static final int LOOSE = 1;
	public static final int TAKE = 2;
	public static final int RETURN = 3;
	public static final int SCORE = 4;

	private int type;

	private int playerid;

	private int flagid;

	/**
	 * 
	 */
	public FlagEvent() {
	}

	/**
	 * @param net
	 */
	public FlagEvent(int type, int playerid, int flagid, boolean net) {
		super(net);
		this.type = type;
		this.setPlayerid(playerid);
		this.setFlagid(flagid);
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
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
	 * @return the flagid
	 */
	public int getFlagid() {
		return flagid;
	}

	/**
	 * @param flagid
	 *            the flagid to set
	 */
	public void setFlagid(int flagid) {
		this.flagid = flagid;
	}

}
