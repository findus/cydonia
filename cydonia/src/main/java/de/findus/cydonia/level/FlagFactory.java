/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;

/**
 * @author Findus
 *
 */
public class FlagFactory {

	private static FlagFactory instance;
	
	public static void init(AssetManager assetManager) {
		instance = new FlagFactory(assetManager);
	}
	
	public static FlagFactory getInstance() {
		return instance;
	}
	
	private AssetManager assetManager;
	
	/**
	 * 
	 */
	private FlagFactory(AssetManager assetManager) {
		this.assetManager = assetManager;
	}
	
	public Flag createFlag(int id, Vector3f origin, int team) {
		Flag f = new Flag();
		f.setId(id);
		f.setOrigin(origin);
		f.setTeam(team);

		ColorRGBA color = null;
		
		if(team == 1) {
			color = ColorRGBA.Blue;
		}else if (team == 2) {
			color = ColorRGBA.Red;
		}
		
		// flag model
		Material mat_lit = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat_lit.setBoolean("UseMaterialColors",true);    
	    mat_lit.setColor("Specular",ColorRGBA.White);
	    mat_lit.setColor("Diffuse", color);
	    mat_lit.setColor("Ambient", color);
	    mat_lit.setFloat("Shininess", 1f);
	    
	    Mesh mesh = new Box(Vector3f.ZERO, 1f, 1f, 1f);
        Geometry model = new Geometry("Flag_" + id, mesh);
        model.setMaterial(mat_lit);
		model.setUserData("id", id);
		model.setShadowMode(ShadowMode.CastAndReceive);
		TangentBinormalGenerator.generate(model);
        
		f.setModel(model);
		
		
		// base model
		Material mat_base = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat_base.setBoolean("UseMaterialColors",true);    
		mat_base.setColor("Specular",ColorRGBA.White);
		mat_base.setColor("Diffuse", ColorRGBA.White);
		mat_base.setColor("Ambient", ColorRGBA.White);
		mat_base.setFloat("Shininess", 1f);
	    
		Mesh meshBase = new Box(new Vector3f(0, 1, 0), 1f, 1f, 1f);
        Geometry modelBase = new Geometry("Flag_" + id, meshBase);
        modelBase.setUserData("id", id);
        modelBase.setUserData("FlagBase", true);
        modelBase.setUserData("team", team);
        modelBase.setMaterial(mat_base);
        modelBase.setShadowMode(ShadowMode.CastAndReceive);
		TangentBinormalGenerator.generate(modelBase);
		
		Node nodeBase = new Node("Flag_" + id);
		nodeBase.setUserData("id", id);
		nodeBase.setUserData("FlagBase", true);
		modelBase.setUserData("team", team);
		nodeBase.attachChild(modelBase);
		
		CollisionShape collisionShape = CollisionShapeFactory.createBoxShape(modelBase);
		GhostControl baseControl = new GhostControl(collisionShape);
		baseControl.setCollisionGroup(GhostControl.COLLISION_GROUP_02);
		baseControl.setCollideWithGroups(GhostControl.COLLISION_GROUP_02);
		modelBase.addControl(baseControl);
		System.out.println(baseControl.getUserObject());
		
		modelBase.setLocalTranslation(origin);
		
		f.setBaseControl(baseControl);
		f.setBaseModel(nodeBase);
		
		return f;
	}

}
