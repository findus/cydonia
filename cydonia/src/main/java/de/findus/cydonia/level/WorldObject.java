/**
 * 
 */
package de.findus.cydonia.level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.jme3.scene.Spatial;

import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public abstract class WorldObject {
	
	private Set<Player> marks = Collections.synchronizedSet(new HashSet<Player>());
	
	public abstract Spatial getModel();
	
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
}
