/**
 * 
 */
package de.encala.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.messages.SpawnPointInfo;

/**
 * @author encala
 * 
 */
public class SpawnPoint {

	private int id;

	private Vector3f position;

	private int team;

	private Node node;

	public SpawnPoint() {

	}

	public SpawnPoint(int id, Vector3f position, int team,
			AssetManager assetManager) {
		this.id = id;
		this.position = position;
		this.team = team;

		this.node = new Node("SpawnPoint_" + id);

		Mesh mesh = new Box(0.25f, 1f, 0.25f);

		ColorRGBA color = null;
		switch (this.team) {
		case 1:
			color = ColorRGBA.Blue.mult(1.5f);
			break;
		case 2:
			color = ColorRGBA.Red.mult(1.5f);
			break;
		}

		Material mat_lit = new Material(assetManager,
				"Common/MatDefs/Light/Lighting.j3md");
		mat_lit.setBoolean("UseMaterialColors", true);
		mat_lit.setColor("Specular", ColorRGBA.White);
		mat_lit.setColor("Diffuse", color);
		mat_lit.setColor("Ambient", color);
		mat_lit.setFloat("Shininess", 1f);

		Geometry model = new Geometry("SpawnPoint_" + id, mesh);
		model.setMaterial(mat_lit);
		model.setUserData("id", id);
		model.setShadowMode(ShadowMode.Off);
		TangentBinormalGenerator.generate(model);

		node.attachChild(model);
		node.setCullHint(CullHint.Always);
		node.setLocalTranslation(position);
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

	public SpawnPointInfo getInfo() {
		return new SpawnPointInfo(this);
	}

	public Node getNode() {
		return node;
	}

}
