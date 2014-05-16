/**
 * 
 */
package de.encala.cydonia.server.equipment;

import com.jme3.scene.Node;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.share.messages.EquipmentInfo;

/**
 * @author encala
 * 
 */
public interface ServerEquipment {

	public String getTypeName();

	public void initGeometry();

	public void usePrimary(boolean activate);

	public void useSecondary(boolean activate);

	public void reset();

	public void setGameServer(GameServer gameServer);

	public void setServerPlayer(ServerPlayer p);

	public EquipmentInfo getInfo();

	public Node getGeometry();

	public void setActive(boolean active);
}
