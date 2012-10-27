/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class ViewDirMessage extends AbstractMessage {

	private int playerid;
	
	private Vector3f viewDir;

	public ViewDirMessage() {
		setReliable(false);
	}
	
	/**
	 * @return the playerid
	 */
	public int getPlayerid() {
		return playerid;
	}

	/**
	 * @param playerid the playerid to set
	 */
	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	/**
	 * @return the viewDir
	 */
	public Vector3f getViewDir() {
		return viewDir;
	}

	/**
	 * @param viewDir the viewDir to set
	 */
	public void setViewDir(Vector3f viewDir) {
		this.viewDir = viewDir;
	}
}
