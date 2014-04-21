/**
 * 
 */
package de.encala.cydonia.share.events;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class RoundEndedEvent extends AbstractEvent {

	private int winteam;

	public RoundEndedEvent() {
		setNetworkEvent(false);
	}

	public RoundEndedEvent(int winteam, boolean forward) {
		this.setWinteam(winteam);
		setNetworkEvent(forward);
	}

	/**
	 * @return the winteam
	 */
	public int getWinteam() {
		return winteam;
	}

	/**
	 * @param winteam
	 *            the winteam to set
	 */
	public void setWinteam(int winteam) {
		this.winteam = winteam;
	}
}
