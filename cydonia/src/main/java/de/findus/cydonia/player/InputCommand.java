/**
 * 
 */
package de.findus.cydonia.player;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Findus
 *
 */
public enum InputCommand {

	PICKUP("pickup"), PLACE("place"), ATTACK("attack"), JUMP("jump"), MOVEFRONT("movefront"), MOVEBACK("moveback"), STRAFELEFT("strafeleft"), STRAFERIGHT("straferight"), JOINGAME("joingame"), QUITGAME("quitgame"),
	SCOREBOARD("scoreboard"), EXIT("exit"), CHOOSETEAM1("chooseteam1"), CHOOSETEAM2("chooseteam2");
	
	public static final Set<InputCommand> forwarded = new HashSet<InputCommand>();
	
	public static final Set<InputCommand> local = new HashSet<InputCommand>();
	
	static {
		forwarded.add(PICKUP);
		forwarded.add(PLACE);
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
	
	private final String code;
	
	private InputCommand(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public static InputCommand parseInputCommand(String string) {
		for (InputCommand command : values()) {
			if(command.getCode().equals(string)) {
				return command;
			}
		}
		return null;
	}
}
