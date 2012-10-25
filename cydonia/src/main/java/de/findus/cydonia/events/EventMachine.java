/**
 * 
 */
package de.findus.cydonia.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The EventMachine handles all events in the Application.
 * One can fire new events and register as listener.
 * Handling is done threaded.
 * 
 * @author Findus
 *
 */
public class EventMachine {

	private ConcurrentHashMap<EventListener, Forwarder> handlers;
	
	/**
	 * 
	 */
	public EventMachine() {
		handlers = new ConcurrentHashMap<EventListener, EventMachine.Forwarder>();
	}
	
	public void fireEvent(Event ev) {
		if(ev == null) return;
		
		for (Forwarder f : handlers.values()) {
			f.addEvent(ev);
		}
	}
	
	public void registerListener(EventListener listener) {
		Forwarder f = new Forwarder(listener);
		handlers.put(listener, f);
		f.start();
	}
	
	private class Forwarder extends Thread {

		private EventListener listener;
		
		private ConcurrentLinkedQueue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();

		private Forwarder(EventListener l) {
			this.listener = l;
		}
		
		private void addEvent(Event e) {
			eventQueue.add(e);
			synchronized (this) {
				this.notify();
			}
		}
		
		@Override
		public void run() {
			Event e = null;
			while(!Thread.interrupted()) {
				while ((e = eventQueue.poll()) != null) {
					listener.newEvent(e);
				}
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException e1) {
				}
			}
		}

	}

}
