/**
 * 
 */
package de.findus.cydonia.messages;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.player.Equipment;
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
	
	private int currEquip;

	private Collection<EquipmentInfo> equipInfos;
	
	public PlayerInfo() {
		
	}

	public PlayerInfo(int playerid, String name, int team, boolean alive, double healthpoints, int kills, int deaths, int currEquip, Collection<EquipmentInfo> equipInfos) {
		this.playerid = playerid;
		this.name = name;
		this.team = team;
		this.alive = alive;
		this.healthpoints = healthpoints;
		this.scores = kills;
		this.setCurrEquip(currEquip);
		this.equipInfos = equipInfos;
	}
	
	public PlayerInfo(Player p) {
		this.playerid = p.getId();
		this.name = p.getName();
		this.team = p.getTeam();
		this.alive = p.isAlive();
		this.healthpoints = p.getHealthpoints();
		this.scores = p.getScores();
		this.setCurrEquip(p.getCurrEquipIndex());
		List<EquipmentInfo> eis = new LinkedList<EquipmentInfo>();
		for(Equipment e : p.getEquips()) {
			eis.add(e.getInfo());
		}
		this.equipInfos = eis;
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

	/**
	 * @return the currEquip
	 */
	public int getCurrEquip() {
		return currEquip;
	}

	/**
	 * @param currEquip the currEquip to set
	 */
	public void setCurrEquip(int currEquip) {
		this.currEquip = currEquip;
	}

	/**
	 * @return the equipInfo
	 */
	public Collection<EquipmentInfo> getEquipInfos() {
		return equipInfos;
	}

	/**
	 * @param equipInfo the equipInfo to set
	 */
	public void setEquipInfos(Collection<EquipmentInfo> equipInfos) {
		this.equipInfos = equipInfos;
	}
}
