/**
 * 
 */
package de.findus.cydonia.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.findus.cydonia.main.GameController;

/**
 * This AppState controls the menu.
 * Should be attached when the game is paused and the menu should be visible.
 * @author Findus
 *
 */
public class MenuAppState extends AbstractAppState {

	/**
	 * Path to the menu description xml files.
	 */
	public static final String MENU_PATH = "de/findus/cydonia/gui/menu/";
	
	private GameController gameController;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.gameController = (GameController) app;
		gameController.getNifty().registerScreenController(gameController);
		gameController.getNifty().fromXmlWithoutStartScreen(MENU_PATH + "menu.xml");
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		this.actualizeScreen();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		this.actualizeScreen();
	}
	
	public void actualizeScreen() {
		switch (gameController.getGamestate()) {
		case LOBBY:
			gameController.getNifty().gotoScreen("lobbymenu");
			gameController.getInputManager().setCursorVisible(true);
			break;

		case PAUSED:
			gameController.getNifty().gotoScreen("pausemenu");
			gameController.getInputManager().setCursorVisible(true);
			break;
			
		case DEAD:
			gameController.getNifty().gotoScreen("deadscreen");
			gameController.getInputManager().setCursorVisible(false);
			break;
			
		case RUNNING:
			gameController.getNifty().gotoScreen("none");
			gameController.getInputManager().setCursorVisible(false);
			break;
			
		default:
			break;
		}
	}

}
