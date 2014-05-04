/**
 * 
 */
package de.encala.cydonia.game.player;

import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;

import de.encala.cydonia.game.equipment.ClientEquipment;
import de.encala.cydonia.game.level.Flag;
import de.encala.cydonia.game.level.MarkableObject;
import de.encala.cydonia.share.player.PlayerInputState;

/**
 * @author encala
 * 
 */
public class Player extends MarkableObject {

	private PlayerDataListener listener;
	
	private int id;

	private String name;

	private int team;

	private boolean alive = false;

	private long gameOverTime = 0;

	private PlayerInputState inputs;

	private Vector3f exactLoc = new Vector3f();

	private int scores = 0;

	private Vector3f viewDir = Vector3f.UNIT_X;

	private boolean jumping = false;

	private List<ClientEquipment> equips = new LinkedList<ClientEquipment>();

	private int currEquip;

	private Flag flag;

	private boolean highlighted;

	/**
	 * Constructs a new ServerPlayer and inits its physics and model.
	 * 
	 * @param id
	 *            the id of this player. If not available set to -1 and reset
	 *            later.
	 * @param assetManager
	 *            the used instance of AssetManager
	 */
	public Player(int id) {
		this.id = id;

		inputs = new PlayerInputState();
	}

	

	public int getCurrEquip() {
		return this.currEquip;
	}
	
	public void setCurrEquip(int index) {
		if (equips.size() > 0) {
			getCurrentEquipment().setActive(false);
			getCurrentEquipment().reset();
			int size = equips.size();
			currEquip = ((index % size) + size) % size;
			getCurrentEquipment().setActive(true);
		}
		if(listener != null) {
			listener.currEquipChanged();
		}
	}

	

	public ClientEquipment getCurrentEquipment() {
		if (this.equips.size() > this.currEquip) {
			return this.equips.get(this.currEquip);
		} else {
			return null;
		}
	}

	

	/**
	 * Returns the InputState oject.
	 * 
	 * @return input state
	 */
	public PlayerInputState getInputState() {
		return this.inputs;
	}

	public void setInputState(PlayerInputState is) {
		this.inputs = is;
		if(listener != null) {
			listener.inputChanged();
		}
	}

	

	/**
	 * Returns the id of this ServerPlayer. The value -1 indicates the real id was not
	 * available at contruction time.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this player. If not available set to -1 and reset later.
	 * 
	 * @param id
	 *            the id
	 */
	void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive
	 *            the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Vector3f getExactLoc() {
		return exactLoc;
	}

	public void setExactLoc(Vector3f smooth) {
		this.exactLoc = smooth;
	}

	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
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
		if(this.team != team) {
			this.team = team;
			if(listener != null) {
				listener.teamChanged();
			}
		}
	}

	/**
	 * @return the viewDir
	 */
	public Vector3f getViewDir() {
		return viewDir.clone();
	}

	public void setViewDir(Vector3f dir) {
		this.viewDir = dir;
		if(listener != null) {
			listener.viewDirChanged();
		}
	}

	/**
	 * @return the equips
	 */
	public List<ClientEquipment> getEquips() {
		return equips;
	}

	/**
	 * @param equips
	 *            the equips to set
	 */
	public void setEquips(List<ClientEquipment> equips) {
		if (equips.size() <= this.currEquip) {
			this.currEquip = equips.size() - 1;
		}
		this.equips = equips;
		if(listener != null) {
			listener.equipsChanged();
		}
	}

	/**
	 * @return the flag
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public long getGameOverTime() {
		return gameOverTime;
	}

	void setGameOverTime(long gameOverTime) {
		this.gameOverTime = gameOverTime;
	}
	
	void setListener(PlayerDataListener l) {
		this.listener = l;
	}
	
	void setForward(boolean value) {
		inputs.setForward(value);
		if(listener != null) {
			listener.inputChanged();
		}
	}
	
	void setBack(boolean value) {
		inputs.setBack(value);
		if(listener != null) {
			listener.inputChanged();
		}
	}
	
	void setLeft(boolean value) {
		inputs.setLeft(value);
		if(listener != null) {
			listener.inputChanged();
		}
	}
	
	void setRight(boolean value) {
		inputs.setRight(value);
		if(listener != null) {
			listener.inputChanged();
		}
	}
	
	void setJump(boolean value) {
		inputs.setJump(value);
		if(listener != null) {
			listener.inputChanged();
		}
	}



	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		if(listener != null) {
			listener.highlightedChanged();
		}
	}
	
	public boolean isHighlighted() {
		return this.highlighted;
	}
}
