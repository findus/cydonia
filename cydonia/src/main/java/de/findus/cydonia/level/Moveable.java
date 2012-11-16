/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * @author Findus
 *
 */
public class Moveable {

	private long id;
	
	private Spatial model;
	
	private RigidBodyControl control;
	
	public Moveable(long id, AssetManager assetManager) {
		this.id = id;
		
		Material mat_red =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_red.setColor("Color", ColorRGBA.Red);
		
		Box box = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
        model = new Geometry("Box", box);
        model.setMaterial(mat_red);
        model.setName("Moveable_" + id);
		model.setUserData("id", id);
        
        CollisionShape collisionShape = CollisionShapeFactory.createBoxShape(model);
        control = new RigidBodyControl(collisionShape, 0);
        model.addControl(control);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the model
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Spatial model) {
		this.model = model;
	}

	/**
	 * @return the control
	 */
	public RigidBodyControl getControl() {
		return control;
	}

	/**
	 * @param control the control to set
	 */
	public void setControl(RigidBodyControl control) {
		this.control = control;
	}
}
