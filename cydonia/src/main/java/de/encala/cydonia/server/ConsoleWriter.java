/**
 * 
 */
package de.encala.cydonia.server;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author encala
 * 
 */
public class ConsoleWriter {

	private static ConsoleWriter instance;

	public static ConsoleWriter getWriter() {
		if (instance == null) {
			instance = new ConsoleWriter();
		}
		return instance;
	}

	private Collection<Console> listeners;

	/**
	 * 
	 */
	public ConsoleWriter() {
		this.listeners = new HashSet<Console>();
	}

	public void addConsole(Console con) {
		this.listeners.add(con);
	}

	public void removeConsole(Console con) {
		this.listeners.remove(con);
	}

	public void writeLine(String line) {
		for (Console con : this.listeners) {
			con.writeLine(line);
		}
	}

}
