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
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * @author Findus
 *
 */
public class Moveable {

	private long id;
	
	private Vector3f origin;
	
	private Spatial model;
	
	private RigidBodyControl control;
	
	public Moveable(long id, Vector3f origin, AssetManager assetManager, String type) {
		this.id = id;
		
		this.origin = origin;
		
		Mesh mesh = null;
		ColorRGBA color = null;
	    if(type.equals("sphere")) {
	    	mesh = new Sphere(10, 10, 0.5f);
	    	color = ColorRGBA.Red;
	    }else if(type.equals("cube")) {
	    	mesh = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
	    	color = ColorRGBA.Blue;
	    }else {
	    	mesh = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
	    	color = ColorRGBA.Gray;
	    }
		
		
		Material mat_lit = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat_lit.setBoolean("UseMaterialColors",true);    
	    mat_lit.setColor("Specular",ColorRGBA.White);
	    mat_lit.setColor("Diffuse", color);
	    mat_lit.setColor("Ambient", color);
	    mat_lit.setFloat("Shininess", 5f);
		
	    
        model = new Geometry("Moveable_" + id, mesh);
        model.setMaterial(mat_lit);
		model.setUserData("id", id);
		model.setShadowMode(ShadowMode.CastAndReceive);
		model.setUserData("PlaceableSurface", true);
		TangentBinormalGenerator.generate(model);
        
        CollisionShape collisionShape = CollisionShapeFactory.createMeshShape(model);
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
	 * @return the origin
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Vector3f origin) {
		this.origin = origin;
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
