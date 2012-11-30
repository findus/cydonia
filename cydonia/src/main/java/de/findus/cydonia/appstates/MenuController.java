/**
 * 
 */
package de.findus.cydonia.appstates;

import java.text.SimpleDateFormat;

import de.findus.cydonia.main.GameController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * This AppState controls the menu.
 * Should be attached when the game is paused and the menu should be visible.
 * @author Findus
 *
 */
public class MenuController {

	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
	
	/**
	 * Path to the menu description xml files.
	 */
	public static final String MENU_PATH = "de/findus/cydonia/gui/menu/";
	
	private GameController gameController;
	
	private Element timeText;
	
	public MenuController(GameController game) {
		this.gameController = game;
		
		gameController.getNifty().registerScreenController(gameController);
		gameController.getNifty().fromXmlWithoutStartScreen(MENU_PATH + "menu.xml");
		
		this.timeText = gameController.getNifty().getScreen("hudscreen").findElementByName("time");
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
			
		case SPECTATE:
			gameController.getNifty().gotoScreen("spectatescreen");
			gameController.getInputManager().setCursorVisible(false);
			break;
			
		case RUNNING:
			gameController.getNifty().gotoScreen("hudscreen");
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

	public void updateHUD() {
		this.timeText.getRenderer(TextRenderer.class).setText(timeFormat.format(gameController.getRemainingTime()));
	}

}
