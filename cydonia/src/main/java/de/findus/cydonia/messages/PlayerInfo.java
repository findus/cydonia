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
	
	private int scores;
	
	public PlayerInfo() {
		
	}

	public PlayerInfo(int playerid, String name, int team, boolean alive, double healthpoints, int kills, int deaths) {
		this.playerid = playerid;
		this.name = name;
		this.team = team;
		this.alive = alive;
		this.healthpoints = healthpoints;
		this.scores = kills;
	}
	
	public PlayerInfo(Player p) {
		this.playerid = p.getId();
		this.name = p.getName();
		this.team = p.getTeam();
		this.alive = p.isAlive();
		this.healthpoints = p.getHealthpoints();
		this.scores = p.getScores();
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
	 * @return the scores
	 */
	public int getScores() {
		return scores;
	}

	/**
	 * @param scores the scores to set
	 */
	public void setScores(int scores) {
		this.scores = scores;
	}
}
