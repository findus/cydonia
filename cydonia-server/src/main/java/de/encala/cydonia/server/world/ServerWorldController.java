/**
 * 
 */
package de.encala.cydonia.server.world;

import java.util.Collection;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.share.player.ForceCharacterControl;

/**
 * This is the central controller for the games virtual world. It loads the
 * scene and administrates the root node.
 * 
 * @author encala
 * 
 */
public class ServerWorldController {

	private static final float FLAGAREARADIUS = 3f;

	/**
	 * The AssetManager.
	 */
	protected AssetManager assetManager;

	protected PhysicsSpace physicsSpace;

	/**
	 * Root Node of the virtual world.
	 */
	protected Node rootNode = new Node("Root Node");

	protected Node worldNode = new Node("World");

	protected Node moveablesNode = new Node("Movables");

	private ServerMap serverMap = new ServerMap("empty");

	/**
	 * Constructs setting up ambient light.
	 */
	public ServerWorldController(AssetManager assetManager, PhysicsSpace physicsSpace) {
		this.assetManager = assetManager;
		this.physicsSpace = physicsSpace;

		rootNode.setShadowMode(ShadowMode.Off);

		rootNode.attachChild(worldNode);
	}

	public void unloadCurrentWorld() {
		for (ServerFlube f : serverMap.getFlubes().values()) {
			detachFlube(f);
		}
		this.serverMap.getFlubes().clear();

		for (ServerFlag serverFlag : serverMap.getFlags().values()) {
			returnFlag(serverFlag);
			this.worldNode.detachChild(serverFlag.getBaseModel());
			this.physicsSpace.removeCollisionObject(serverFlag.getBaseControl());
		}
		this.serverMap.getFlags().clear();

		for (ServerSpawnPoint sp : serverMap.getSpawnPoints().values()) {
			this.worldNode.detachChild(sp.getNode());
		}
		this.serverMap.getSpawnPoints().clear();
	}

	public void loadWorld(ServerMap level) {
		this.serverMap = level;

		for (ServerFlube f : level.getFlubes().values()) {
			f.getControl().setPhysicsLocation(f.getOrigin());
			attachFlube(f);
		}
		for (ServerFlag serverFlag : level.getFlags().values()) {
			this.worldNode.attachChild(serverFlag.getBaseModel());
			this.physicsSpace.addCollisionObject(serverFlag.getBaseControl());
			serverFlag.getBaseModel().attachChild(serverFlag.getModel());
		}

		for (ServerSpawnPoint sp : level.getSpawnPoints().values()) {
			this.worldNode.attachChild(sp.getNode());
		}

		// addflubestoworld();
		// addflubestoworld2();

		// rootNode.attachChild(worldNode);

		// try {
		// String xml = new MapXMLParser(assetManager).writeMap(level);
		// System.out.println(xml);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private void addflubestoworld() {
		long idcounter = 1000;

		int type = -1;
		for (int j = 3; j < 10; j++) {
			for (int i = -5; i <= 5; i++) {
				addFlube(idcounter++, new Vector3f(i, -3f, j), type, true);
				idcounter++;
			}
		}

		for (int j = 15; j < 20; j++) {
			for (int i = -5; i <= 5; i++) {
				addFlube(idcounter++, new Vector3f(i, -3f, j), type, true);
				idcounter++;
			}
		}

		type = 0;
		for (int j = 18; j < 20; j++) {
			for (int i = -5; i <= 5; i++) {
				addFlube(idcounter++, new Vector3f(i, -2f, -j), type, true);
				idcounter++;
			}
		}

		type = 1;
		for (int i = -2; i < 0; i++) {
			addFlube(idcounter++, new Vector3f(i, -2f, 19), type, true);
			idcounter++;
		}

		type = 2;
		for (int i = 1; i <= 2; i++) {
			addFlube(idcounter++, new Vector3f(i, -2f, -19), type, true);
			idcounter++;
		}
	}

	private void addflubestoworld2() {
		long idcounter = 2000;

		int type = -0;
		for (int j = -20; j <= 20; j++) {
			for (int i = -20; i <= 20; i++) {
				addFlube(idcounter++, new Vector3f(i, -2f, j), type, true);
			}
		}
	}

	private void addFlube(long id, Vector3f pos, int type,
			boolean doubleSymmetric) {
		ServerFlube m1 = new ServerFlube(id, pos, type, assetManager);
		this.serverMap.getFlubes().put(m1.getId(), m1);
		m1.getControl().setPhysicsLocation(m1.getOrigin());
		attachFlube(m1);

		if (doubleSymmetric) {
			ServerFlube m2 = new ServerFlube(++id, pos.mult(new Vector3f(1, 1, -1)), type,
					assetManager);
			this.serverMap.getFlubes().put(m2.getId(), m2);
			m2.getControl().setPhysicsLocation(m2.getOrigin());
			attachFlube(m2);
		}
	}

	public ServerFlube addNewFlube(long id, Vector3f origin, int type) {
		ServerFlube serverFlube = new ServerFlube(id, origin, type, assetManager);
		serverFlube.getControl().setPhysicsLocation(origin);
		this.serverMap.getFlubes().put(serverFlube.getId(), serverFlube);
		return serverFlube;
	}

	public void removeFlube(ServerFlube serverFlube) {
		detachFlube(serverFlube);
		this.serverMap.getFlubes().remove(serverFlube.getId());
	}

	public ServerFlag addNewFlag(int id, Vector3f origin, int team) {
		ServerFlag serverFlag = ServerFlagFactory.getInstance().createFlag(id, origin, team);
		this.serverMap.getFlags().put(serverFlag.getId(), serverFlag);
		this.worldNode.attachChild(serverFlag.getBaseModel());
		this.physicsSpace.addCollisionObject(serverFlag.getBaseControl());
		serverFlag.getBaseModel().attachChild(serverFlag.getModel());
		return serverFlag;
	}

	public void removeFlag(ServerFlag serverFlag) {
		detachObject(serverFlag.getBaseModel());
		this.serverMap.getFlags().remove(serverFlag.getId());
	}

	public ServerSpawnPoint addNewSpawnPoint(int id, Vector3f position, int team) {
		ServerSpawnPoint sp = new ServerSpawnPoint(id, position, team, assetManager);
		this.worldNode.attachChild(sp.getNode());
		serverMap.getSpawnPoints().put(sp.getId(), sp);
		return sp;
	}

	public void removeSpawnPoint(ServerSpawnPoint sp) {
		detachObject(sp.getNode());
		serverMap.getSpawnPoints().remove(sp.getId());
	}

	public ServerSpawnPoint getSpawnPointForTeam(int team) {
		for (ServerSpawnPoint sp : serverMap.getSpawnPoints().values()) {
			if (sp.getTeam() == team) {
				return sp;
			}
		}
		return null;
	}

	public boolean isBelowBottomOfPlayground(ServerPlayer p) {
		if (p == null || this.serverMap == null)
			return false;
		if (this.serverMap.getBottomHeight() > p.getControl().getPhysicsLocation()
				.getY()) {
			return true;
		}
		return false;
	}

	public void resetWorld() {
		for (ServerFlube f : serverMap.getFlubes().values()) {
			detachFlube(f);
			f.getControl().setPhysicsLocation(f.getOrigin());
			attachFlube(f);
		}
		for (ServerFlag serverFlag : serverMap.getFlags().values()) {
			returnFlag(serverFlag);
		}
	}

	public void returnFlag(ServerFlag serverFlag) {
		if (serverFlag.getPlayer() != null) {
			serverFlag.getPlayer().setFlag(null);
			serverFlag.setPlayer(null);
		}
		Node parent = serverFlag.getModel().getParent();
		if (parent != null) {
			parent.detachChild(serverFlag.getModel());
		}
		serverFlag.getBaseModel().attachChild(serverFlag.getModel());
		serverFlag.getModel().setLocalTranslation(0, 0, 0);
		serverFlag.setInBase(true);
	}

	/**
	 * Attaches the objectes to the scenes root node.
	 * 
	 * @param obj
	 *            the object to attach
	 */
	public void attachObject(Spatial obj) {
		worldNode.attachChild(obj);
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if (control != null) {
			physicsSpace.addCollisionObject(control);
		}
	}

	/**
	 * Detaches the object from the scenes root node.
	 * 
	 * @param obj
	 *            the object to remove
	 */
	public void detachObject(Spatial obj) {
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if (control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		worldNode.detachChild(obj);
	}

	public void attachPlayer(ServerPlayer player) {
		rootNode.attachChild(player.getNode());
		ForceCharacterControl control = player.getControl();
		if (control != null) {
			physicsSpace.addCollisionObject(control);
			physicsSpace.addTickListener(control);
		}
		GhostControl ghostcontrol = player.getGhostControl();
		if (ghostcontrol != null) {
			physicsSpace.addCollisionObject(ghostcontrol);
		}
	}

	public void detachPlayer(ServerPlayer player) {
		GhostControl ghostcontrol = player.getGhostControl();
		if (ghostcontrol != null) {
			physicsSpace.removeCollisionObject(ghostcontrol);
		}
		ForceCharacterControl control = player.getControl();
		if (control != null) {
			physicsSpace.removeCollisionObject(control);
			physicsSpace.removeTickListener(control);
		}
		rootNode.detachChild(player.getNode());
	}

	public void attachFlube(ServerFlube serverFlube) {
		worldNode.attachChild(serverFlube.getModel());
		physicsSpace.addCollisionObject(serverFlube.getControl());
	}

	public void detachFlube(ServerFlube serverFlube) {
		worldNode.detachChild(serverFlube.getModel());
		physicsSpace.removeCollisionObject(serverFlube.getControl());
	}

	/**
	 * Updates the logical state of the scene. Only called by global physics
	 * update loop.
	 */
	public void updateLogicalState(float tpf) {
		rootNode.updateLogicalState(tpf);
	}

	/**
	 * Updates the geometric state of the scene. Only called by global physics
	 * update loop.
	 */
	public void updateGeometricState() {
		rootNode.updateGeometricState();
	}

	/**
	 * Returns the root node of the world. Called by <code>GameController</code>
	 * to attach the root node to the ViewPort. DO NOT USE OTHER THAN THAT.
	 * 
	 * @return the root node
	 */
	public Node getRootNode() {
		return this.rootNode;
	}

	/**
	 * @return the serverMap
	 */
	public ServerMap getMap() {
		return serverMap;
	}

	public ServerFlube getFlube(long id) {
		return serverMap.getFlubes().get(id);
	}

	public Collection<ServerFlube> getAllFlubes() {
		return serverMap.getFlubes().values();
	}

	public ServerFlag getFlag(int id) {
		return serverMap.getFlags().get(id);
	}

	public Collection<ServerFlag> getAllFlags() {
		return serverMap.getFlags().values();
	}

	public ServerSpawnPoint getSpawnPoint(int id) {
		return serverMap.getSpawnPoints().get(id);
	}

	public Collection<ServerSpawnPoint> getAllSpawnPoints() {
		return serverMap.getSpawnPoints().values();
	}

	public CollisionResult pickWorld(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		System.out.println(ray.getLimit());
		worldNode.collideWith(ray, results);
		return results.getClosestCollision();
	}

	public CollisionResult pickRoot(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		rootNode.collideWith(ray, results);
		return results.getClosestCollision();
	}

	public CollisionResults pickRootList(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		rootNode.collideWith(ray, results);
		return results;
	}

	public boolean isFlube(Spatial obj) {
		return (obj.getName().startsWith("Flube_"));
	}

	public boolean isFlag(Geometry obj) {
		return (obj.getName().startsWith("ServerFlag"));
	}

	public boolean isSpawnPoint(Geometry obj) {
		return (obj.getName().startsWith("ServerSpawnPoint"));
	}

	public boolean isPlaceableSurface(Spatial obj) {
		if (obj.getUserData("Type") != null) {
			int type = obj.getUserData("Type");
			return (type >= -1);
		}
		return false;
	}

	public boolean isSwapableFlube(ServerFlube f) {
		if (f != null) {
			if (f.getType() >= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isInFlagArea(Vector3f loc) {
		for (ServerFlag f : getAllFlags()) {
			if (f.getOrigin().distance(loc) < FLAGAREARADIUS) {
				return true;
			}
		}
		return false;
	}

	public long getFreeFlubeId() {
		for (long i = 1; i < Long.MAX_VALUE; i++) {
			if (!serverMap.getFlubes().containsKey(i)) {
				return i;
			}
		}
		return -1;
	}

	public int getFreeFlagId() {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			if (!serverMap.getFlags().containsKey(i)) {
				return i;
			}
		}
		return -1;
	}

	public int getFreeSpawnPointId() {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			if (!serverMap.getSpawnPoints().containsKey(i)) {
				return i;
			}
		}
		return -1;
	}

	public Vector3f rasterize(Vector3f vector) {
		Vector3f result = new Vector3f();
		result.setX(Math.round(vector.getX()));
		result.setY(Math.round(vector.getY()));
		result.setZ(Math.round(vector.getZ()));
		return result;
	}
}
