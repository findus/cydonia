/**
 * 
 */
package de.findus.cydonia.appstates;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;

import com.jme3.niftygui.RenderImageJme;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import de.findus.cydonia.main.GameController;
import de.findus.cydonia.main.GameState;
import de.findus.cydonia.player.Player;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.SizeValue;

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
	
	private Element timetext;
	private Element healthpointstext;
	private Element	inventoryimg;
	private Element scoreboardlayer;
	private Element scorestext;
	private Element messagelayer;
	private Element messagetext;
	private Element hudlayer;
	
	
	public MenuController(GameController game) {
		this.gameController = game;
		
		gameController.getNifty().registerScreenController(gameController);
		gameController.getNifty().fromXmlWithoutStartScreen(MENU_PATH + "menu.xml");
		
		this.timetext = gameController.getNifty().getScreen("ingamescreen").findElementByName("timetext");
		this.healthpointstext = gameController.getNifty().getScreen("ingamescreen").findElementByName("healthpointstext");
		this.inventoryimg = gameController.getNifty().getScreen("ingamescreen").findElementByName("inventoryimg");
		this.scoreboardlayer = gameController.getNifty().getScreen("ingamescreen").findElementByName("scoreboardlayer");
		this.scorestext = gameController.getNifty().getScreen("ingamescreen").findElementByName("scorestext");
		this.messagelayer = gameController.getNifty().getScreen("ingamescreen").findElementByName("messagelayer");
		this.messagetext = gameController.getNifty().getScreen("ingamescreen").findElementByName("messagetext");
		this.hudlayer = gameController.getNifty().getScreen("ingamescreen").findElementByName("hudlayer");
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
			gameController.getNifty().gotoScreen("ingamescreen");
			gameController.getInputManager().setCursorVisible(false);
			hideHUD();
			showMessage("You will join the game next round. Please be patient...");
			break;

		case RUNNING:
			gameController.getNifty().gotoScreen("ingamescreen");
			gameController.getInputManager().setCursorVisible(false);
			hideMessage();
			showHUD();
			break;

		case ROUNDOVER:
			gameController.getNifty().gotoScreen("ingamescreen");
			gameController.getInputManager().setCursorVisible(false);
			hideHUD();
			String message = "Round is over. New round will start automatically in a view seconds...";
			Player scorer = gameController.getLastScorer();
			if(scorer != null) {
				message += "\n\n\n" + scorer.getName() + " scored.";
			}
			showMessage(message);
			break;

		default:
			break;
		}
	}
	
	public void showScoreboard() {
		scorestext.getRenderer(TextRenderer.class).setText(gameController.getScores());
		scoreboardlayer.setVisible(true);
	}
	
	public void hideScoreboard() {
		scoreboardlayer.setVisible(false);
	}
	
	public void showMessage(String msg) {
		messagetext.getRenderer(TextRenderer.class).setText(msg);
		messagelayer.setVisible(true);
	}
	
	public void hideMessage() {
		messagelayer.setVisible(false);
	}
	
	public void showHUD() {
		hudlayer.setVisible(true);
	}
	
	public void hideHUD() {
		hudlayer.setVisible(false);
	}

	public void updateHUD() {
		this.timetext.getRenderer(TextRenderer.class).setText(timeFormat.format(gameController.getRemainingTime()));
		
		if(gameController.getPlayer() != null && gameController.getPlayer().getCurrentEquipment() != null) {
			BufferedImage img = gameController.getPlayer().getCurrentEquipment().getHUDImage();

			AWTLoader loader =new AWTLoader();
			Image imageJME = loader.load(img, true);
			
			RenderImage rimg = new RenderImageJme(new Texture2D(imageJME));
			NiftyImage nimg = new NiftyImage(gameController.getNifty().getRenderEngine(), rimg);
			this.inventoryimg.getRenderer(ImageRenderer.class).setImage(nimg);
			this.inventoryimg.setConstraintWidth(new SizeValue(String.valueOf(nimg.getWidth())));
			this.hudlayer.layoutElements();
		}
		
		if(gameController.getPlayer() != null) {
			this.healthpointstext.getRenderer(TextRenderer.class).setText(String.valueOf(Math.round(gameController.getPlayer().getHealthpoints())));
		}
	}
}
