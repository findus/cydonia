/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Player;


/**
 * @author Findus
 *
 */
@Serializable
public class PlayerInfo {

	private int playerid;
	
	private String name;
	
	private int team;
	
	private boolean alive;
	
	private double healthpoints;
	
	private int kills;
	
	private int deaths;
	
	public PlayerInfo() {
		
	}

	public PlayerInfo(int playerid, String name, int team, boolean alive, double healthpoints, int kills, int deaths) {
		this.playerid = playerid;
		this.name = name;
		this.team = team;
		this.alive = alive;
		this.healthpoints = healthpoints;
		this.kills = kills;
		this.deaths = deaths;
	}
	
	public PlayerInfo(Player p) {
		this.playerid = p.getId();
		this.name = p.getName();
		this.team = p.getTeam();
		this.alive = p.isAlive();
		this.healthpoints = p.getHealthpoints();
		this.kills = p.getKills();
		this.deaths = p.getDeaths();
	}
	
	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	/**
	 * @param playerid the playerid to set
	 */
	public void setPlayerid(int playerid) {
		this.playerid = playerid;
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

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * @return the healthpoints
	 */
	public double getHealthpoints() {
		return healthpoints;
	}

	/**
	 * @param healthpoints the healthpoints to set
	 */
	public void setHealthpoints(double healthpoints) {
		this.healthpoints = healthpoints;
	}

	/**
	 * @return the kills
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * @param kills the kills to set
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * @return the deaths
	 */
	public int getDeaths() {
		return deaths;
	}

	/**
	 * @param deaths the deaths to set
	 */
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
}
