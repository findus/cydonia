/**
 * 
 */
package de.findus.cydonia.player;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Spatial;

/**
 * @author Findus
 *
 */
public class PlayerView {

	private Spatial model;
	
	private CharacterControl control;
	
	public PlayerView() {
		
	}
	
	/**
	 * Returns the model for visualization of this player.
	 * @return model of this player
	 */
	public Spatial getModel() {
		return model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(Spatial model) {
		this.model = model;
	}

	/**
	 * Returns the physics control object.
	 * @return physics control
	 */
	public CharacterControl getControl() {
		return control;
	}

	/**
	 * @param control the control to set
	 */
	public void setControl(CharacterControl control) {
		this.control = control;
	}
}
