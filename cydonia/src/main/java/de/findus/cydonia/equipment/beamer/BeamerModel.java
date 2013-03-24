/**
 * 
 */
package de.findus.cydonia.equipment.beamer;

import java.security.InvalidParameterException;

import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;

import de.findus.cydonia.equipment.AbstractEquipmentModel;
import de.findus.cydonia.equipment.EquipmentController;
import de.findus.cydonia.equipment.picker.ClientPickerController;
import de.findus.cydonia.equipment.picker.ServerPickerController;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public class BeamerModel extends AbstractEquipmentModel {

private String name;
	
	protected float range;
	
	protected boolean beaming;
	
	protected Node geom = new Node("Beamer");
	
	protected ParticleEmitter beam;
	
	public BeamerModel() {
	}
	
	public BeamerModel(String name, float range, Player player) {
		super(player);
		
		this.name = name;
		this.range = range;
		this.player = player;
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

	@Override
	public Node getGeometry() {
		return this.geom;
	}

	public void setBeaming(boolean activate) {
		this.beaming = activate;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new BeamerInfo(this);
	}

	@Override
	public EquipmentController getController(String type, MainController mc) {
		EquipmentController con;
		if("Client".equals(type)) {
			con = new ClientBeamerController();
		} else if("Server".equals(type)) {
			con = new ServerBeamerController();
		}else {
			throw new InvalidParameterException();
		}
		con.setMainController(mc);
		return con;
	}
}
