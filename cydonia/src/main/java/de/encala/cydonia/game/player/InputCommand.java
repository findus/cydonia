/**
 * 
 */
package de.encala.cydonia.game.player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author encala
 * 
 */
public enum InputCommand {

	USEPRIMARY("useprimary"), USESECONDARY("usesecondary"), SWITCHEQUIP(
			"switchequip"), SWITCHEQUIPUP("switchequipup"), SWITCHEQUIPDOWN(
			"switchequipdown"), ATTACK("attack"), JUMP("jump"), MOVEFRONT(
			"movefront"), MOVEBACK("moveback"), STRAFELEFT("strafeleft"), STRAFERIGHT(
			"straferight"), JOINGAME("joingame"), QUITGAME("quitgame"), SCOREBOARD(
			"scoreboard"), EXIT("exit"), CHOOSETEAM1("chooseteam1"), CHOOSETEAM2(
			"chooseteam2"), FPS("fps"), HUD("hud"), CROSSHAIR("crosshair");

	public static final Set<InputCommand> forwarded = new HashSet<InputCommand>();

	public static final Set<InputCommand> usedirect = new HashSet<InputCommand>();

	static {
		forwarded.add(USEPRIMARY);
		forwarded.add(USESECONDARY);
		forwarded.add(SWITCHEQUIP);
		forwarded.add(ATTACK);
		forwarded.add(JUMP);
		forwarded.add(MOVEFRONT);
		forwarded.add(MOVEBACK);
		forwarded.add(STRAFELEFT);
		forwarded.add(STRAFERIGHT);
		forwarded.add(JOINGAME);
		forwarded.add(QUITGAME);
		forwarded.add(CHOOSETEAM1);
		forwarded.add(CHOOSETEAM2);
	}

	static {
		usedirect.add(MOVEFRONT);
		usedirect.add(MOVEBACK);
		usedirect.add(STRAFELEFT);
		usedirect.add(STRAFERIGHT);
		usedirect.add(JUMP);
	}

	private final String code;

	private InputCommand(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public static InputCommand parseInputCommand(String string) {
		for (InputCommand command : values()) {
			if (command.getCode().equals(string)) {
				return command;
			}
		}
		return null;
	}
}
