/**
 * 
 */
package de.findus.cydonia.events;


/**
 * @author Findus
 *
 */
public class PlayerJoinEvent extends AbstractEvent {

	private int playerid;
	
	private String name;
	
	private int team;
	
	public PlayerJoinEvent() {
		setNetworkEvent(true);
	}

	/**
	 * @return the id
	 */
	public int getPlayerId() {
		return playerid;
	}

	/**
	 * @param id the id to set
	 */
	public void setPlayerId(int id) {
		this.playerid = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}
}
