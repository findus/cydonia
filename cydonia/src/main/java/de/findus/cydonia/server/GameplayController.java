/**
 * 
 */
package de.findus.cydonia.server;

import java.util.Timer;
import java.util.TimerTask;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.main.GameConfig;
import de.findus.cydonia.main.GameState;

/**
 * @author Findus
 *
 */
public class GameplayController {
	
	/**
	 * Delay before new round is startet after the last round ended.
	 */
	private static final long RESTARTDELAY = 10;

	private EventMachine eventMachine;
	
	private GameConfig gameConfig;
	
	private Timer timer;
	
	private GameState gameState;
	
	private TimerTask endRoundTask;
	
	private TimerTask restartRoundTask;
	
	private long roundStartTime;
	
	public GameplayController(EventMachine em, GameConfig gameConfig) {
		this.eventMachine = em;
		this.gameConfig = gameConfig;
		
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
		timer.schedule(endRoundTask, gameConfig.getLong("mp_roundtime") * 1000);
		roundStartTime = System.currentTimeMillis();
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
	
	public long getRoundStartTime() {
		return this.roundStartTime;
	}
}
