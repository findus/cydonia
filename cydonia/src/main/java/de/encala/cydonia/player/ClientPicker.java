/**
 * 
 */
package de.encala.cydonia.player;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.encala.cydonia.share.MainController;

/**
 * @author encala
 * 
 */
public class ClientPicker extends Picker {

	private Node node = new Node("Picker");

	private AudioNode placeSound;
	private AudioNode pickupSound;

	/**
	 * 
	 */
	public ClientPicker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param mainController
	 */
	public ClientPicker(String name, float range, int capacity, Player player,
			MainController mainController) {
		super(name, range, capacity, player, mainController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initGeometry() {
		initSound();
	}

	private void initSound() {
		placeSound = new AudioNode(getMainController().getAssetManager(),
				"de/encala/cydonia/sounds/place_mono.wav", false);
		placeSound.setLooping(false);
		placeSound.setPositional(true);
		placeSound.setLocalTranslation(Vector3f.ZERO);
		placeSound.setVolume(1);
		this.node.attachChild(placeSound);

		pickupSound = new AudioNode(getMainController().getAssetManager(),
				"de/encala/cydonia/sounds/pickup_mono.wav", false);
		pickupSound.setLooping(false);
		pickupSound.setPositional(true);
		pickupSound.setLocalTranslation(Vector3f.ZERO);
		pickupSound.setVolume(1);
		this.node.attachChild(pickupSound);
	}

	@Override
	public void usePrimary(boolean activate) {
		if (activate) {
			// placeSound.playInstance();
		}
	}

	@Override
	public void useSecondary(boolean activate) {
		if (activate) {
			// pickupSound.playInstance();
		}
	}

	@Override
	public Node getGeometry() {
		return this.node;
	}

}
