/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.PickupEvent;
import de.findus.cydonia.events.PlaceEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PickerInfo;
import de.findus.cydonia.server.GameServer;

/**
 * @author Findus
 *
 */
public class Picker extends AbstractEquipment {

	private static Image[] hudImgs;
	
	private String name;
	
	private float range;
	
	private int capacity;
	
	private List<Flube> repository = new LinkedList<Flube>();
	
	public Picker() {
		
	}
	
	public Picker(String name, float range, int capacity, Player player, WorldController worldController, EventMachine eventMachine) {
		this.name = name;
		this.range = range;
		this.capacity = capacity;
		this.worldController = worldController;
		this.player = player;
		this.eventMachine = eventMachine;
		
		
		try {
			if(hudImgs == null) {
				hudImgs = new Image[3];
				hudImgs[0] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_gold.png"));
				hudImgs[1] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_blue.png"));
				hudImgs[2] = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/inventory_red.png"));
			}
		} catch (IOException e) {
		}
	}
	
	public void usePrimary() {
		if(this.repository.size() < this.capacity) {
			CollisionResult result = worldController.pickWorld(this.player.getEyePosition(), this.player.getViewDir());
			if(result != null && canPickup(this.player, result.getGeometry(), result.getDistance())) {
				Flube m = worldController.getFlube((Long) result.getGeometry().getUserData("id"));
				worldController.detachFlube(m);
				this.repository.add(m);

				PickupEvent pickup = new PickupEvent(this.player.getId(), m.getId(), true);
				eventMachine.fireEvent(pickup);
			}
		}
	}
	
	public void useSecondary() {
		if(this.repository.size() > 0) {
			Flube m = this.repository.get(0);
			if(m != null) {
				CollisionResult result = worldController.pickWorld(this.player.getEyePosition(), this.player.getViewDir());
				if(result != null && result.getDistance() <= this.range && worldController.isPlaceableSurface(result.getGeometry())) {
					Vector3f contactnormal = result.getContactNormal();
					Vector3f contactpos = result.getContactPoint();

					Vector3f loc;
					if(GameServer.FREE_PLACING) {
						loc = contactpos.add(contactnormal.mult(0.5f));
					}else {
						loc = result.getGeometry().getLocalTranslation().add(contactnormal);
					}
					m.getControl().setPhysicsLocation(loc);
					worldController.attachFlube(m);
					this.repository.remove(0);

					PlaceEvent place = new PlaceEvent(this.player.getId(), m.getId(), loc, true);
					eventMachine.fireEvent(place);
				}
			}
		}
	}
	
	private boolean canPickup(Player p, Spatial g, float distance) {
		if(distance <= this.range) {
			if(p != null && g != null) {
				if(worldController.isFlube(g) && g.getUserData("Type") != null) {
					int type = g.getUserData("Type");
					if(type == 0 || type == p.getTeam()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.repository = new LinkedList<Flube>();
	}

	@Override
	public EquipmentInfo getInfo() {
		return new PickerInfo(this);
	}

	@Override
	public void loadInfo(EquipmentInfo info) {
		if(info instanceof PickerInfo) {
			PickerInfo i = (PickerInfo) info;
			this.name = i.getName();
			this.range = i.getRange();
			this.capacity = i.getCapacity();
			this.repository = new LinkedList<Flube>();
			for (Long id : i.getRepository()) {
				this.repository.add(worldController.getFlube(id));
			}
		}
	}

	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35*this.capacity, 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		int imgpos = 0;
		for(Flube f : this.repository) {
			if(f.getType() >= 0) {
				gr.drawImage(hudImgs[f.getType()], imgpos, 0, new Color(0, 0, 0, 0), null);
				imgpos += 35;
			}
		}

		return tmpimg;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the range
	 */
	public float getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the repository
	 */
	public List<Flube> getRepository() {
		return repository;
	}

	/**
	 * @param repository the repository to set
	 */
	public void setRepository(List<Flube> repository) {
		this.repository = repository;
	}
}
