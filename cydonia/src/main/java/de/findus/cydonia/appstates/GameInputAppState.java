/**
 * 
 */
package de.findus.cydonia.appstates;

import static de.findus.cydonia.player.InputCommand.ATTACK;
import static de.findus.cydonia.player.InputCommand.EXIT;
import static de.findus.cydonia.player.InputCommand.JUMP;
import static de.findus.cydonia.player.InputCommand.MOVEBACK;
import static de.findus.cydonia.player.InputCommand.MOVEFRONT;
import static de.findus.cydonia.player.InputCommand.PICKUP;
import static de.findus.cydonia.player.InputCommand.PLACE;
import static de.findus.cydonia.player.InputCommand.SCOREBOARD;
import static de.findus.cydonia.player.InputCommand.STRAFELEFT;
import static de.findus.cydonia.player.InputCommand.STRAFERIGHT;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import de.findus.cydonia.main.GameController;
import de.findus.cydonia.main.ServerConnector;
import de.findus.cydonia.messages.InputMessage;
import de.findus.cydonia.player.InputCommand;

/**
 * This Appstate controls user inputs and maps them to commands.
 * Should be attached when game is running.
 * 
 * @author Findus
 *
 */
public class GameInputAppState extends AbstractAppState implements ActionListener{

	private GameController gameController;
	private ServerConnector serverConnector;
	private InputManager inputManager;
	private FlyByCamera flyCam;
	private BitmapText crosshair;
	
	/**
	 * Constructor.
	 * @param app the game controller
	 */
	public GameInputAppState(GameController app, ServerConnector scon) {
		this.gameController = app;
		this.serverConnector = scon;
		this.inputManager = app.getInputManager();
		flyCam = new FlyByCamera(app.getCamera());
	}
	
	private void mapDefaultKeys() {
        inputManager.addMapping(EXIT.getCode(), new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(this, EXIT.getCode());
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		inputManager.addMapping(STRAFELEFT.getCode(), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(STRAFERIGHT.getCode(), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(MOVEFRONT.getCode(), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(MOVEBACK.getCode(), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(JUMP.getCode(), new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping(PLACE.getCode(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(PICKUP.getCode(), new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, STRAFELEFT.getCode(), STRAFERIGHT.getCode(), MOVEFRONT.getCode(), MOVEBACK.getCode(), JUMP.getCode(), ATTACK.getCode(), PICKUP.getCode(), PLACE.getCode());
        
        inputManager.addMapping(SCOREBOARD.getCode(), new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addListener(this, SCOREBOARD.getCode());
        
        mapDefaultKeys();
        
        flyCam.registerWithInput(inputManager);
        flyCam.setEnabled(true);
        
        crosshair = getCrosshair();
        gameController.getGuiNode().attachChild(crosshair);
    }

	@Override
    public void stateDetached(AppStateManager stateManager) {
    	gameController.getGuiNode().detachChild(crosshair);
    	inputManager.removeListener(this);
    	flyCam.setEnabled(false);
    }

	@Override
    public void onAction(String name, boolean isPressed, float tpf) {
		InputCommand command = InputCommand.parseInputCommand(name);
		if(command != null) {
			InputMessage msg = new InputMessage(gameController.getPlayer().getId(), command, isPressed);
			serverConnector.sendMessage(msg);
		}
	}
	
	/**
	 * A plus sign used as crosshairs to help the player with aiming.
	 */
    protected BitmapText getCrosshair() {
      //guiNode.detachAllChildren();
      BitmapFont guiFont = gameController.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
      BitmapText ch = new BitmapText(guiFont, false);
      ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      ch.setText("+");        // fake crosshairs :)
      ch.setLocalTranslation( // center
        gameController.getContext().getSettings().getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
        gameController.getContext().getSettings().getHeight() / 2 + ch.getLineHeight() / 2, 0);
      return ch;
    }

}
