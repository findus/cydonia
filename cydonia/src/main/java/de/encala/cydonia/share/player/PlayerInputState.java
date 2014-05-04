/**
 * 
 */
package de.encala.cydonia.share.player;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class PlayerInputState {

	private boolean left, right, forward, back, jump;

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isBack() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}
	
	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean value) {
		this.jump = value;
	}
}
