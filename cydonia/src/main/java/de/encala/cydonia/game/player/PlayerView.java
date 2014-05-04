/**
 * 
 */
package de.encala.cydonia.game.player;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import de.encala.cydonia.share.player.ForceCharacterControl;

/**
 * @author Findus
 *
 */
public class PlayerView implements AnimEventListener, PlayerDataListener {

	public static float MAX_STEP_HEIGHT = 0.2f;

	private static Vector3f RELATIVE_EYE_POSITION = new Vector3f(0, 0.70f, 0);
	
	Player datamodel;
	
	private ForceCharacterControl control;

	private GhostControl ghostControl;

	private Node model;

	Node node;

	private AnimChannel basechannel;

	private AnimChannel topchannel;

	private AnimControl animcontrol;
	
	/**
	 * 
	 */
	public PlayerView(Player datamodel) {
		this.datamodel = datamodel;
		this.datamodel.setListener(this);
		this.node = new Node("player" + datamodel.getId());

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f,
				0.8f);
		control = new ForceCharacterControl(capsuleShape, MAX_STEP_HEIGHT);
		control.setJumpSpeed(10);
		control.setFallSpeed(25);
		control.setGravity(25);
		control.setMinimalForceAmount(2f);
		control.setForceDamping(0.97f);

		BoxCollisionShape boxShape = new BoxCollisionShape(new Vector3f(0.25f,
				0.8f, 0.25f));
		ghostControl = new GhostControl(boxShape);
		ghostControl.setCollisionGroup(GhostControl.COLLISION_GROUP_02);
		ghostControl.setCollideWithGroups(GhostControl.COLLISION_GROUP_02);

		node.addControl(control);
		node.addControl(ghostControl);
	}

	void updateAnimationState() {
		// Update Animation
		if (datamodel.getInputState().isForward() || datamodel.getInputState().isBack() || datamodel.getInputState().isLeft()
				|| datamodel.getInputState().isRight()) {
			basechannel.setAnim("RunBase", 0.5f);
			basechannel.setLoopMode(LoopMode.Loop);
			topchannel.setAnim("RunTop", 0.5f);
			topchannel.setLoopMode(LoopMode.Loop);
		} else {
			basechannel.setAnim("IdleBase", 0.5f);
			basechannel.setLoopMode(LoopMode.Loop);
			topchannel.setAnim("IdleTop", 0.5f);
			topchannel.setLoopMode(LoopMode.Loop);
		}
	}

	@Override
	public void onAnimCycleDone(AnimControl animControl, AnimChannel channel,
			String animName) {
		if (animName.equals("JumpStart")) {
			channel.setAnim("JumpLoop", 0.5f);
			channel.setLoopMode(LoopMode.Loop);
		} else if (animName.equals("JumpLoop")) {
			if (this.control.onGround()) {
				channel.setAnim("JumpEnd", 0.5f);
			}
		} else if (animName.equals("JumpEnd")) {
			updateAnimationState();
		}
	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel,
			String animName) {
		// TODO Auto-generated method stub

	}

	public void setModel(Node s) {
		if (this.node.hasChild(this.model)) {
			this.node.detachChild(this.model);
		}
		this.model = s;
		this.node.attachChild(this.model);

		animcontrol = model.getControl(AnimControl.class);
		animcontrol.addListener(this);
		basechannel = animcontrol.createChannel();
		basechannel.setSpeed(0.1f);
		topchannel = animcontrol.createChannel();
		topchannel.setSpeed(0.1f);
	}

	void jump() {
		if(this.control.onGround()) {
			control.jump();
			basechannel.setAnim("JumpStart", 0.5f);
			basechannel.setLoopMode(LoopMode.Loop);
		}
	}
	
	/**
	 * Returns the model for visualization of this player.
	 * 
	 * @return model of this player
	 */
	public Spatial getModel() {
		return model;
	}

	public Node getNode() {
		return node;
	}
	
	/**
	 * Returns the physics control object.
	 * 
	 * @return physics control
	 */
	public ForceCharacterControl getControl() {
		return control;
	}
	
	/**
	 * @return the ghostControl
	 */
	public GhostControl getGhostControl() {
		return ghostControl;
	}

	public Vector3f getEyePosition() {
		return control.getPhysicsLocation().add(RELATIVE_EYE_POSITION);
	}
	
	public Player getDataModel() {
		return this.datamodel;
	}

	@Override
	public void currEquipChanged() {
		// TODO remove old equip and attach new equip to node
		
	}

	@Override
	public void equipsChanged() {
		// TODO remove old equips and attach new curr equip to node
		
	}

	@Override
	public void inputChanged() {
		if(datamodel.getInputState().isJump()) {
			jump();
		}
		updateAnimationState();
	}

	@Override
	public void teamChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewDirChanged() {
		control.setViewDirection(datamodel.getViewDir().clone().setY(0).normalizeLocal());
	}

	@Override
	public void highlightedChanged() {
		if(datamodel.isHighlighted()) {
			AmbientLight highLight = new AmbientLight();
			highLight.setColor(ColorRGBA.White.mult(0.5f));
			highLight.setName("HighLight");
			model.addLight(highLight);
		} else {
			for(Light l : getModel().getLocalLightList()) {
				if(l.getName().equals("HighLight")) {
					getModel().removeLight(l);
				}
			}
		}
	}
}
