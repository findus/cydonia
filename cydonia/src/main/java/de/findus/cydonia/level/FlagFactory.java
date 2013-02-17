/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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
		ColorRGBA colorbase = null;
		
		if(team == 1) {
			color = ColorRGBA.Blue;
			colorbase = new ColorRGBA(0, 0, 1, 0.5f);
		}else if (team == 2) {
			color = ColorRGBA.Red;
			colorbase = new ColorRGBA(1, 0, 0, 0.5f);
		}
		
		// flag model
		Material mat_lit = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat_lit.setBoolean("UseMaterialColors",true);    
	    mat_lit.setColor("Specular",ColorRGBA.White);
	    mat_lit.setColor("Diffuse", color);
	    mat_lit.setColor("Ambient", color);
	    mat_lit.setFloat("Shininess", 2f);
	    
	    Mesh mesh = new Box(0.1f, 0.1f, 0.1f);
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
		mat_base.setColor("Diffuse", colorbase);
		mat_base.setColor("Ambient", colorbase);
		mat_base.setFloat("Shininess", 1f);
		mat_base.setBoolean("UseAlpha",true);
		mat_base.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	    
		Mesh meshBase = new Box(0.5f, 0.5f, 0.5f);
        Geometry modelBase = new Geometry("Flag_" + id, meshBase);
        modelBase.setUserData("id", id);
        modelBase.setUserData("FlagBase", true);
        modelBase.setUserData("team", team);
        modelBase.setMaterial(mat_base);
        modelBase.setShadowMode(ShadowMode.CastAndReceive);
        modelBase.setQueueBucket(Bucket.Transparent);
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
		
		nodeBase.setLocalTranslation(origin);
		
		f.setBaseControl(baseControl);
		f.setBaseModel(nodeBase);
		
		return f;
	}

}
