/**
 * 
 */
package de.encala.cydonia.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.jme3.scene.Spatial;

import de.encala.cydonia.server.player.ServerPlayer;

/**
 * @author encala
 * 
 */
public abstract class ServerWorldObject {

	private Set<ServerPlayer> marks = Collections
			.synchronizedSet(new HashSet<ServerPlayer>());

	public abstract Spatial getModel();

	public void addMark(ServerPlayer p) {
		marks.add(p);
	}

	public boolean hasMarks() {
		return !marks.isEmpty();
	}

	public boolean hasMark(ServerPlayer p) {
		return marks.contains(p);
	}

	public void removeMark(ServerPlayer p) {
		marks.remove(p);
	}

	public Collection<ServerPlayer> getAllMarks() {
		return new HashSet<ServerPlayer>(marks);
	}

	public void removeAllMarks() {
		marks.clear();
	}
}
