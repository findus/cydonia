/**
 * 
 */
package de.findus.cydonia.player;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

import de.findus.cydonia.equipment.EquipmentModel;
import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public abstract class PlayerController {
	
    
    protected MainController mainController;

	protected ConcurrentHashMap<Integer, Player> players;

	public PlayerController(MainController mainController) {
		this.mainController = mainController;
        
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
		
		Node model;
		if(p.getTeam() == 1) {
			model = (Node) mainController.getAssetManager().loadModel("de/findus/cydonia/models/blue/Sinbad.mesh.xml");
		}else if(p.getTeam() == 2) {
			model = (Node) mainController.getAssetManager().loadModel("de/findus/cydonia/models/red/Sinbad.mesh.xml");
		}else {
			model = (Node) mainController.getAssetManager().loadModel("de/findus/cydonia/models/green/Sinbad.mesh.xml");
		}
		model.setName("player" + p.getId());
		model.setLocalScale(0.2f);
		model.setShadowMode(ShadowMode.Cast);
		model.setQueueBucket(Bucket.Transparent);
		
		p.setModel(model);
	}
	
	public void setHealthpoints(Player p, double health) {
		if(p == null) return;
		p.setHealthpoints(health);
	}
	
	public void reset(Player p) {
		p.setScores(0);
		
		for(EquipmentModel equip : p.getEquips()) {
			equip.getController(getType(), mainController).reset(equip);
		}
	}
	
	public void setTeam(Player p, int team) {
		if(p == null) return;
		
		p.setTeam(team);
		updateModel(p);
	}

	public int getPlayerCount() {
		return players.size();
	}
	
	public void switchEquipment(Player p, boolean up) {
		this.setCurrEquip(p, p.currEquip + (up?1:-1));
	}
	
	public void setCurrEquip(Player p, int index) {
		if(p.equips.size() > 0) {
			p.getCurrentEquipment().getController(getType(), mainController).setActive(p.getCurrentEquipment(), false);
			if(p.getCurrentEquipment().getGeometry() != null) {
				p.node.detachChild(p.getCurrentEquipment().getGeometry());
			}
			p.getCurrentEquipment().getController(getType(), mainController).reset(p.getCurrentEquipment());
			p.currEquip = index % p.equips.size();
			if(p.getCurrentEquipment().getGeometry() != null) {
				p.node.attachChild(p.getCurrentEquipment().getGeometry());
			}
			p.getCurrentEquipment().getController(getType(), mainController).setActive(p.getCurrentEquipment(), true);
		}
	}
	
	public void handleInput(Player p, InputCommand command, boolean value) {
		switch (command) {
		case MOVEFRONT:
			p.inputs.setForward(value);
			break;
		case MOVEBACK:
			p.inputs.setBack(value);
			break;
		case STRAFELEFT:
			p.inputs.setLeft(value);
			break;
		case STRAFERIGHT:
			p.inputs.setRight(value);
			break;
		case JUMP:
			if(value) {
				p.jump();
			}
			break;
		case USEPRIMARY:
			p.getCurrentEquipment().getController(getType(), mainController).usePrimary(p.getCurrentEquipment(), value);
			break;
		case USESECONDARY:
			p.getCurrentEquipment().getController(getType(), mainController).useSecondary(p.getCurrentEquipment(), value);
			break;
		case SWITCHEQUIP:
			switchEquipment(p, value);
			break;
		default:
			break;
		}
	}
	
	protected abstract String getType();
}
