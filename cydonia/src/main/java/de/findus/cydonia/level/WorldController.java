/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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
	
	/**
	 * Root Node of the virtual world.
	 */
	protected Node rootNode = new Node("Root Node");
	
	/**
	 * Physics control object of the scene.
	 */
	private RigidBodyControl worldCollisionControll;

	/**
	 * Constructs setting up ambient light.
	 */
	public WorldController() {
		setUpAmbientLight();
	}
	
	/**
	 * Loads the world.
	 * @param assetManager an instance of AssetManager
	 */
	public void loadWorld() {
		Level level = new Level1();
        Spatial scene = null;
        scene = level.getScene(assetManager);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
        worldCollisionControll = new RigidBodyControl(sceneShape, 0);
        scene.addControl(worldCollisionControll);
        rootNode.attachChild(scene);
	}
	
	/**
	 * Attaches the objectes to the scenes root node.
	 * @param obj the object to attach
	 */
	public void attachObject(Spatial obj) {
		rootNode.attachChild(obj);
	}
	
	/**
	 * Detaches the object from the scenes root node.
	 * @param obj the object to remove
	 */
	public void detachObject(Spatial obj) {
		rootNode.detachChild(obj);
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
	 * Returns the physics control object of the scene.
	 * Called by <code>GameController</code> to attach physics controller to the PhysicsSpace.
	 * DO NOT USE OTHER THAN THAT.
	 * @return the physics controller
	 */
	public RigidBodyControl getWorldCollisionControll() {
		return this.worldCollisionControll;
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
    }

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

}
