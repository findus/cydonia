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
import de.findus.cydonia.player.Player;

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
	
	private int team1score;
	private int team2score;
	
	public GameplayController(EventMachine em, GameConfig gameConfig) {
		this.eventMachine = em;
		this.gameConfig = gameConfig;
		
		timer = new Timer();
		
		team1score = 0;
		team2score = 0;
	}
	
	public void restartRound() {
		System.out.println("restart round...");
		if(endRoundTask != null) {
			endRoundTask.cancel();
		}
		
		if(gameConfig.getLong("mp_timelimit") > 0) {
			endRoundTask = new TimerTask() {
				@Override
				public void run() {
					endRound(-1, true);
				}
			};
			timer.schedule(endRoundTask, gameConfig.getLong("mp_timelimit") * 1000);
		}
		
		team1score = 0;
		team2score = 0;
		roundStartTime = System.currentTimeMillis();
		gameState = GameState.RUNNING;
		RestartRoundEvent start = new RestartRoundEvent(true);
		eventMachine.fireEvent(start);
	}
	
	public void endRound(int winteam, boolean triggerNewRound) {
		System.out.println("end round...");
		
		endRoundTask.cancel();
		
		gameState = GameState.ROUNDOVER;
		RoundEndedEvent end = new RoundEndedEvent(winteam, true);
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

	public void playerScored(Player p) {
		if(p.getTeam() == 1) {
			team1score++;
		}else if(p.getTeam() == 2) {
			team2score++;
		}
		
		if(gameConfig.getInteger("mp_scorelimit") > 0) {
			if(team1score >= gameConfig.getInteger("mp_scorelimit")) {
				endRound(1, true);
			}else if(team2score >= gameConfig.getInteger("mp_scorelimit")) {
				endRound(2, true);
			}
		}
	}
	
	public long getRoundStartTime() {
		return this.roundStartTime;
	}
	
	public int getTeam1score() {
		return team1score;
	}
	
	public int getTeam2score() {
		return team2score;
	}
}
