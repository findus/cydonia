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
public class TargetArea {

	private Spatial model;
	
	private RigidBodyControl control;
	
	public TargetArea(AssetManager assetManager) {
		Material mat_red =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_red.setColor("Color", ColorRGBA.Yellow);
		
		Box box = new Box(Vector3f.ZERO, 1.0f, 0.5f, 1.0f);
        model = new Geometry("TargetArea", box);
        model.setMaterial(mat_red);
        
        CollisionShape collisionShape = CollisionShapeFactory.createBoxShape(model);
        control = new RigidBodyControl(collisionShape, 0);
        model.addControl(control);
	}

	/**
	 * @return the model
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * @return the control
	 */
	public RigidBodyControl getControl() {
		return control;
	}
	
	
}
