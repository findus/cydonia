/**
 * 
 */
package de.encala.cydonia.share.events;

import de.encala.cydonia.share.messages.FlagInfo;
import de.encala.cydonia.share.messages.MoveableInfo;
import de.encala.cydonia.share.messages.SpawnPointInfo;

/**
 * @author encala
 * 
 */
public class ConnectionInitEvent extends AbstractEvent {

	private String message;

	private String level;

	private java.util.Map<Long, MoveableInfo> flubes;

	private java.util.Map<Integer, SpawnPointInfo> spawnPoints;

	private java.util.Map<Integer, FlagInfo> flags;

	public ConnectionInitEvent() {
		setNetworkEvent(false);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	public java.util.Map<Long, MoveableInfo> getFlubes() {
		return flubes;
	}

	public void setFlubes(java.util.Map<Long, MoveableInfo> flubes) {
		this.flubes = flubes;
	}

	public java.util.Map<Integer, SpawnPointInfo> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(java.util.Map<Integer, SpawnPointInfo> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public java.util.Map<Integer, FlagInfo> getFlags() {
		return flags;
	}

	public void setFlags(java.util.Map<Integer, FlagInfo> flags) {
		this.flags = flags;
	}
}
