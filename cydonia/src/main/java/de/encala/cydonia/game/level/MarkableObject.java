/**
 * 
 */
package de.encala.cydonia.game.level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.encala.cydonia.game.player.Player;

/**
 * @author encala
 * 
 */
public abstract class MarkableObject {

	private Set<Player> marks = Collections
			.synchronizedSet(new HashSet<Player>());

	public void addMark(Player p) {
		marks.add(p);
	}

	public boolean hasMarks() {
		return !marks.isEmpty();
	}

	public boolean hasMark(Player p) {
		return marks.contains(p);
	}

	public void removeMark(Player p) {
		marks.remove(p);
	}

	public Collection<Player> getAllMarks() {
		return new HashSet<Player>(marks);
	}

	public void removeAllMarks() {
		marks.clear();
	}
	
	public abstract void setHighlighted(boolean highlighted);
}
