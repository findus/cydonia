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


	/**
	 * Constructor.
	 * @param app the game controller
	 */
	public MenuAppState(GameController app) {
		this.gameController = app;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		/** init the screen */    
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		gameController.getNifty().fromXml(MENU_PATH + "menu.xml", "menu", gameController);
		gameController.getInputManager().setCursorVisible(true);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		gameController.getNifty().removeScreen("menu");
	}

}
