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
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

import de.findus.cydonia.main.GameController;

/**
 * @author Findus
 *
 */
public class Flube {

	private long id;
	
	private int type;
	
	private Vector3f origin;
	
	private Spatial model;
	
	private RigidBodyControl control;
	
	public Flube(long id, Vector3f origin, int type, AssetManager assetManager) {
		this.id = id;
		this.type = type;
		this.origin = origin;
		
		Mesh mesh = new Box(Vector3f.ZERO, 0.5f, 0.5f, 0.5f);
	    
	    ColorRGBA color = null;
	    switch (this.type) {
	    case -4:
	    	color = new ColorRGBA(1.0f, 0.8f, 0.8f, 0.5f);
	    	break;
	    case -3:
	    	color = new ColorRGBA(0.8f, 0.8f, 1.0f, 0.5f);
	    	break;
	    case -2:
	    	color = ColorRGBA.DarkGray;
	    	break;
	    case -1:
			color = ColorRGBA.LightGray;
			break;
		case 0:
			color = new ColorRGBA(1.0f, 0.90f, 0.6f, 1.0f);
			break;
		case 1:
			color = ColorRGBA.Blue;
			break;
		case 2:
			color = ColorRGBA.Red;
			break;

		default:
			this.type = 0;
			color = ColorRGBA.White;
			break;
		}
		
	    Texture tex_box = assetManager.loadTexture(GameController.TEXTURES_PATH + "Box_white.png");
		Material mat_lit = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat_lit.setTexture("DiffuseMap", tex_box);
	    mat_lit.setBoolean("UseMaterialColors",true);    
	    mat_lit.setColor("Specular",ColorRGBA.White);
	    mat_lit.setColor("Diffuse", color);
	    mat_lit.setColor("Ambient", color);
	    mat_lit.setFloat("Shininess", 1f);
	    
//	    if(type == -3) {
	    	Texture tex_box_glow = assetManager.loadTexture(GameController.TEXTURES_PATH + "Box_glow2.png");
	    	mat_lit.setTexture("GlowMap", tex_box_glow);
//	    }
	    
        model = new Geometry("Flube_" + id, mesh);
        model.setMaterial(mat_lit);
		model.setUserData("id", id);
		model.setShadowMode(ShadowMode.CastAndReceive);
		model.setUserData("PlaceableSurface", (this.type >= -1));
		model.setUserData("TargetArea", (this.type <= -3));
		model.setUserData("Type", this.type);
		TangentBinormalGenerator.generate(model);
        
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	public int getTeam() {
		return type;
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
