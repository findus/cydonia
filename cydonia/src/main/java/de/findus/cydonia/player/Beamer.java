/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	
	private boolean beaming;
	
	public Beamer() {
		initHUDImgs();
	}
	
	public Beamer(String name, float range, Player player, MainController mainController) {
		super(mainController);
		
		this.name = name;
		this.range = range;
		this.player = player;
		
		initHUDImgs();
	}
	
	private void initHUDImgs() {
		try {
			if(hudImg == null) {
				hudImg = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/beamer.png"));
			}
		} catch (IOException e) {
		}
	}
	
	@Override
	public void usePrimary(boolean activate) {
		this.setBeaming(activate);
		
	}

	@Override
	public void useSecondary(boolean activate) {
		// no action yet
	}

	@Override
	public void reset() {
		this.beaming = false;
	}

	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		gr.drawImage(hudImg, 0, 0, new Color(0, 0, 0, 0), null);

		return tmpimg;
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

	/**
	 * @return the beaming
	 */
	public boolean isBeaming() {
		return beaming;
	}

	/**
	 * @param beaming the beaming to set
	 */
	public void setBeaming(boolean beaming) {
		this.beaming = beaming;
	}

}
