/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.math.Vector3f;

/**
 * @author Findus
 *
 */
public class PlayerModel {

	private int id; //model
	
	private String name; //model
	
	private int team; //model
	
	private boolean alive = false; //model

	private PlayerInputState inputs; //model
	
	private Vector3f exactLoc = new Vector3f(); //model
	
	private double healthpoints = 100; //model
	
	private long lastShot = 0; //model
	
	private int scores = 0; //model
	
	private Vector3f viewDir = Vector3f.UNIT_X; //model
	
	private long inventory = 0; //model
	
	/**
	 * Constructs a new Player.
	 * @param id the id of this player. If not available set to -1 and reset later.
	 */
	public PlayerModel(int id) {
		this.id = id;
		inputs = new PlayerInputState();
	}
	
	/**
	 * Returns the InputState oject.
	 * @return input state
	 */
	public PlayerInputState getInputState() {
		return this.inputs;
	}
	
	/**
	 * Sets the InputState object.
	 * @param pis input state
	 */
	public void setInputState(PlayerInputState pis) {
		this.inputs = pis;
	}
	
	/**
	 * Returns the id of this Player. The value -1 indicates the real id was not available at contruction time.
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this player. If not available set to -1 and reset later.
	 * @param id the id
	 */
	public void setId(int id) {
		this.id = id;
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

	public Vector3f getExactLoc() {
		return exactLoc;
	}

	public void setExactLoc(Vector3f smooth) {
		this.exactLoc = smooth;
	}

	public double getHealthpoints() {
		return healthpoints;
	}

	public void setHealthpoints(double healthpoints) {
		this.healthpoints = healthpoints;
	}
	
	public long getLastShot() {
		return this.lastShot;
	}
	
	public void setLastShot(long time) {
		this.lastShot = time;
	}

	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
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
	 * @return the viewDir
	 */
	public Vector3f getViewDir() {
		return viewDir.clone();
	}

	/**
	 * @param viewDir the viewDir to set
	 */
	public void setViewDir(Vector3f viewDir) {
		this.viewDir = viewDir.clone();
	}

	/**
	 * @return the inventory
	 */
	public long getInventory() {
		return inventory;
	}

	/**
	 * @param inventory the inventory to set
	 */
	public void setInventory(long inventory) {
		this.inventory = inventory;
	}
}
