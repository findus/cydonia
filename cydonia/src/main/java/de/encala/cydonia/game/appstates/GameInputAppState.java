/**
 * 
 */
package de.encala.cydonia.game.appstates;

import static de.encala.cydonia.share.player.InputCommand.ATTACK;
import static de.encala.cydonia.share.player.InputCommand.HUD;
import static de.encala.cydonia.share.player.InputCommand.JUMP;
import static de.encala.cydonia.share.player.InputCommand.MOVEBACK;
import static de.encala.cydonia.share.player.InputCommand.MOVEFRONT;
import static de.encala.cydonia.share.player.InputCommand.STRAFELEFT;
import static de.encala.cydonia.share.player.InputCommand.STRAFERIGHT;
import static de.encala.cydonia.share.player.InputCommand.SWITCHEQUIP;
import static de.encala.cydonia.share.player.InputCommand.SWITCHEQUIPDOWN;
import static de.encala.cydonia.share.player.InputCommand.SWITCHEQUIPUP;
import static de.encala.cydonia.share.player.InputCommand.USEPRIMARY;
import static de.encala.cydonia.share.player.InputCommand.USESECONDARY;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.share.player.InputCommand;

/**
 * This Appstate controls user inputs and maps them to commands. Should be
 * attached when game is running.
 * 
 * @author encala
 * 
 */
public class GameInputAppState extends AbstractAppState implements
		ActionListener {

	private GameController gameController;
	private InputManager inputManager;
	private FirstPersonCamera camController;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            the game controller
	 */
	public GameInputAppState(GameController app) {
		this.gameController = app;
		this.inputManager = app.getInputManager();
		camController = new FirstPersonCamera(app.getCamera(), Vector3f.UNIT_Y);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		inputManager
				.addMapping(HUD.getCode(), new KeyTrigger(KeyInput.KEY_F11));
		inputManager.addMapping(STRAFELEFT.getCode(), new KeyTrigger(
				KeyInput.KEY_A));
		inputManager.addMapping(STRAFERIGHT.getCode(), new KeyTrigger(
				KeyInput.KEY_D));
		inputManager.addMapping(MOVEFRONT.getCode(), new KeyTrigger(
				KeyInput.KEY_W));
		inputManager.addMapping(MOVEBACK.getCode(), new KeyTrigger(
				KeyInput.KEY_S));
		inputManager.addMapping(JUMP.getCode(), new KeyTrigger(
				KeyInput.KEY_LSHIFT));
		inputManager.addMapping(USEPRIMARY.getCode(), new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		inputManager.addMapping(USESECONDARY.getCode(), new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(SWITCHEQUIPUP.getCode(), new KeyTrigger(
				KeyInput.KEY_PGUP));
		inputManager.addMapping(SWITCHEQUIPDOWN.getCode(), new KeyTrigger(
				KeyInput.KEY_PGDN));
		inputManager.addMapping(SWITCHEQUIPDOWN.getCode(),
				new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping(SWITCHEQUIPUP.getCode(), new MouseAxisTrigger(
				MouseInput.AXIS_WHEEL, true));
		inputManager.addListener(this, STRAFELEFT.getCode(), STRAFERIGHT.getCode(),
				MOVEFRONT.getCode(), MOVEBACK.getCode(), JUMP.getCode(),
				ATTACK.getCode(), USEPRIMARY.getCode(), USESECONDARY.getCode(),
				SWITCHEQUIPUP.getCode(), SWITCHEQUIPDOWN.getCode());

		camController.registerWithInput(inputManager);
		camController.setEnabled(true);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputManager.removeListener(this);
		camController.setEnabled(false);
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		InputCommand command = InputCommand.parseInputCommand(name);
		if (command != null) {
			if (command == SWITCHEQUIPUP) {
				if (!isPressed) {
					return;
				}
				command = SWITCHEQUIP;
				isPressed = true;
			}
			if (command == SWITCHEQUIPDOWN) {
				if (!isPressed) {
					return;
				}
				command = SWITCHEQUIP;
				isPressed = false;
			}
			gameController.handlePlayerInput(command, isPressed);
		}
	}
}
