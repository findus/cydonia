/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class ChooseTeamEvent extends AbstractEvent {

	private int playerid;

	private int team;

	public ChooseTeamEvent() {
		super();
	}

	public ChooseTeamEvent(int playerid, int team, boolean forward) {
		this.playerid = playerid;
		this.team = team;
		this.network = forward;
	}

	/**
	 * @return the id
	 */
	public int getPlayerId() {
		return playerid;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}
}
