/**
 * 
 */
package de.encala.cydonia.game.appstates;

import com.jme3.collision.MotionAllowedListener;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * @author encala
 * 
 */
public class FirstPersonCamera implements AnalogListener {

	private static String[] mappings = new String[] { "FLYCAM_Left",
			"FLYCAM_Right", "FLYCAM_Up", "FLYCAM_Down",

			"FLYCAM_StrafeLeft", "FLYCAM_StrafeRight", "FLYCAM_Forward",
			"FLYCAM_Backward", };

	protected Camera cam;
	protected Vector3f upAxis;
	protected float rotationSpeed = 1f;
	protected float moveSpeed = 3f;
	protected MotionAllowedListener motionAllowed = null;
	protected boolean enabled = true;
	protected boolean invertY = false;
	protected InputManager inputManager;

	/**
	 * Creates a new FlyByCamera to control the given Camera object.
	 * 
	 * @param cam
	 */
	public FirstPersonCamera(Camera cam, Vector3f upAxis) {
		this.cam = cam;
		this.upAxis = upAxis;
	}

	/**
	 * Sets the up vector that should be used for the camera.
	 * 
	 * @param upVec
	 */
	public void setUpVector(Vector3f upVec) {
		upAxis.set(upVec);
	}

	public void setMotionAllowedListener(MotionAllowedListener listener) {
		this.motionAllowed = listener;
	}

	/**
	 * Sets the move speed. The speed is given in world units per second.
	 * 
	 * @param moveSpeed
	 */
	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	/**
	 * Gets the move speed. The speed is given in world units per second.
	 * 
	 * @return moveSpeed
	 */
	public float getMoveSpeed() {
		return moveSpeed;
	}

	/**
	 * Sets the rotation speed.
	 * 
	 * @param rotationSpeed
	 */
	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	/**
	 * Gets the move speed. The speed is given in world units per second.
	 * 
	 * @return rotationSpeed
	 */
	public float getRotationSpeed() {
		return rotationSpeed;
	}

	/**
	 * @param enable
	 *            If false, the camera will ignore input.
	 */
	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	/**
	 * @return If enabled
	 * @see FlyByCamera#setEnabled(boolean)
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Registers the FlyByCamera to receive input events from the provided
	 * Dispatcher.
	 * 
	 * @param inputManager
	 */
	public void registerWithInput(InputManager inputManager) {
		this.inputManager = inputManager;

		// both mouse and button - rotation of cam
		inputManager.addMapping("FLYCAM_Left", new MouseAxisTrigger(
				MouseInput.AXIS_X, true), new KeyTrigger(KeyInput.KEY_LEFT));

		inputManager.addMapping("FLYCAM_Right", new MouseAxisTrigger(
				MouseInput.AXIS_X, false), new KeyTrigger(KeyInput.KEY_RIGHT));

		inputManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(
				MouseInput.AXIS_Y, false), new KeyTrigger(KeyInput.KEY_UP));

		inputManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(
				MouseInput.AXIS_Y, true), new KeyTrigger(KeyInput.KEY_DOWN));

		// keyboard only WASD for movement and WZ for rise/lower height
		inputManager.addMapping("FLYCAM_StrafeLeft", new KeyTrigger(
				KeyInput.KEY_A));
		inputManager.addMapping("FLYCAM_StrafeRight", new KeyTrigger(
				KeyInput.KEY_D));
		inputManager.addMapping("FLYCAM_Forward",
				new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("FLYCAM_Backward", new KeyTrigger(
				KeyInput.KEY_S));

		inputManager.addListener(this, mappings);

		Joystick[] joysticks = inputManager.getJoysticks();
		if (joysticks != null && joysticks.length > 0) {
			for (Joystick j : joysticks) {
				mapJoystick(j);
			}
		}
	}

	protected void mapJoystick(Joystick joystick) {

		// Map it differently if there are Z axis
		if (joystick.getAxis(JoystickAxis.Z_ROTATION) != null
				&& joystick.getAxis(JoystickAxis.Z_AXIS) != null) {

			// Make the left stick move
			joystick.getXAxis().assignAxis("FLYCAM_StrafeRight",
					"FLYCAM_StrafeLeft");
			joystick.getYAxis().assignAxis("FLYCAM_Backward", "FLYCAM_Forward");

			// And the right stick control the camera
			joystick.getAxis(JoystickAxis.Z_ROTATION).assignAxis("FLYCAM_Down",
					"FLYCAM_Up");
			joystick.getAxis(JoystickAxis.Z_AXIS).assignAxis("FLYCAM_Right",
					"FLYCAM_Left");

		} else {
			joystick.getPovXAxis().assignAxis("FLYCAM_StrafeRight",
					"FLYCAM_StrafeLeft");
			joystick.getPovYAxis().assignAxis("FLYCAM_Forward",
					"FLYCAM_Backward");
			joystick.getXAxis().assignAxis("FLYCAM_Right", "FLYCAM_Left");
			joystick.getYAxis().assignAxis("FLYCAM_Down", "FLYCAM_Up");
		}
	}

	/**
	 * Registers the FlyByCamera to receive input events from the provided
	 * Dispatcher.
	 * 
	 * @param inputManager
	 */
	public void unregisterInput() {

		if (inputManager == null) {
			return;
		}

		for (String s : mappings) {
			if (inputManager.hasMapping(s)) {
				inputManager.deleteMapping(s);
			}
		}

		inputManager.removeListener(this);

		Joystick[] joysticks = inputManager.getJoysticks();
		if (joysticks != null && joysticks.length > 0) {
			Joystick joystick = joysticks[0];

			// No way to unassign axis
		}
	}

	protected void rotateCameraHorizontal(float value) {
		Matrix3f mat = new Matrix3f();
		mat.fromAngleNormalAxis(rotationSpeed * value, upAxis);

		Vector3f up = cam.getUp();
		Vector3f left = cam.getLeft();
		Vector3f dir = cam.getDirection();

		mat.mult(up, up);
		mat.mult(left, left);
		mat.mult(dir, dir);

		Quaternion q = new Quaternion();
		q.fromAxes(left, up, dir);
		q.normalizeLocal();

		cam.setAxes(q);
	}

	protected void rotateCameraVertical(float value) {
		if (invertY) {
			value = -value;
		}

		Matrix3f mat = new Matrix3f();
		mat.fromAngleNormalAxis(rotationSpeed * value, cam.getLeft());

		Vector3f up = cam.getUp();
		Vector3f left = cam.getLeft();
		Vector3f dir = cam.getDirection();

		// do not look more up than up^^
		if (upAxis.angleBetween(mat.mult(up)) > FastMath.HALF_PI) {
			return;
		}

		mat.mult(up, up);
		mat.mult(left, left);
		mat.mult(dir, dir);

		Quaternion q = new Quaternion();
		q.fromAxes(left, up, dir);
		q.normalizeLocal();

		cam.setAxes(q);
	}

	protected void moveCamera(float value, boolean sideways) {
		Vector3f vel = new Vector3f();
		Vector3f pos = cam.getLocation().clone();

		if (sideways) {
			cam.getLeft(vel);
		} else {
			cam.getDirection(vel);
		}
		vel.multLocal(value * moveSpeed);

		if (motionAllowed != null)
			motionAllowed.checkMotionAllowed(pos, vel);
		else
			pos.addLocal(vel);

		cam.setLocation(pos);
	}

	public void onAnalog(String name, float value, float tpf) {
		if (!enabled)
			return;

		if (name.equals("FLYCAM_Left")) {
			rotateCameraHorizontal(value);
		} else if (name.equals("FLYCAM_Right")) {
			rotateCameraHorizontal(-value);
		} else if (name.equals("FLYCAM_Up")) {
			rotateCameraVertical(-value);
		} else if (name.equals("FLYCAM_Down")) {
			rotateCameraVertical(value);
		} else if (name.equals("FLYCAM_Forward")) {
			moveCamera(value, false);
		} else if (name.equals("FLYCAM_Backward")) {
			moveCamera(-value, false);
		} else if (name.equals("FLYCAM_StrafeLeft")) {
			moveCamera(value, true);
		} else if (name.equals("FLYCAM_StrafeRight")) {
			moveCamera(-value, true);
		}
	}

}
