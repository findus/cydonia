/**
 * 
 */
package de.findus.cydonia.server;

import java.util.Timer;
import java.util.TimerTask;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.main.GameState;

/**
 * @author Findus
 *
 */
public class GameplayController {

	/**
	 * Duration of one round in seconds.
	 */
	private static final long ROUNDTIME = 3 * 60;
	
	/**
	 * Delay before new round is startet after the last round ended.
	 */
	private static final long RESTARTDELAY = 10;

	private EventMachine eventMachine;
	
	private Timer timer;
	
	private GameState gameState;
	
	private TimerTask endRoundTask;
	
	private TimerTask restartRoundTask;
	
	public GameplayController(EventMachine em) {
		eventMachine = em;
		timer = new Timer();
	}
	
	public void restartRound() {
		System.out.println("restart round...");
		if(endRoundTask != null) {
			endRoundTask.cancel();
		}
		endRoundTask = new TimerTask() {
			@Override
			public void run() {
				endRound(-1, true);
			}
		};
		timer.schedule(endRoundTask, ROUNDTIME * 1000);
		gameState = GameState.RUNNING;
		RestartRoundEvent start = new RestartRoundEvent(true);
		eventMachine.fireEvent(start);
	}
	
	public void endRound(int winnerid, boolean triggerNewRound) {
		System.out.println("end round...");
		
		endRoundTask.cancel();
		
		gameState = GameState.ROUNDOVER;
		RoundEndedEvent end = new RoundEndedEvent(winnerid, true);
		eventMachine.fireEvent(end);
		
		if(triggerNewRound) {
			System.out.println("triggering restartRound...");
			if(restartRoundTask != null) {
				restartRoundTask.cancel();
			}
			restartRoundTask = new TimerTask() {
				@Override
				public void run() {
					restartRound();
				}
			};
			timer.schedule(restartRoundTask, RESTARTDELAY * 1000);
		}
	}

	/**
	 * @return the gameState
	 */
	public GameState getGameState() {
		return gameState;
	}
	
	public void dispose() {
		timer.cancel();
	}

	public void targetReached(int playerid) {
		if(gameState == GameState.RUNNING) {
			this.endRound(playerid, true);
		}
	}
}
