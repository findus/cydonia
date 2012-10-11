/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Findus
 *
 */
public class Level2 implements Level {

	/* (non-Javadoc)
	 * @see de.findus.cydonia.level.Level#getScene(com.jme3.asset.AssetManager)
	 */
	@Override
	public Node getScene(AssetManager assetManager) {
		Node levelRoot = new Node("levelRoot");
		Spatial model = assetManager.loadModel("de/findus/cydonia/scenes/testworld.j3o");
		levelRoot.attachChild(model);
		return levelRoot;
	}

	/* (non-Javadoc)
	 * @see de.findus.cydonia.level.Level#getSpawnPoints()
	 */
	@Override
	public SpawnPoint[] getSpawnPoints() {
		SpawnPoint sp1 = new SpawnPoint();
		sp1.setPosition(new Vector3f(5, 1, 5));
		sp1.setTeam(1);
		
		SpawnPoint sp2 = new SpawnPoint();
		sp2.setPosition(new Vector3f(-5, 1, -5));
		sp2.setTeam(2);
		
		return new SpawnPoint[]{sp1, sp2};
	}

	/* (non-Javadoc)
	 * @see de.findus.cydonia.level.Level#getSpawnPoint(int)
	 */
	@Override
	public SpawnPoint getSpawnPoint(int team) {
		SpawnPoint sp = new SpawnPoint();
		if(team == 1) {
			sp.setPosition(new Vector3f(5, 1, 5));
			sp.setTeam(1);
		}else if(team == 2) {
			sp.setPosition(new Vector3f(-5, 1, -5));
			sp.setTeam(2);
		}
		return sp;
	}

}
