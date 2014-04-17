/**
 * 
 */
package de.encala.cydonia.messages;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public interface EquipmentInfo {

	public abstract String getTypeName();
}
