/**
 * 
 */
package de.encala.cydonia.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class FlubeStatePartMessage extends AbstractMessage {

	private int number;

	private MoveableInfo[] flubes;

	/**
	 * 
	 */
	public FlubeStatePartMessage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param reliable
	 */
	public FlubeStatePartMessage(int number, MoveableInfo[] flubes,
			boolean reliable) {
		super(reliable);
		this.number = number;
		this.flubes = flubes;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public MoveableInfo[] getFlubes() {
		return flubes;
	}

	public void setFlubes(MoveableInfo[] flubes) {
		this.flubes = flubes;
	}

}
