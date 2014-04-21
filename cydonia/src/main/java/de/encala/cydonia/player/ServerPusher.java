/**
 * 
 */
package de.encala.cydonia.player;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;

import de.encala.cydonia.events.PushEvent;
import de.encala.cydonia.share.MainController;

/**
 * @author encala
 * 
 */
public class ServerPusher extends Pusher {

	/**
	 * 
	 */
	public ServerPusher() {
		super();
	}

	/**
	 * @param mainController
	 */
	public ServerPusher(String name, float force, long interval, Player player,
			MainController mainController) {
		super(name, force, interval, player, mainController);
	}

	@Override
	public void usePrimary(boolean activate) {
		if (!activate)
			return;

		if (this.lastShotTime + this.getInterval() < System.currentTimeMillis()) {
			this.lastShotTime = System.currentTimeMillis();
			CollisionResult result = getMainController()
					.getWorldController()
					.pickRoot(
							this.player.getEyePosition().add(
									player.getViewDir().normalize().mult(0.3f)),
							this.player.getViewDir());

			if (result != null
					&& result.getGeometry().getParent() != null
					&& result.getGeometry().getParent().getName() != null
					&& result.getGeometry().getParent().getName()
							.startsWith("player")) {
				Player victim = getMainController().getPlayerController()
						.getPlayer(
								Integer.valueOf(result.getGeometry()
										.getParent().getName().substring(6)));
				if (victim != null && victim.getId() != this.player.getId()) {
					Vector3f forceVector = this.player.getViewDir().normalize()
							.mult(this.getForce());
					PushEvent event = new PushEvent(this.getPlayer().getId(),
							victim.getId(), forceVector, true);
					getMainController().getEventMachine().fireEvent(event);
				}
			}
		}
	}
}
