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
public class BeamEvent extends AbstractEvent {

	private int sourceid;

	private int targetid;

	/**
	 * 
	 */
	public BeamEvent() {
		super();
	}

	/**
	 * 
	 * @param sourceid
	 * @param moveableid
	 * @param forward
	 */
	public BeamEvent(int sourceid, int targetid, boolean forward) {
		super(forward);
		this.setSourceid(sourceid);
		this.setTargetid(targetid);
	}

	/**
	 * @return the sourceid
	 */
	public int getSourceid() {
		return sourceid;
	}

	/**
	 * @param sourceid
	 *            the sourceid to set
	 */
	public void setSourceid(int sourceid) {
		this.sourceid = sourceid;
	}

	/**
	 * @return the targetid
	 */
	public int getTargetid() {
		return targetid;
	}

	/**
	 * @param targetid
	 *            the targetid to set
	 */
	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}

}
