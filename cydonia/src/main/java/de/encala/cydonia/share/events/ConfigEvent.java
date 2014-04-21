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
public class ConfigEvent extends AbstractEvent {

	private String key;

	private Object newValue;

	/**
	 * 
	 */
	public ConfigEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param net
	 */
	public ConfigEvent(String key, Object value, boolean net) {
		super(net);
		this.setKey(key);
		this.setNewValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

}
