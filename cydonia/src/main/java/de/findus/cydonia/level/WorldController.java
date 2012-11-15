/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

import de.findus.cydonia.player.Player;

/**
 * This is the central controller for the games virtual world.
 * It loads the scene and administrates the root node.
 * @author Findus
 *
 */
public class WorldController {
	
	/**
	 * The AssetManager.
	 */
	protected AssetManager assetManager;
	
	protected PhysicsSpace physicsSpace;
	
	/**
	 * Root Node of the virtual world.
	 */
	protected Node rootNode = new Node("Root Node");
	
	protected Node moveables = new Node("Movables");
	
	/**
	 * Physics control object of the scene.
	 */
	private RigidBodyControl worldCollisionControll;
	
	/**
	 * The level currently loaded.
	 */
	private Level level;

	/**
	 * Constructs setting up ambient light.
	 */
	public WorldController(AssetManager assetManager, PhysicsSpace physicsSpace) {
		setUpAmbientLight();
		this.assetManager = assetManager;
		this.physicsSpace = physicsSpace;
	}
	
	/**
	 * Loads the world.
	 * @param assetManager an instance of AssetManager
	 */
	public void loadWorld(String levelname) {
		Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", true);
		rootNode.attachChild(sky);
		
		try {
			level = (Level) Class.forName(levelname).newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

        Spatial scene = null;
        scene = level.getScene(assetManager);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
        worldCollisionControll = new RigidBodyControl(sceneShape, 0);
        scene.addControl(worldCollisionControll);
        rootNode.attachChild(scene);
    	physicsSpace.add(worldCollisionControll);
        
        BoxBPO boxBPO = new BoxBPO(assetManager);
		Spatial box;
		for (int i = 1; i < 5; i++) {
			box = boxBPO.createBox("red", new Vector3f(2*i, 0.5f, 0), true);
			box.setName("Moveable_" + i);
			box.setUserData("id", new Long(i));
			attachMoveable(box);
		}
		
		rootNode.attachChild(moveables);
	}
	
	/**
	 * Attaches the objectes to the scenes root node.
	 * @param obj the object to attach
	 */
	public void attachObject(Spatial obj) {
		rootNode.attachChild(obj);
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.addCollisionObject(control);
		}
	}
	
	/**
	 * Detaches the object from the scenes root node.
	 * @param obj the object to remove
	 */
	public void detachObject(Spatial obj) {
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		rootNode.detachChild(obj);
	}
	
	public void attachPlayer(Player player) {
		rootNode.attachChild(player.getModel());
		CharacterControl control = player.getControl();
		if(control != null) {
			physicsSpace.addCollisionObject(control);
		}
	}
	
	public void detachPlayer(Player player) {
		CharacterControl control = player.getControl();
		if(control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		rootNode.detachChild(player.getModel());
	}
	
	public void attachMoveable(Spatial moveable) {
		moveables.attachChild(moveable);
		RigidBodyControl control = moveable.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.addCollisionObject(control);
		}
	}
	
	public void detachMoveable(Spatial moveable) {
		RigidBodyControl control = moveable.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		moveables.detachChild(moveable);
	}
	
	/**
	 * Updates the logical state of the scene.
	 * Only called by global physics update loop.
	 */
	public void updateLogicalState(float tpf) {
		rootNode.updateLogicalState(tpf);
	}
	
	/**
	 * Updates the geometric state of the scene.
	 * Only called by global physics update loop.
	 */
	public void updateGeometricState() {
		rootNode.updateGeometricState();
	}
	
	/**
	 * Returns the root node of the world.
	 * Called by <code>GameController</code> to attach the root node to the ViewPort.
	 * DO NOT USE OTHER THAN THAT.
	 * @return the root node 
	 */
	public Node getRootNode() {
		return this.rootNode;
	}
	
	private void setUpAmbientLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        
        rootNode.setShadowMode(ShadowMode.Off);
    }

	public Level getLevel() {
		return level;
	}
	
	public Spatial getMoveable(long id) {
		return moveables.getChild("Moveable_" + id);
	}
	
	public CollisionResult pickMovable(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		moveables.collideWith(ray, results);
		return results.getClosestCollision();
	}

}
