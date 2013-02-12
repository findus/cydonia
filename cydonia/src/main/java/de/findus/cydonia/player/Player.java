/**
 * 
 */
package de.findus.cydonia.player;

import java.util.ArrayList;
import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Findus
 *
 */
public class Player implements AnimEventListener{
	
	public static float MAX_STEP_HEIGHT = 0.2f;
	
	private static Vector3f RELATIVE_EYE_POSITION = new Vector3f(0, 0.70f, 0);
	
	private int id;
	
	private String name;
	
	private int team;
	
	private boolean alive = false;

	private PlayerInputState inputs;
	
	private Vector3f exactLoc = new Vector3f();
	
	private CharacterControl control;
	
	private Spatial model;
	
	private AnimChannel basechannel;
	
	private AnimChannel topchannel;

	private AnimControl animcontrol;
	
	private double healthpoints = 100;
	
	private long lastShot = 0;
	
	private int scores = 0;
	
	private Vector3f viewDir = Vector3f.UNIT_X;

	private boolean jumping = false;
	
	private List<Equipment> equips = new ArrayList<Equipment>();
	
	private int currEquip;

	/**
	 * Constructs a new Player and inits its physics and model.
	 * @param id the id of this player. If not available set to -1 and reset later.
	 * @param assetManager the used instance of AssetManager 
	 */
	public Player(int id) {
		this.id = id;

		
		inputs = new PlayerInputState();
		
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 0.8f);
        control = new CharacterControl(capsuleShape, MAX_STEP_HEIGHT);
        control.setJumpSpeed(10);
        control.setFallSpeed(25);
        control.setGravity(25);
	}
	
	public void handleInput(InputCommand command, boolean value) {
		switch (command) {
		case MOVEFRONT:
			inputs.setForward(value);
			break;
		case MOVEBACK:
			inputs.setBack(value);
			break;
		case STRAFELEFT:
			inputs.setLeft(value);
			break;
		case STRAFERIGHT:
			inputs.setRight(value);
			break;
		case JUMP:
			if(value) {
				this.jump();
			}
			break;
		case SWITCHEQUIP:
			this.switchEquipment(value);
			break;
		default:
			break;
		}
		
		updateAnimationState();
	}
	
	public void updateAnimationState() {
		// Update Animation
		if(jumping) {
			
		}else if(inputs.isForward() || inputs.isBack() || inputs.isLeft() || inputs.isRight()) {
			basechannel.setAnim("RunBase", 0.5f);
			basechannel.setLoopMode(LoopMode.Loop);
			topchannel.setAnim("RunTop", 0.5f);
			topchannel.setLoopMode(LoopMode.Loop);
		}else {
			basechannel.setAnim("IdleBase", 0.5f);
			basechannel.setLoopMode(LoopMode.Loop);
			topchannel.setAnim("IdleTop", 0.5f);
			topchannel.setLoopMode(LoopMode.Loop);
		}
	}
	
	@Override
	public void onAnimCycleDone(AnimControl animControl, AnimChannel channel,
			String animName) {
		if(animName.equals("JumpStart")) {
			channel.setAnim("JumpLoop", 0.5f);
			channel.setLoopMode(LoopMode.Loop);
		}else if(animName.equals("JumpLoop")) {
			if(this.control.onGround()) {
				channel.setAnim("JumpEnd", 0.5f);
			}
		}else if(animName.equals("JumpEnd")) {
			jumping = false;
			updateAnimationState();
		}
	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel,
			String animName) {
		// TODO Auto-generated method stub
		
	}

	public void setModel(Spatial s) {
		this.model = s;
		
		animcontrol = model.getControl(AnimControl.class);
		animcontrol.addListener(this);
        basechannel = animcontrol.createChannel();
        basechannel.setSpeed(0.1f);
        topchannel = animcontrol.createChannel();
        topchannel.setSpeed(0.1f);
	}
	
	public void jump() {
		control.jump();
		this.jumping = true;
		basechannel.setAnim("JumpStart", 0.5f);
		basechannel.setLoopMode(LoopMode.Loop);
	}
	
	public void switchEquipment(boolean up) {
		if(this.equips.size() > 0) {
			this.currEquip = (this.currEquip + (up?1:-1)) % this.equips.size();
		}
	}
	
	public int getCurrEquipIndex() {
		return this.currEquip;
	}
	
	public void setCurrEquip(int index) {
		this.currEquip = index;
	}
	
	public Equipment getCurrentEquipment() {
		if(this.equips.size() > this.currEquip) {
			return this.equips.get(this.currEquip);
		}else {
			return null;
		}
	}
	
	/**
	 * Returns the model for visualization of this player.
	 * @return model of this player
	 */
	public Spatial getModel() {
		return model;
	}

	/**
	 * Returns the InputState oject.
	 * @return input state
	 */
	public PlayerInputState getInputState() {
		return this.inputs;
	}
	
	/**
	 * Sets the InputState object.
	 * @param pis input state
	 */
	public void setInputState(PlayerInputState pis) {
		this.inputs = pis;
		updateAnimationState();
	}

	/**
	 * Returns the physics control object.
	 * @return physics control
	 */
	public CharacterControl getControl() {
		return control;
	}
	
	public Vector3f getEyePosition() {
		return control.getPhysicsLocation().add(RELATIVE_EYE_POSITION);
	}

	/**
	 * Returns the id of this Player. The value -1 indicates the real id was not available at contruction time.
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this player. If not available set to -1 and reset later.
	 * @param id the id
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
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

	public double getHealthpoints() {
		return healthpoints;
	}

	public void setHealthpoints(double healthpoints) {
		this.healthpoints = healthpoints;
	}
	
	public long getLastShot() {
		return this.lastShot;
	}
	
	public void setLastShot(long time) {
		this.lastShot = time;
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
	 * @param team the team to set
	 */
	protected void setTeam(int team) {
		this.team = team;
	}

	/**
	 * @return the viewDir
	 */
	public Vector3f getViewDir() {
		return viewDir.clone();
	}

	/**
	 * @param viewDir the viewDir to set
	 */
	public void setViewDir(Vector3f viewDir) {
		this.viewDir = viewDir.clone();
		control.setViewDirection(viewDir.clone().setY(0).normalizeLocal());
	}

	/**
	 * @return the equips
	 */
	public List<Equipment> getEquips() {
		return equips;
	}

	/**
	 * @param equips the equips to set
	 */
	public void setEquips(List<Equipment> equips) {
		if(equips.size() <= this.currEquip) {
			this.currEquip = equips.size()-1;
		}
		this.equips = equips;
	}

}
