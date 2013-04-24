/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;

import de.findus.cydonia.events.PhaseEvent;
import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ServerPhaser extends Phaser {

	/**
	 * 
	 */
	public ServerPhaser() {
		super();
	}

	/**
	 * @param mainController
	 */
	public ServerPhaser(String name, float damage, long interval, Player player, MainController mainController) {
		super(name, damage, interval, player, mainController);
	}

	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		if(this.lastShotTime + this.getInterval() < System.currentTimeMillis()) {
			this.lastShotTime = System.currentTimeMillis();
			CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.3f)), this.player.getViewDir());
			
			if(result != null && result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player")) {
				Player victim = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				if(victim != null && victim.getTeam() != this.getPlayer().getTeam()) {
					PhaseEvent event = new PhaseEvent(this.getPlayer().getId(), victim.getId(), this.getDamage(), true);
					getMainController().getEventMachine().fireEvent(event);
				}
			}
		}
	}
}
