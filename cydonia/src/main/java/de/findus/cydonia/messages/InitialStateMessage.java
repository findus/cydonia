/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class InitialStateMessage extends AbstractMessage {

	private PlayerInfo[] players;
	
	private MoveableInfo[] moveables;

	
	public InitialStateMessage() {
		setReliable(true);
	}
	
	/**
	 * @return the players
	 */
	public PlayerInfo[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(PlayerInfo[] infos) {
		this.players = infos;
	}

	/**
	 * @return the moveablesNode
	 */
	public MoveableInfo[] getMoveables() {
		return moveables;
	}

	/**
	 * @param moveablesNode the moveablesNode to set
	 */
	public void setMoveables(MoveableInfo[] moveables) {
		this.moveables = moveables;
	}
}
