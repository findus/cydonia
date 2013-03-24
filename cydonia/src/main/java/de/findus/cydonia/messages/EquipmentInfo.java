/**
 * 
 */
package de.findus.cydonia.messages;

import com.jme3.network.serializing.Serializable;

/**
 * @author Findus
 *
 */
@Serializable
public interface EquipmentInfo {

	public abstract String getTypeName();
}
