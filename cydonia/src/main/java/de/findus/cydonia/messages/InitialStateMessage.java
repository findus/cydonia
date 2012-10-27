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

	private PlayerInfo[] infos;

	
	public InitialStateMessage() {
		setReliable(true);
	}
	
	/**
	 * @return the infos
	 */
	public PlayerInfo[] getInfos() {
		return infos;
	}

	/**
	 * @param infos the infos to set
	 */
	public void setInfos(PlayerInfo[] infos) {
		this.infos = infos;
	}
}
