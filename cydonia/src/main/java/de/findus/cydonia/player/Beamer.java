/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.EquipmentInfo;

/**
 * @author Findus
 *
 */
public class Beamer extends AbstractEquipment {

	private static Image[] hudImgs;
	
	private String name;
	
	private float range;
	
	public Beamer(String name, float range, Player player, WorldController worldController, EventMachine eventMachine) {
		this.name = name;
		this.range = range;
		this.worldController = worldController;
		this.player = player;
		this.eventMachine = eventMachine;
		
		
		try {
			if(hudImgs == null) {
				hudImgs = new Image[3];
				hudImgs[0] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_gold.png"));
				hudImgs[1] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_blue.png"));
				hudImgs[2] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_red.png"));
			}
		} catch (IOException e) {
		}
	}
	
	@Override
	public void usePrimary() {
		// TODO Auto-generated method stub

	}

	@Override
	public void useSecondary() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {

	}

	@Override
	public BufferedImage getHUDImage() {
		// TODO Auto-generated method stub
		return null;
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
