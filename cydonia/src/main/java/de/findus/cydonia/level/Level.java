/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 * Level is the base interface for all levels.
 * @author Findus
 */
public interface Level {
    
	/**
	 * Returns the root node of the levels scene graph.
	 * @param assetManager a instance of AssetManager
	 * @return root node of scene graph
	 */
    public Node getScene(AssetManager assetManager);
    
    /**
     * Returns all available spawnpoints in this level as an array.
     * 
     * @return array of spwanpoints
     */
    public SpawnPoint[] getSpawnPoints();
    
    /**
     * Returns the spawnpoint for the specified team in this level.
     * 
     * @return the spawnpoint
     */
    public SpawnPoint getSpawnPoint(int team);
}
