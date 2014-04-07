/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.MarkEvent;
import de.findus.cydonia.events.SwapEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.WorldObject;
import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ServerSwapper extends Swapper {

	
	WorldObject[] markers;
	
	/**
	 * 
	 */
	public ServerSwapper() {
		super();
		markers = new WorldObject[2];
	}

	/**
	 * @param mainController
	 */
	public ServerSwapper(MainController mainController) {
		super(mainController);
		markers = new WorldObject[2];
	}


	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		CollisionResults results = getMainController().getWorldController().pickRootList(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.3f)), this.player.getViewDir());
		for(CollisionResult result : results) {
			if(result != null && result.getDistance() <= this.getRange()) {
				if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
					Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
					if(target == player) {
						continue;
					}
					mark(target);
					break;
				}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
					Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
					if(getMainController().getWorldController().isSwapableFlube(target)) {
						mark(target);
					}
					break;
				}
			}else {
				break;
			}
		}
	}
	
	@Override
	public void useSecondary(boolean activate) {
		if(!activate) return;

		swap();
	}
	
	private void swap() {
		if(this.markers[0] != null && this.markers[1] != null) {
			int pA=-1, pB=-1;
			long fA=0, fB=0;
			
			if(this.markers[0] instanceof Player) {
				pA = ((Player) this.markers[0]).getId();
			}else if(this.markers[0] instanceof Flube) {
				fA = ((Flube) this.markers[0]).getId();
			}
			
			if(this.markers[1] instanceof Player) {
				pB = ((Player) this.markers[1]).getId();
			}else if(this.markers[1] instanceof Flube) {
				fB = ((Flube) this.markers[1]).getId();
			}
			
			this.markers[0] = null;
			this.markers[1] = null;
			
			SwapEvent event = new SwapEvent(pA, pB, fA, fB, true);
			getMainController().getEventMachine().fireEvent(event);
		}
	}
	
	private void mark(WorldObject obj) {
		for(int i=0; i<markers.length; i++) {
			if(markers[i] == obj) {
				markers[i] = null;
				unmark(obj);
				return;
			}
		}
		
		for(int i=0; i<markers.length; i++) {
			if(markers[i] == null) {
				markers[i] = obj;
				int playerid = -1;
				long flubeid = 0;
				if(obj instanceof Player) {
					playerid = ((Player) obj).getId();
				}else if(obj instanceof Flube) {
					flubeid = ((Flube) obj).getId();
				}
				obj.addMark(this.player);
				MarkEvent ev = new MarkEvent(this.player.getId(), false, flubeid, playerid, true);
				getMainController().getEventMachine().fireEvent(ev);
				break;
			}
		}
	}

	private void unmark(WorldObject obj) {
		int playerid = -1;
		long flubeid = 0;
		if(obj instanceof Player) {
			playerid = ((Player) obj).getId();
		}else if(obj instanceof Flube) {
			flubeid = ((Flube) obj).getId();
		}
		obj.removeMark(this.player);
		MarkEvent ev = new MarkEvent(this.player.getId(), true, flubeid, playerid, true);
		getMainController().getEventMachine().fireEvent(ev);
	}
	
	public void resetMark(WorldObject obj) {
		for(int i=0; i<markers.length; i++) {
			if(markers[i] == obj) {
				markers[i] = null;
			}
		}
	}
	
	@Override
	public void reset() {
		for(int i=0; i<markers.length; i++) {
			if(markers[i] != null) {
				unmark(markers[i]);
				markers[i] = null;
			}
		}
		
		super.reset();
	}
}
