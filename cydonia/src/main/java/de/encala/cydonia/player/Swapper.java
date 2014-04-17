/**
 * 
 */
package de.encala.cydonia.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;

import de.encala.cydonia.level.WorldObject;
import de.encala.cydonia.main.MainController;
import de.encala.cydonia.messages.EquipmentInfo;
import de.encala.cydonia.messages.SwapperInfo;

/**
 * @author encala
 * 
 */
public class Swapper extends AbstractEquipment {

	private static final String TYPENAME = "Swapper";

	private static BufferedImage hudImg;

	private String name;

	private float range;

	private Node geom = new Node("Swapper");

	protected ParticleEmitter beam;

	protected AudioNode fireSound;

	protected WorldObject markerA;

	protected WorldObject markerB;

	/**
	 * 
	 */
	public Swapper() {
		initHUDImgs();
	}

	/**
	 * @param mainController
	 */
	public Swapper(MainController mainController) {
		super(mainController);
		initHUDImgs();
	}

	/**
	 * @param mainController
	 */
	public Swapper(String name, Player player, MainController mainController) {
		super(mainController);
		this.name = name;
		this.player = player;

		initHUDImgs();
	}

	@Override
	public String getTypeName() {
		return TYPENAME;
	}

	private void initHUDImgs() {
		try {
			if (hudImg == null) {
				hudImg = ImageIO.read(this.getClass().getResourceAsStream(
						"/de/encala/cydonia/gui/hud/swapper.png"));
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void initGeometry() {

	}

	@Override
	public void usePrimary(boolean activate) {

	}

	@Override
	public void useSecondary(boolean activate) {

	}

	@Override
	public void reset() {
		markerA = null;
		markerB = null;
	}

	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35, 35,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();

		gr.drawImage(hudImg, 0, 0, new Color(0, 0, 0, 0), null);

		return tmpimg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new SwapperInfo(this);
	}

	@Override
	public void loadInfo(EquipmentInfo info) {
		if (info instanceof SwapperInfo) {
			SwapperInfo i = (SwapperInfo) info;
			this.name = i.getName();
		}
	}

	@Override
	public Node getGeometry() {
		return this.geom;
	}

	@Override
	public void setActive(boolean active) {

	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

}
