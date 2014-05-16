/**
 * 
 */
package de.encala.cydonia.server.equipment;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.server.world.ServerFlag;
import de.encala.cydonia.server.world.ServerFlube;
import de.encala.cydonia.server.world.ServerSpawnPoint;
import de.encala.cydonia.share.events.AddEvent;
import de.encala.cydonia.share.events.RemoveEvent;
import de.encala.cydonia.share.messages.EditorInfo;
import de.encala.cydonia.share.messages.EquipmentInfo;

/**
 * @author encala
 * 
 */
public class ServerEditor extends AbstractServerEquipment {

	static final String TYPENAME = "Editor";
	
	private String name;

	private int objectSpec;

	private String objectType;

	private float range;
	
	/**
	 * 
	 */
	public ServerEditor() {
		
	}

	/**
	 * @param name
	 * @param range
	 * @param capacity
	 * @param player
	 * @param gameServer
	 */
	public ServerEditor(String name, float range, String objectType,
			int objectSpec, ServerPlayer player, GameServer gameServer) {
		super(player, gameServer);
		
		this.name = name;
		this.range = range;
		this.objectType = objectType;
	}

	@Override
	public void usePrimary(boolean activate) {
		if (!activate)
			return;

		CollisionResult result = getGameServer().getWorldController()
				.pickWorld(this.player.getEyePosition(),
						this.player.getViewDir());
		if (result != null
				&& result.getDistance() <= this.getRange()
				&& getGameServer().getWorldController().isFlube(
						result.getGeometry())) {
			Vector3f contactnormal = result.getContactNormal();
			Vector3f contactpos = result.getContactPoint();

			Vector3f loc;
			if (GameServer.FREE_PLACING) {
				loc = contactpos.add(contactnormal.mult(0.5f));
			} else {
				loc = result.getGeometry().getLocalTranslation()
						.add(contactnormal);
			}
			if ("flag".equalsIgnoreCase(getObjectType())) {
				loc.addLocal(new Vector3f(0, 0.5f, 0));
			} else if ("spawnpoint".equalsIgnoreCase(getObjectType())) {
				loc.addLocal(new Vector3f(0, 0.5f, 0));
			}

			long id = -1;
			if ("flube".equalsIgnoreCase(getObjectType())) {
				id = getGameServer().getWorldController().getFreeFlubeId();
			} else if ("flag".equalsIgnoreCase(getObjectType())) {
				id = getGameServer().getWorldController().getFreeFlagId();
			} else if ("spawnpoint".equalsIgnoreCase(getObjectType())) {
				id = getGameServer().getWorldController()
						.getFreeSpawnPointId();
			}

			if (id < 0) {
				System.out.println("no free id found");
			} else {
				AddEvent add = new AddEvent(this.player.getId(), id,
						this.getObjectType(), this.getObjectSpec(), loc, true);
				getGameServer().getEventMachine().fireEvent(add);
			}
		}
	}

	@Override
	public void useSecondary(boolean activate) {
		if (!activate)
			return;

		CollisionResult result = getGameServer().getWorldController()
				.pickWorld(this.player.getEyePosition(),
						this.player.getViewDir());
		if (result != null && result.getDistance() <= this.getRange()) {
			if (getGameServer().getWorldController().isFlube(
					result.getGeometry())) {
				ServerFlube m = getGameServer().getWorldController().getFlube(
						(Long) result.getGeometry().getUserData("id"));
				RemoveEvent remove = new RemoveEvent(this.player.getId(),
						m.getId(), "flube", true);
				getGameServer().getEventMachine().fireEvent(remove);
			} else if (getGameServer().getWorldController().isFlag(
					result.getGeometry())) {
				ServerFlag f = getGameServer().getWorldController().getFlag(
						(Integer) result.getGeometry().getUserData("id"));
				RemoveEvent remove = new RemoveEvent(this.player.getId(),
						f.getId(), "flag", true);
				getGameServer().getEventMachine().fireEvent(remove);
			} else if (getGameServer().getWorldController().isSpawnPoint(
					result.getGeometry())) {
				ServerSpawnPoint sp = getGameServer().getWorldController()
						.getSpawnPoint(
								(Integer) result.getGeometry()
										.getUserData("id"));
				RemoveEvent remove = new RemoveEvent(this.player.getId(),
						sp.getId(), "spawnpoint", true);
				getGameServer().getEventMachine().fireEvent(remove);
			}

		}
	}

	@Override
	public String getTypeName() {
		return TYPENAME;
	}

	@Override
	public void initGeometry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EquipmentInfo getInfo() {
		return new EditorInfo(this.name, this.range, this.objectType, this.objectSpec, this.player.getId(), TYPENAME);
	}

	@Override
	public Node getGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param range
	 *            the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public int getObjectSpec() {
		return objectSpec;
	}

	public void setObjectSpec(int objectSpec) {
		this.objectSpec = objectSpec;
	}

}
