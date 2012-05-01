/**
 * 
 */
package de.findus.cydonia.server;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import de.findus.cydonia.main.GameController;

/**
 * @author Findus
 *
 */
public class Player {
	
	private int id;

	private PlayerInputState inputs;
	
	private Vector3f exactLoc = new Vector3f();
	
	private CharacterControl control;
	
	private Spatial model;
	
	private double healthpoints = 100;
	
	
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
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.6f, 1);
        control = new CharacterControl(capsuleShape, GameController.MAX_STEP_HEIGHT);
        control.setJumpSpeed(10);
        control.setFallSpeed(30);
        control.setGravity(30);
        
        model.addControl(control);
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

}
