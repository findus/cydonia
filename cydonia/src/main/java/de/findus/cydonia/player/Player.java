/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * @author Findus
 *
 */
public class Player {
	
	public static float MAX_STEP_HEIGHT = 0.2f;
	
	private static Vector3f RELATIVE_EYE_POSITION = new Vector3f(0, 0.35f, 0);
	
	private int id;
	
	private String name;
	
	private int team;
	
	private boolean alive = false;

	private PlayerInputState inputs;
	
	private Vector3f exactLoc = new Vector3f();
	
	private CharacterControl control;
	
	private Spatial model;
	
	private double healthpoints = 100;
	
	private long lastShot = 0;
	
	private int kills = 0;
	
	private int deaths = 0;
	
	private Vector3f viewDir = Vector3f.UNIT_X;
	
	private long inventory = 0;
	
	/**
	 * Constructs a new Player and inits its physics and model.
	 * @param id the id of this player. If not available set to -1 and reset later.
	 * @param assetManager the used instance of AssetManager 
	 */
	public Player(int id, AssetManager assetManager) {
		this.id = id;
		
		inputs = new PlayerInputState();
		
		model = assetManager.loadModel("Models/Sinbad/Sinbad.j3o");
		model.setName("player" + id);
		model.setLocalScale(0.2f);
		
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 0.8f);
        control = new CharacterControl(capsuleShape, MAX_STEP_HEIGHT);
        control.setJumpSpeed(10);
        control.setFallSpeed(20);
        control.setGravity(20);
        
        model.addControl(control);
	}
	
	public void handleInput(InputCommand command, boolean value) {
		switch (command) {
		case MOVEFRONT:
			inputs.setForward(value);
			break;
		case MOVEBACK:
			inputs.setBack(value);
			break;
		case STRAFELEFT:
			inputs.setLeft(value);
			break;
		case STRAFERIGHT:
			inputs.setRight(value);
			break;

		default:
			break;
		}
	}
	
	/**
	 * Returns the model for visualization of this player.
	 * @return model of this player
	 */
	public Spatial getModel() {
		return model;
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
	 * Returns the physics control object.
	 * @return physics control
	 */
	public CharacterControl getControl() {
		return control;
	}
	
	public Vector3f getEyePosition() {
		return control.getPhysicsLocation().add(RELATIVE_EYE_POSITION);
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

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
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
		control.setViewDirection(viewDir.clone().setY(0).normalizeLocal());
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
