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
public class SwapEvent extends AbstractEvent {

	private int playerA;
	private int playerB;

	private long flubeA;
	private long flubeB;

	/**
	 * 
	 */
	public SwapEvent() {
		super();
	}

	/**
	 * 
	 * @param playerA
	 * @param playerB
	 * @param flubeA
	 * @param flubeB
	 * @param forward
	 */
	public SwapEvent(int playerA, int playerB, long flubeA, long flubeB,
			boolean forward) {
		super(forward);
		this.setPlayerA(playerA);
		this.setPlayerB(playerB);
		this.setFlubeA(flubeA);
		this.setFlubeB(flubeB);
	}

	public int getPlayerA() {
		return playerA;
	}

	public void setPlayerA(int playerA) {
		this.playerA = playerA;
	}

	public int getPlayerB() {
		return playerB;
	}

	public void setPlayerB(int playerB) {
		this.playerB = playerB;
	}

	public long getFlubeA() {
		return flubeA;
	}

	public void setFlubeA(long flubeA) {
		this.flubeA = flubeA;
	}

	public long getFlubeB() {
		return flubeB;
	}

	public void setFlubeB(long flubeB) {
		this.flubeB = flubeB;
	}

}
