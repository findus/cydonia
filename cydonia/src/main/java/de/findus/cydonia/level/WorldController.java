/**
 * 
 */
package de.findus.cydonia.level;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightList;
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
	
	protected Node worldNode = new Node("World");
	
	protected Node moveablesNode = new Node("Movables");
	
	protected ConcurrentHashMap<Long, Moveable> moveables;
	
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
		this.assetManager = assetManager;
		this.physicsSpace = physicsSpace;
		
		setUpAmbientLight();
		
		this.moveables = new ConcurrentHashMap<Long, Moveable>();
	}
	
	/**
	 * Loads the world.
	 * @param assetManager an instance of AssetManager
	 */
	public void loadWorld(String levelname) {
		Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false);
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
        worldNode.attachChild(scene);
    	physicsSpace.add(worldCollisionControll);
        
    	for (int j = 1; j < 10; j++) {
    		for (int i = 1; i <= 20; i++) {
    			String type = "cube";
    			if(i%3 == 0) {
    				type = "sphere";
    			}
    			Moveable m = new Moveable(i+(20*j), new Vector3f(2*i, 0.5f, 2*j), assetManager, type);
    			this.moveables.put(m.getId(), m);
    			m.getControl().setPhysicsLocation(m.getOrigin());
    			attachMoveable(m);
    		}
    	}

		TargetArea ta = new TargetArea(assetManager);
		ta.getControl().setPhysicsLocation(new Vector3f(0, 10, 0));
		worldNode.attachChild(ta.getModel());
		physicsSpace.addCollisionObject(ta.getControl());
		
		worldNode.attachChild(moveablesNode);
		
		rootNode.attachChild(worldNode);
	}
	
	public void resetWorld() {
		for (Moveable m : moveables.values()) {
			detachMoveable(m);
			m.getControl().setPhysicsLocation(m.getOrigin());
			attachMoveable(m);
		}
	}
	
	/**
	 * Attaches the objectes to the scenes root node.
	 * @param obj the object to attach
	 */
	public void attachObject(Spatial obj) {
		worldNode.attachChild(obj);
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
		worldNode.detachChild(obj);
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
	
	public void attachMoveable(Moveable moveable) {
		moveablesNode.attachChild(moveable.getModel());
		physicsSpace.addCollisionObject(moveable.getControl());
	}
	
	public void detachMoveable(Moveable moveable) {
		moveablesNode.detachChild(moveable.getModel());
		physicsSpace.removeCollisionObject(moveable.getControl());
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
		rootNode.setShadowMode(ShadowMode.Off);
		
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);
        
        DirectionalLight dl1 = new DirectionalLight();
        dl1.setColor(new ColorRGBA(1.0f, 0.9f, 0.3f, 1.0f).mult(0.7f));
        dl1.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(dl1);
    }
	
	public LightList getLights() {
		return rootNode.getLocalLightList();
	}

	public Level getLevel() {
		return level;
	}
	
	public Moveable getMoveable(long id) {
		return moveables.get(id);
	}
	
	public Collection<Moveable> getAllMoveables() {
		return moveables.values();
	}
	
	public CollisionResult pickMovable(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		moveablesNode.collideWith(ray, results);
		return results.getClosestCollision();
	}
	
	public CollisionResult pickWorld(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		worldNode.collideWith(ray, results);
		return results.getClosestCollision();
	}
	
	public boolean isPlaceableSurface(Spatial obj) {
		if(obj.getUserData("PlaceableSurface") != null) {
			return ((Boolean)obj.getUserData("PlaceableSurface")).booleanValue();
		}
		return false;
	}

}
