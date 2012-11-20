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
public class RoundEndedEvent extends AbstractEvent {

	private int winnerid;
	
	public RoundEndedEvent() {
		setNetworkEvent(false);
	}
	
	public RoundEndedEvent(int winnerid, boolean forward) {
		this.setWinnerid(winnerid);
		setNetworkEvent(forward);
	}

	/**
	 * @return the winnerid
	 */
	public int getWinnerid() {
		return winnerid;
	}

	/**
	 * @param winnerid the winnerid to set
	 */
	public void setWinnerid(int winnerid) {
		this.winnerid = winnerid;
	}
}
