/**
 * 
 */
package de.findus.cydonia.equipment.picker;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import com.jme3.scene.Node;

import de.findus.cydonia.equipment.AbstractEquipmentModel;
import de.findus.cydonia.equipment.EquipmentController;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PickerInfo;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public class PickerModel extends AbstractEquipmentModel {

	protected String name;
	
	protected float range;
	
	protected int capacity;
	
	protected List<Flube> repository = new LinkedList<Flube>();
	
	public PickerModel() {
	}
	
	public PickerModel(String name, float range, int capacity, Player player) {
		super(player);
		
		this.name = name;
		this.range = range;
		this.capacity = capacity;
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

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
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
	 * @param repository the repository to set
	 */
	public void setRepository(List<Flube> repository) {
		this.repository = repository;
	}

	@Override
	public Node getGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new PickerInfo(this);
	}

	@Override
	public EquipmentController getController(String type, MainController mc) {
		EquipmentController con;
		if("Client".equals(type)) {
			con = new ClientPickerController();
		} else if("Server".equals(type)) {
			con = new ServerPickerController();
		}else {
			throw new InvalidParameterException();
		}
		con.setMainController(mc);
		return con;
	}
}
