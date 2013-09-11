/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.collision.CollisionResult;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.WorldObject;
import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ClientSwapper extends Swapper {
	
	AmbientLight markLightA;
	AmbientLight markLightB;
	
	/**
	 * 
	 */
	public ClientSwapper() {
		
	}

	/**
	 * @param mainController
	 */
	public ClientSwapper(MainController mainController) {
		super(mainController);
	}

	/**
	 * @param name
	 * @param player
	 * @param mainController
	 */
	public ClientSwapper(String name, Player player,
			MainController mainController) {
		super(name, player, mainController);
	}
	
	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.3f)), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange()) {
			if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
				Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				markA(target);
			}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
				Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
				markA(target);
			}
		}
	}
	
	@Override
	public void useSecondary(boolean activate) {
		if(!activate) return;

		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition().add(player.getViewDir().normalize().mult(0.4f)), this.player.getViewDir());
		if(result != null && result.getDistance() <= this.getRange()) {
			if((result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player"))) {
				Player target = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
				markB(target);
			}else if(getMainController().getWorldController().isFlube(result.getGeometry())) {
				Flube target = getMainController().getWorldController().getFlube((long)result.getGeometry().getUserData("id"));
				markB(target);
			}
		}
	}
	
	private void markA(WorldObject s) {
		if(markerA != null) {
			unmark(markerA);
		}
		if(markerB == s) {
			unmark(markerB);
		}
		
		markerA = s;
		Spatial model = markerA.getModel();
		
		if(markLightA == null) {
			markLightA = new AmbientLight();
			markLightA.setColor(ColorRGBA.Orange);
			markLightA.setName("MarkLight");
		}
		model.addLight(markLightA);
		System.out.println("mark: " + s);
	}
	
	private void markB(WorldObject s) {
		if(markerB != null) {
			unmark(markerB);
		}
		if(markerA == s) {
			unmark(markerA);
		}
		
		markerB = s;
		Spatial model = markerB.getModel();
		
		if(markLightB == null) {
			markLightB = new AmbientLight();
			markLightB.setColor(ColorRGBA.Cyan);
			markLightB.setName("MarkLight");
		}
		model.addLight(markLightB);
		System.out.println("mark: " + s);
	}

	private void unmark(WorldObject marker) {
		System.out.println("try to unmark: " + marker);
		for(Light l : marker.getModel().getLocalLightList()) {
			if(l.getName().equals("MarkLight")) {
				marker.getModel().removeLight(l);
				System.out.println("unmark: " + marker);
			}
		}
	}
	
	@Override
	public void reset() {
		if(markerA != null) unmark(markerA);
		if(markerB != null) unmark(markerB);
		
		super.reset();
	}

}
