/**
 * 
 */
package de.encala.cydonia.bullet;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

import de.encala.cydonia.game.GameController;

/**
 * @author encala
 * 
 */
public class Bullet {

	private static long counter = 0;

	private static AssetManager assetManager;

	private static Material material;

	public static void setAssetManager(AssetManager am) {
		assetManager = am;
	}

	public static void preloadTextures() {
		material = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		Texture tex2 = assetManager.loadTexture(GameController.TEXTURES_PATH
				+ "felsen1.jpg");
		material.setTexture("ColorMap", tex2);
	}

	public static Bullet createBullet(int playerid) {
		return new Bullet(++counter, playerid);
	}

	private long id;

	private int playerid;

	private Vector3f exactLoc = new Vector3f();

	private RigidBodyControl control;

	private Spatial model;

	/**
	 * Constructs a new Player and inits its physics and model.
	 */
	public Bullet(long id, int playerid) {
		this.id = id;
		this.playerid = playerid;

		Sphere sphere = new Sphere(10, 10, 0.1f);
		model = new Geometry("bullet", sphere);
		model.setName("bullet" + id);
		model.setUserData("Sticky", Boolean.TRUE);
		model.setMaterial(material);
		control = new RigidBodyControl(2f);
		model.addControl(control);
		control.setUserObject(model);
	}

	/**
	 * Returns the model for visualization of this bullet.
	 * 
	 * @return model of this player
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * Returns the physics control object.
	 * 
	 * @return physics control
	 */
	public RigidBodyControl getControl() {
		return control;
	}

	/**
	 * Returns the id of this bullet. The value -1 indicates the real id was not
	 * available at contruction time.
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id of this bullet. If not available set to -1 and reset later.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public Vector3f getExactLoc() {
		return exactLoc;
	}

	public void setExactLoc(Vector3f loc) {
		this.exactLoc = loc;
	}

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

}
