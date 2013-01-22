/**
 * 
 */
package de.findus.cydonia.player;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Findus
 *
 */
public class PlayerController {
	
	private AssetManager assetManager;
    
    private ConcurrentHashMap<Integer, Player> players;

	public PlayerController(AssetManager assetManager) {
		this.assetManager = assetManager;
        
        players = new ConcurrentHashMap<Integer, Player>();
	}
	
	public Player createNew(int id) {
		Player p = new Player(id);
		players.put(p.getId(), p);
		
		return p;
	}
	
	public Player getPlayer(int id) {
		return players.get(id);
	}
	
	public Collection<Player> getAllPlayers() {
		return players.values();
	}
	
	public void removePlayer(int id) {
		players.remove(id);
	}
	
	public void removeAllPlayers() {
		players.clear();
	}
	
	public void updateModel(Player p) {
		if(p == null) return;
		
		Spatial model;
		if(p.getTeam() == 1) {
			model = (Node) assetManager.loadModel("de/findus/cydonia/models/blue/Sinbad.mesh.xml");
		}else if(p.getTeam() == 2) {
			model = (Node) assetManager.loadModel("de/findus/cydonia/models/red/Sinbad.mesh.xml");
		}else {
			model = (Node) assetManager.loadModel("de/findus/cydonia/models/green/Sinbad.mesh.xml");
		}
		model.setName("player" + p.getId());
		model.setLocalScale(0.2f);
		model.addControl(p.getControl());
		model.setShadowMode(ShadowMode.Cast);
		
		p.setModel(model);
	}
	
	public void setTeam(Player p, int team) {
		if(p == null) return;
		
		p.setTeam(team);
		updateModel(p);
	}

	public int getPlayerCount() {
		return players.size();
	}
}
