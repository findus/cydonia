/**
 * 
 */
package de.encala.cydonia.game.level;

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
 * @author encala
 * 
 */
public class TargetArea {

	private int id;

	private int team;

	private Spatial model;

	private Vector3f position;

	private int width;
	private int height;
	private int depth;

	private RigidBodyControl control;

	public TargetArea(int id, int team, Vector3f position, int width,
			int height, int depth, AssetManager assetManager) {
		this.id = id;
		this.setTeam(team);
		this.position = position;
		this.width = width;
		this.height = height;
		this.depth = depth;

		Material mat_red = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat_red.setColor("Color", ColorRGBA.Yellow);

		Box box = new Box(width / 2, height / 2, depth / 2);
		model = new Geometry("TargetArea", box);
		model.setMaterial(mat_red);
		model.setUserData("PlaceableSurface", true);

		CollisionShape collisionShape = CollisionShapeFactory
				.createBoxShape(model);
		control = new RigidBodyControl(collisionShape, 0);
		model.addControl(control);

		control.setPhysicsLocation(position);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}

}
