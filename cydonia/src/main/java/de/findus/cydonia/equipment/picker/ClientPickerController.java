/**
 * 
 */
package de.findus.cydonia.equipment.picker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import de.findus.cydonia.equipment.ClientEquipmentController;
import de.findus.cydonia.equipment.EquipmentModel;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PickerInfo;


/**
 * @author Findus
 *
 */
public class ClientPickerController implements ClientEquipmentController {

	private static Image[] hudImgs;
	private MainController maincontroller;
	
	/**
	 * 
	 */
	public ClientPickerController() {
		
	}

	@Override
	public void usePrimary(EquipmentModel e, boolean activate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useSecondary(EquipmentModel e, boolean activate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BufferedImage getHUDImage(EquipmentModel e) {
		if(!(e instanceof PickerModel)) {
			throw new InvalidParameterException("e must be of type " + PickerModel.class.getName());
		}
		if(hudImgs == null) {
			initHUDImgs();
		}
		
		PickerModel picker = (PickerModel) e;
		
		BufferedImage tmpimg = new BufferedImage(35*picker.getCapacity(), 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		int imgpos = 0;
		for(Flube f : picker.getRepository()) {
			if(f.getType() >= 0) {
				gr.drawImage(hudImgs[f.getType()], imgpos, 0, new Color(0, 0, 0, 0), null);
				imgpos += 35;
			}
		}

		return tmpimg;
	}
	
	private void initHUDImgs() {
		try {
			hudImgs = new Image[3];
			hudImgs[0] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_gold.png"));
			hudImgs[1] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_blue.png"));
			hudImgs[2] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_red.png"));
		} catch (IOException e) {
		}
	}


	@Override
	public void setActive(EquipmentModel e, boolean active) {
		
	}

	@Override
	public void reset(EquipmentModel e) {
		if(!(e instanceof PickerModel)) {
			throw new InvalidParameterException("e must be of type " + PickerModel.class.getName());
		}

		PickerModel picker = (PickerModel) e;
		picker.repository = new LinkedList<Flube>();
	}

	@Override
	public void loadInfo(EquipmentModel e, EquipmentInfo info) {
		if(!(info instanceof PickerInfo)) {
			throw new InvalidParameterException("info must be of type " + PickerInfo.class.getName());
		}
		if(!(e instanceof PickerModel)) {
			throw new InvalidParameterException("e must be of type " + PickerModel.class.getName());
		}

		PickerModel picker = (PickerModel) e;
		PickerInfo i = (PickerInfo) info;
		picker.setName(i.getName());
		picker.setRange(i.getRange());
		picker.setCapacity(i.getCapacity());
		picker.setRepository(new LinkedList<Flube>());
		for (Long id : i.getRepository()) {
			picker.getRepository().add(this.maincontroller.getWorldController().getFlube(id));
		}
	}

	@Override
	public void setMainController(MainController mc) {
		this.maincontroller = mc;
	}
}
