/**
 * 
 */
package de.findus.cydonia.events;

/**
 * @author Findus
 *
 */
public class ConnectionEstablishedEvent extends AbstractEvent {

	private String level;

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
}
