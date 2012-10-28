/**
 * 
 */
package de.findus.cydonia.appstates;

import de.findus.cydonia.main.GameController;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * This AppState controls the menu.
 * Should be attached when the game is paused and the menu should be visible.
 * @author Findus
 *
 */
public class MenuController {

	/**
	 * Path to the menu description xml files.
	 */
	public static final String MENU_PATH = "de/findus/cydonia/gui/menu/";
	
	private GameController gameController;
	
	public MenuController(GameController game) {
		this.gameController = game;
		gameController.getNifty().registerScreenController(gameController);
		gameController.getNifty().fromXmlWithoutStartScreen(MENU_PATH + "menu.xml");
	}
	
	public void actualizeScreen() {
		switch (gameController.getGamestate()) {
		case LOBBY:
			gameController.getNifty().gotoScreen("lobbymenu");
			gameController.getInputManager().setCursorVisible(true);
			break;

		case MENU:
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
			
		case ROUNDOVER:
			gameController.getNifty().gotoScreen("roundoverscreen");
			gameController.getInputManager().setCursorVisible(false);
			gameController.getNifty().getCurrentScreen().findElementByName("scores").getRenderer(TextRenderer.class).setText(gameController.getScores());
			break;
			
		default:
			break;
		}
	}
	
	public void showScorebord() {
		gameController.getNifty().gotoScreen("scoreboardscreen");
		gameController.getNifty().getCurrentScreen().findElementByName("scores").getRenderer(TextRenderer.class).setText(gameController.getScores());						
	}

}
