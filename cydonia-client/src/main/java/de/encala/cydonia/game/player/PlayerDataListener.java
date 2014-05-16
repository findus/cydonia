/**
 * 
 */
package de.encala.cydonia.game.player;

/**
 * @author Findus
 *
 */
public interface PlayerDataListener {

	void currEquipChanged();
	
	void equipsChanged();
	
	void inputChanged();
	
	void teamChanged();

	void viewDirChanged();

	void highlightedChanged();
}
