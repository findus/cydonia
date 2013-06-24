/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;

import de.findus.cydonia.events.SwapEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ServerSwapper extends Swapper {

	/**
	 * 
	 */
	public ServerSwapper() {
		super();
	}

	/**
	 * @param mainController
	 */
	public ServerSwapper(MainController mainController) {
		super(mainController);
	}


	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.3f)), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange()) {
			if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
				Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				this.markerA = target;
			}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
				Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
				this.markerA = target;
			}
			
			swap();
		}
	}
	
	@Override
	public void useSecondary(boolean activate) {
		if(!activate) return;

		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.4f)), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange()) {
			if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
				Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				this.markerB = target;
			}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
				Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
				this.markerB = target;
			}
			
			swap();
		}
	}
	
	private void swap() {
		if(this.markerA != null && this.markerB != null) {
			int pA=0, pB=0;
			long fA=0, fB=0;
			
			if(markerA instanceof Player) {
				pA = ((Player) markerA).getId();
			}else if(markerA instanceof Flube) {
				fA = ((Flube) markerA).getId();
			}
			
			if(markerB instanceof Player) {
				pB = ((Player) markerB).getId();
			}else if(markerB instanceof Flube) {
				fB = ((Flube) markerB).getId();
			}
			
			markerA = null;
			markerB = null;
			
			SwapEvent event = new SwapEvent(pA, pB, fA, fB, true);
			getMainController().getEventMachine().fireEvent(event);
		}
	}
}
