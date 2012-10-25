/**
 * 
 */
package de.findus.cydonia.player;


/**
 * @author Findus
 *
 */
public enum InputCommand {

	ATTACK("attack"), JUMP("jump"), MOVEFRONT("movefront"), MOVEBACK("moveback"), STRAFELEFT("strafeleft"), STRAFERIGHT("straferight"), JOINGAME("joingame"), QUITGAME("quitgame"),
	SCOREBOARD("scoreboard"), EXIT("exit"), CHOOSETEAM1("chooseteam1"), CHOOSETEAM2("chooseteam2");
	
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
