/**
 * 
 */
package de.findus.cydonia.appstates;

import com.jme3.app.SimpleApplication;
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
import de.findus.cydonia.server.InputUpdate;

/**
 * This Appstate controls user inputs and maps them to commands.
 * Should be attached when game is running.
 * 
 * @author Findus
 *
 */
public class GameInputAppState extends AbstractAppState implements ActionListener{

	private GameController gameController;
	private InputManager inputManager;
	private FlyByCamera flyCam;
	private BitmapText crosshair;
	
	/**
	 * Constructor.
	 * @param app the game controller
	 */
	public GameInputAppState(GameController app) {
		this.gameController = app;
		this.inputManager = app.getInputManager();
		flyCam = new FlyByCamera(app.getCamera());
	}
	
	@Override
	public void stateAttached(AppStateManager stateManager) {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Jump", "Attack");
        
        inputManager.addMapping(SimpleApplication.INPUT_MAPPING_HIDE_STATS, new KeyTrigger(KeyInput.KEY_F5));
        inputManager.addListener(this, SimpleApplication.INPUT_MAPPING_HIDE_STATS);
        
        inputManager.addMapping(SimpleApplication.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(this, SimpleApplication.INPUT_MAPPING_EXIT);
        
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
		if(name.equals("Left")) {
            if(isPressed) gameController.getPlayer().getInputState().setLeft(true); else gameController.getPlayer().getInputState().setLeft(false);
        }else if(name.equals("Right")) {
            if(isPressed) gameController.getPlayer().getInputState().setRight(true); else gameController.getPlayer().getInputState().setRight(false);
        }else if(name.equals("Up")) {
            if(isPressed) gameController.getPlayer().getInputState().setForward(true); else gameController.getPlayer().getInputState().setForward(false);
        }else if(name.equals("Down")) {
            if(isPressed) gameController.getPlayer().getInputState().setBack(true); else gameController.getPlayer().getInputState().setBack(false);
        }else if(name.equals("Jump")) {
            if(isPressed) gameController.getPlayer().getControl().jump();
        }else if(name.equals("Attack")) {
            if(isPressed) gameController.attack();
        }else if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            gameController.pauseGame();
        }
		InputUpdate m = new InputUpdate();
		m.setInputs(gameController.getPlayer().getInputState());
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
