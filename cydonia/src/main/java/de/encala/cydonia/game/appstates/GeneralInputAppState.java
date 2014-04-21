/**
 * 
 */
package de.encala.cydonia.game.appstates;

import static de.encala.cydonia.game.player.InputCommand.EXIT;
import static de.encala.cydonia.game.player.InputCommand.FPS;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.player.InputCommand;

/**
 * This Appstate controls user inputs and maps them to commands. Should be
 * attached when game is running.
 * 
 * @author encala
 * 
 */
public class GeneralInputAppState extends AbstractAppState implements
		ActionListener {

	private GameController gameController;
	private InputManager inputManager;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            the game controller
	 */
	public GeneralInputAppState(GameController app) {
		this.gameController = app;
		this.inputManager = app.getInputManager();
	}

	private void mapDefaultKeys() {
		inputManager.addMapping(EXIT.getCode(), new KeyTrigger(
				KeyInput.KEY_ESCAPE));
		inputManager
				.addMapping(FPS.getCode(), new KeyTrigger(KeyInput.KEY_F12));
		inputManager.addListener(this, EXIT.getCode(), FPS.getCode());

	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		mapDefaultKeys();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputManager.removeListener(this);
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		InputCommand command = InputCommand.parseInputCommand(name);
		if (command != null) {
			gameController.handlePlayerInput(command, isPressed);
		}
	}
}
