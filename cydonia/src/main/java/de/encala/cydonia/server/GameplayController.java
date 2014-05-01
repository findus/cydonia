/**
 * 
 */
package de.encala.cydonia.server;

import java.util.Timer;
import java.util.TimerTask;

import de.encala.cydonia.server.GameServer.ServerStateListener;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.share.GameConfig;
import de.encala.cydonia.share.events.EventMachine;
import de.encala.cydonia.share.events.RestartRoundEvent;
import de.encala.cydonia.share.events.RoundEndedEvent;

/**
 * @author encala
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

	private ServerGameState serverGameState;

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
		if (endRoundTask != null) {
			endRoundTask.cancel();
		}

		if (gameConfig.getLong("timelimit") > 0) {
			endRoundTask = new TimerTask() {
				@Override
				public void run() {
					endRound(-1, true);
				}
			};
			timer.schedule(endRoundTask,
					gameConfig.getLong("timelimit") * 1000);
		}

		team1score = 0;
		team2score = 0;
		roundStartTime = System.currentTimeMillis();
		serverGameState = ServerGameState.RUNNING;
		RestartRoundEvent start = new RestartRoundEvent(true);
		eventMachine.fireEvent(start);
	}

	public void endRound(int winteam, boolean triggerNewRound) {
		System.out.println("end round...");

		endRoundTask.cancel();

		serverGameState = ServerGameState.ROUNDOVER;
		RoundEndedEvent end = new RoundEndedEvent(winteam, true);
		eventMachine.fireEvent(end);

		if (triggerNewRound) {
			System.out.println("triggering restartRound...");
			if (restartRoundTask != null) {
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
	 * @return the serverGameState
	 */
	public ServerGameState getGameState() {
		return serverGameState;
	}

	public void dispose() {
		timer.cancel();
	}

	public void playerScored(ServerPlayer p) {
		if (p.getTeam() == 1) {
			team1score++;
		} else if (p.getTeam() == 2) {
			team2score++;
		}

		if (gameConfig.getInteger("scorelimit") > 0) {
			if (team1score >= gameConfig.getInteger("scorelimit")) {
				endRound(1, true);
			} else if (team2score >= gameConfig.getInteger("scorelimit")) {
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
