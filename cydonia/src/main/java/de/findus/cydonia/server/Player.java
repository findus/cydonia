/**
 * 
 */
package de.findus.cydonia.server;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;
import com.jme3.scene.plugins.blender.BlenderModelLoader;
import com.jme3.scene.shape.Cylinder;

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
	
	
	/**
	 * Constructs a new Player and inits its physics and model.
	 * @param id the id of this player. If not available set to -1 and reset later.
	 * @param assetManager the used instance of AssetManager 
	 */
	public Player(int id, AssetManager assetManager) {
		this.id = id;
		
		inputs = new PlayerInputState();
		
//		model = (Spatial) assetManager.loadAsset("Models/Ferrari/Car.scene");
		model = (Spatial) assetManager.loadAsset("Models/Sinbad/Sinbad.j3o");
        
//        Geometry geo = new Geometry("Player_" + id, new Cylinder(5, 5, 1f, 1.7f));
//        Material mat = new Material(assetManager, 
//        "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Red);
//        geo.setMaterial(mat);
//        model = geo;
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 1.6f, 1);
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
	
	

}
