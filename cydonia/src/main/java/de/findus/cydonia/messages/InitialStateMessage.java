/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.main.GameConfig;

/**
 * @author Findus
 *
 */
@Serializable
public class InitialStateMessage extends AbstractMessage {

	private PlayerInfo[] players;
	
	private MoveableInfo[] moveables;
	
	private FlagInfo[] flags;
	
	private GameConfig config;

	
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

	/**
	 * @return the config
	 */
	public GameConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setconfig(GameConfig config) {
		this.config = config;
	}

	/**
	 * @return the flags
	 */
	public FlagInfo[] getFlags() {
		return flags;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(FlagInfo[] flags) {
		this.flags = flags;
	}
}
