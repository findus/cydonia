/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;
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

		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.3f)), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange()) {
			if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
				Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				mark(target);
			}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
				Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
				mark(target);
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
	
	private void mark(WorldObject s) {
		for(int i=0; i<markers.length; i++) {
			if(markers[i] == s) {
				markers[i] = null;
				unmark(s);
				return;
			}
		}
		
		for(int i=0; i<markers.length; i++) {
			if(markers[i] == null) {
				markers[i] = s;
				int playerid = -1;
				long flubeid = 0;
				if(s instanceof Player) {
					playerid = ((Player) s).getId();
				}else if(s instanceof Flube) {
					flubeid = ((Flube) s).getId();
				}
				MarkEvent ev = new MarkEvent(this.player.getId(), false, flubeid, playerid, true);
				getMainController().getEventMachine().fireEvent(ev);
				break;
			}
		}
	}

	private void unmark(WorldObject marker) {
		int playerid = -1;
		long flubeid = 0;
		if(marker instanceof Player) {
			playerid = ((Player) marker).getId();
		}else if(marker instanceof Flube) {
			flubeid = ((Flube) marker).getId();
		}
		MarkEvent ev = new MarkEvent(this.player.getId(), true, flubeid, playerid, true);
		getMainController().getEventMachine().fireEvent(ev);
	}
	
	@Override
	public void reset() {
		if(markerA != null) unmark(markerA);
		if(markerB != null) unmark(markerB);
		
		markerA = null;
		markerB = null;
		
		super.reset();
	}
}
