/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.collision.CollisionResult;

import de.findus.cydonia.events.BeamEvent;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public class Beamer extends AbstractEquipment {

	private static BufferedImage hudImg;
	
	private String name;
	
	private float range;
	
	public Beamer() {
		
	}
	
	public Beamer(String name, float range, Player player, MainController mainController) {
		super(mainController);
		
		this.name = name;
		this.range = range;
		this.player = player;
		
		
		try {
			if(hudImg == null) {
				hudImg = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_gold.png"));
			}
		} catch (IOException e) {
		}
	}
	
	@Override
	public void usePrimary() {
		// no action yet
	}

	@Override
	public void useSecondary() {
		CollisionResult result = getMainController().getWorldController().pickRoot(this.player.getEyePosition(), this.player.getViewDir());
		if(result != null && result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player")) {
			Player victim = getMainController().getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
			if(victim != null && victim.getTeam() != player.getTeam()) {
				BeamEvent beam = new BeamEvent(player.getId(), victim.getId(), true);
				getMainController().getEventMachine().fireEvent(beam);
			}
		}
	}

	@Override
	public void reset() {

	}

	@Override
	public BufferedImage getHUDImage() {
		return hudImg;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new BeamerInfo(this);
	}

	@Override
	public void loadInfo(EquipmentInfo info) {
		if(info instanceof BeamerInfo) {
			BeamerInfo i = (BeamerInfo) info;
			this.name = i.getName();
			this.range = i.getRange();
		}
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
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
	 * @param range the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}

}
