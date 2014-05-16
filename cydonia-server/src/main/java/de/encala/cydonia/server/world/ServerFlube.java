/**
 * 
 */
package de.encala.cydonia.server.world;

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

import de.encala.cydonia.server.GameServer;

/**
 * @author encala
 * 
 */
public class ServerFlube extends ServerWorldObject {

	private long id;

	private int type;

	private Vector3f origin;

	private Spatial model;

	private RigidBodyControl control;

	private Texture highlightGlow;

	private Material mat_box;

	public ServerFlube() {

	}

	public ServerFlube(long id, Vector3f origin, int type, AssetManager assetManager) {
		this.id = id;
		this.type = type;
		this.origin = origin;

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

//		Texture tex_box = assetManager.loadTexture(GameServer.TEXTURES_PATH
//				+ "Box_white.png");
//		mat_box = new Material(assetManager,
//				"Common/MatDefs/Light/Lighting.j3md");
//		mat_box.setTexture("DiffuseMap", tex_box);
//		mat_box.setBoolean("UseMaterialColors", true);
//		mat_box.setColor("Specular", ColorRGBA.White);
//		mat_box.setColor("Diffuse", color);
//		mat_box.setColor("Ambient", color);
//		mat_box.setFloat("Shininess", 1f);

		// if(type == -3) {
		// Texture tex_box_glow =
		// assetManager.loadTexture(GameController.TEXTURES_PATH +
		// "Box_glow2.png");
		// mat_box.setTexture("GlowMap", tex_box_glow);
		// }

		Mesh mesh = new Box(0.5f, 0.5f, 0.5f);
		model = new Geometry("Flube_" + id, mesh);
//		model.setMaterial(mat_box);
		model.setUserData("id", id);
		model.setShadowMode(ShadowMode.CastAndReceive);
		model.setUserData("PlaceableSurface", (this.type >= -1));
		model.setUserData("TargetArea", (this.type <= -3));
		model.setUserData("Type", this.type);
		TangentBinormalGenerator.generate(model);

//		highlightGlow = assetManager.loadTexture(GameServer.TEXTURES_PATH
//				+ "Box_glow2.png");

		CollisionShape collisionShape = CollisionShapeFactory
				.createBoxShape(model);
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
	 * @param id
	 *            the id to set
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

	/**
	 * @return the origin
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(Vector3f origin) {
		this.origin = origin;
	}

	/**
	 * @return the geom
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * @param geom
	 *            the geom to set
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
	 * @param control
	 *            the control to set
	 */
	public void setControl(RigidBodyControl control) {
		this.control = control;
	}

	public void setHighlighted(boolean highlighted) {
//		if (highlighted) {
//			mat_box.setTexture("GlowMap", highlightGlow);
//		} else {
//			mat_box.setTexture("GlowMap", null);
//		}
	}
}
