/**
 * 
 */
package de.encala.cydonia.game.equipment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.level.Flube;
import de.encala.cydonia.game.player.Player;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.PickerInfo;

/**
 * @author encala
 * 
 */
public class ClientPicker extends AbstractClientEquipment {

	static final String TYPENAME = "Picker";

	private static Image[] hudImgs;

	private String name;

	private float range;

	private int capacity;

	private List<Flube> repository = new LinkedList<Flube>();
	
	private Node node = new Node("Picker");

	private AudioNode placeSound;
	private AudioNode pickupSound;

	/**
	 * 
	 */
	public ClientPicker() {
		initHUDImgs();
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param gameController
	 */
	public ClientPicker(String name, float range, int capacity, Player player,
			GameController gameController) {
		super(gameController);

		initHUDImgs();
	}

	@Override
	public void initGeometry() {
		initSound();
	}
	
	private void initHUDImgs() {
		try {
			if (hudImgs == null) {
				hudImgs = new Image[3];
				hudImgs[0] = ImageIO.read(this.getClass().getResourceAsStream(
						"/de/encala/cydonia/gui/hud/inventory_gold.png"));
				hudImgs[1] = ImageIO.read(this.getClass().getResourceAsStream(
						"/de/encala/cydonia/gui/hud/inventory_blue.png"));
				hudImgs[2] = ImageIO.read(this.getClass().getResourceAsStream(
						"/de/encala/cydonia/gui/hud/inventory_red.png"));
			}
		} catch (IOException e) {
		}
	}

	private void initSound() {
		placeSound = new AudioNode(getGameController().getAssetManager(),
				"de/encala/cydonia/sounds/place_mono.wav", false);
		placeSound.setLooping(false);
		placeSound.setPositional(true);
		placeSound.setLocalTranslation(Vector3f.ZERO);
		placeSound.setVolume(1);
		this.node.attachChild(placeSound);

		pickupSound = new AudioNode(getGameController().getAssetManager(),
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
	public void loadInfo(EquipmentInfo info) {
		if (info instanceof PickerInfo) {
			PickerInfo i = (PickerInfo) info;
			this.name = i.getName();
			this.range = i.getRange();
			this.capacity = i.getCapacity();
			this.repository = new LinkedList<Flube>();
			for (Long id : i.getRepository()) {
				this.repository.add(getGameController().getWorldController()
						.getFlube(id));
			}
		}
	}
	
	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35 * this.capacity, 35,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();

		int imgpos = 0;
		for (Flube f : this.repository) {
			if (f.getType() >= 0) {
				gr.drawImage(hudImgs[f.getType()], imgpos, 0, new Color(0, 0,
						0, 0), null);
				imgpos += 35;
			}
		}

		return tmpimg;
	}

	@Override
	public Node getGeometry() {
		return this.node;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the range
	 */
	public float getRange() {
		return range;
	}

	/**
	 * @param range
	 *            the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity
	 *            the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the repository
	 */
	public List<Flube> getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(List<Flube> repository) {
		this.repository = repository;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTypeName() {
		return TYPENAME;
	}

	@Override
	public void reset() {
		// this.repository = new LinkedList<ServerFlube>();
	}

}
