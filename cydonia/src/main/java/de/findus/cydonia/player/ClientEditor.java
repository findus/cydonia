/**
 * 
 */
package de.findus.cydonia.player;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ClientEditor extends Editor {

	/**
	 * 
	 */
	public ClientEditor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param mainController
	 */
	public ClientEditor(String name, float range, int capacity, Player player,
			MainController mainController) {
		super(name, range, capacity, player, mainController);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void usePrimary(boolean activate) {
		
	}
	
	public void useSecondary(boolean activate) {
		
	}

}
