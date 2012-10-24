/**
 * 
 */
package de.findus.cydonia.server;

import java.util.Timer;
import java.util.TimerTask;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.RoundEndedEvent;

/**
 * @author Findus
 *
 */
public class GameplayController {

	/**
	 * Duration of one round in seconds.
	 */
	private static final long ROUNDTIME = 1 * 60;

	private EventMachine eventMachine;
	
	private Timer timer;
	
	public GameplayController(EventMachine em) {
		eventMachine = em;
		timer = new Timer();
	}
	
	public void startRound() {
		TimerTask endRound = new TimerTask() {
			
			@Override
			public void run() {
				RoundEndedEvent end = new RoundEndedEvent();
				eventMachine.fireEvent(end);
			}
		};
		timer.schedule(endRound, ROUNDTIME * 1000);
		
		RoundStartetEvent start = new RoundStartetEvent();
		eventMachine.fireEvent(start);
	}
}
