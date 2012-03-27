/**
 * 
 */
package de.findus.cydonia.main;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public class AttackMessage extends AbstractMessage {

	/**
	 * Sets reliable to true.
	 */
	public AttackMessage() {
		setReliable(true);
	}

}
