/**
 * 
 */
package de.findus.cydonia.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class MarkEvent extends AbstractEvent {

	private int playerId;
	
	private boolean unmark;
	
	private long targetFlubeId;
	private int targetPlayerId;
	
	public MarkEvent() {
		super();
	}

	public MarkEvent(int playerId, boolean unmark, long targetFlubeId, int targetPlayerId, boolean forward) {
		super(forward);
		this.playerId = playerId;
		this.setUnmark(unmark);
		this.targetFlubeId = targetFlubeId;
		this.targetPlayerId = targetPlayerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public boolean isUnmark() {
		return unmark;
	}

	public void setUnmark(boolean unmark) {
		this.unmark = unmark;
	}

	public long getTargetFlubeId() {
		return targetFlubeId;
	}

	public void setTargetFlubeId(long targetFlubeId) {
		this.targetFlubeId = targetFlubeId;
	}

	public int getTargetPlayerId() {
		return targetPlayerId;
	}

	public void setTargetPlayerId(int targetPlayerId) {
		this.targetPlayerId = targetPlayerId;
	}

}
