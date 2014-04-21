/**
 * 
 */
package de.encala.cydonia.game.equipment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.AmbientLight;
import com.jme3.scene.Node;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.player.Player;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.SwapperInfo;

/**
 * @author encala
 * 
 */
public class ClientSwapper extends AbstractClientEquipment {

	private static final String TYPENAME = "Swapper";

	private static BufferedImage hudImg;

	private String name;

	private float range;

	private Node geom = new Node("Swapper");

	protected ParticleEmitter beam;

	protected AudioNode fireSound;
	
	
	AmbientLight markLightA;
	AmbientLight markLightB;

	/**
	 * 
	 */
	public ClientSwapper() {
		initHUDImgs();
	}

	/**
	 * @param name
	 * @param player
	 * @param gameController
	 */
	public ClientSwapper(String name, Player player,
			GameController gameController) {
		super(gameController);
		
		this.name = name;
		this.player = player;
	}

	@Override
	public void usePrimary(boolean activate) {

	}

	@Override
	public void useSecondary(boolean activate) {

	}





	@Override
	public void reset() {
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
