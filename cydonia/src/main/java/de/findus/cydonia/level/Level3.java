/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import de.findus.cydonia.main.GameController;

/**
 * @author Findus
 *
 */
public class Level3 implements Level {

	@Override
	public Node getScene(AssetManager assetManager) {
		Node levelRoot = new Node("levelRoot");
		
		Material mat_pflaster = new Material(assetManager, 
				"Common/MatDefs/Misc/Unshaded.j3md");
		Texture tex_pflaster = assetManager.loadTexture(GameController.TEXTURES_PATH + "pflaster1.jpg");
		mat_pflaster.setTexture("ColorMap", tex_pflaster);

		Quad quad1 = new Quad(100, 100);
		Geometry floor1 = new Geometry("Floor", quad1);
		floor1.setMaterial(mat_pflaster);
		floor1.rotate((float) (-Math.PI/2), 0, 0);
		floor1.move(-50, 0, 50);
		levelRoot.attachChild(floor1);

		return levelRoot;
	}

	@Override
	public SpawnPoint[] getSpawnPoints() {
		SpawnPoint sp1 = new SpawnPoint();
		sp1.setPosition(new Vector3f(10, 1, 10));
		sp1.setTeam(1);
		
		SpawnPoint sp2 = new SpawnPoint();
		sp2.setPosition(new Vector3f(-10, 1, -10));
		sp2.setTeam(2);
		
		return new SpawnPoint[]{sp1, sp2};
	}

	@Override
	public SpawnPoint getSpawnPoint(int team) {
		SpawnPoint sp = new SpawnPoint();
		if(team == 1) {
			sp.setPosition(new Vector3f(10, 1, 10));
			sp.setTeam(1);
		}else if(team == 2) {
			sp.setPosition(new Vector3f(-10, 1, -10));
			sp.setTeam(2);
		}
		return sp;
	}

}
