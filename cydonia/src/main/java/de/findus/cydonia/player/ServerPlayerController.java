/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ServerPlayerController extends PlayerController {
    

	public ServerPlayerController(MainController mainController) {
		super(mainController);
	}

	@Override
	protected String getType() {
		return "Server";
	}

}
