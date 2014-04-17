/**
 * 
 */
package de.encala.cydonia.messages;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import de.encala.cydonia.player.Equipment;
import de.encala.cydonia.player.Player;
import de.encala.cydonia.player.PlayerInputState;

/**
 * @author encala
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

	private PlayerInputState inputs;

	private Vector3f orientation;
	private Vector3f location;

	public PlayerInfo() {

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
		for (Equipment e : p.getEquips()) {
			eis.add(e.getInfo());
		}
		this.equipInfos = eis;
		this.inputs = p.getInputState();

		if (p.getControl() != null) {
			this.orientation = p.getControl().getViewDirection();
			this.location = p.getControl().getPhysicsLocation();
		}
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
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param team
	 *            the team to set
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
	 * @param alive
	 *            the alive to set
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
	 * @param healthpoints
	 *            the healthpoints to set
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
	 * @param scores
	 *            the scores to set
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
	 * @param currEquip
	 *            the currEquip to set
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
	 * @param equipInfo
	 *            the equipInfo to set
	 */
	public void setEquipInfos(Collection<EquipmentInfo> equipInfos) {
		this.equipInfos = equipInfos;
	}

	/**
	 * @return the orientation
	 */
	public Vector3f getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 *            the orientation to set
	 */
	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return the location
	 */
	public Vector3f getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Vector3f location) {
		this.location = location;
	}

	/**
	 * @return the inputs
	 */
	public PlayerInputState getInputs() {
		return inputs;
	}

	/**
	 * @param inputs
	 *            the inputs to set
	 */
	public void setInputs(PlayerInputState inputs) {
		this.inputs = inputs;
	}
}
