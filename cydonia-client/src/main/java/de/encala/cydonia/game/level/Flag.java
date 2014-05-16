/**
 * 
 */
package de.encala.cydonia.game.level;

import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import de.encala.cydonia.game.player.Player;

/**
 * @author encala
 * 
 */
@Serializable
public class Flag {

	private int id;

	private transient Player player;

	private int team;

	private Vector3f origin;

	private boolean inBase;

	private transient Spatial model;

	private transient Node baseModel;

	private transient GhostControl control;

	private transient GhostControl baseControl;

	/**
	 * 
	 */
	public Flag() {

	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the team
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(int team) {
		this.team = team;
	}

	/**
	 * @return the origin
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(Vector3f origin) {
		this.origin = origin;
	}

	/**
	 * @return the inBase
	 */
	public boolean isInBase() {
		return inBase;
	}

	/**
	 * @param inBase
	 *            the inBase to set
	 */
	public void setInBase(boolean inBase) {
		this.inBase = inBase;
	}

	/**
	 * @return the model
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Spatial model) {
		this.model = model;
	}

	/**
	 * @return the baseModel
	 */
	public Node getBaseModel() {
		return baseModel;
	}

	/**
	 * @param baseModel
	 *            the baseModel to set
	 */
	public void setBaseModel(Node baseModel) {
		this.baseModel = baseModel;
	}

	/**
	 * @return the control
	 */
	public GhostControl getControl() {
		return control;
	}

	/**
	 * @param control
	 *            the control to set
	 */
	public void setControl(GhostControl control) {
		this.control = control;
	}

	/**
	 * @return the baseControl
	 */
	public GhostControl getBaseControl() {
		return baseControl;
	}

	/**
	 * @param baseControl
	 *            the baseControl to set
	 */
	public void setBaseControl(GhostControl baseControl) {
		this.baseControl = baseControl;
	}

}
