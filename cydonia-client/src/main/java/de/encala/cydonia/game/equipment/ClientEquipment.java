/**
 * 
 */
package de.encala.cydonia.game.equipment;

import java.awt.image.BufferedImage;

import com.jme3.scene.Node;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.player.Player;
import de.encala.cydonia.share.messages.EquipmentInfo;

/**
 * @author encala
 * 
 */
public interface ClientEquipment {

	public String getTypeName();

	public void initGeometry();

	public void usePrimary(boolean activate);

	public void useSecondary(boolean activate);

	public void reset();

	public BufferedImage getHUDImage();

	public void setGameController(GameController gameController);

	public void setPlayer(Player p);

	public void loadInfo(EquipmentInfo info);

	public Node getGeometry();

	public void setActive(boolean active);
}
