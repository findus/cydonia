/**
 * 
 */
package de.findus.cydonia.level;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import de.findus.cydonia.player.Player;

/**
 * This is the central controller for the games virtual world.
 * It loads the scene and administrates the root node.
 * @author Findus
 *
 */
public class WorldController {
	
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
	
	protected ConcurrentHashMap<Long, Flube> flubes;
	
	protected ConcurrentHashMap<Integer, Flag> flags;
	
	private Map map;
	
	/**
	 * Constructs setting up ambient light.
	 */
	public WorldController(AssetManager assetManager, PhysicsSpace physicsSpace) {
		this.assetManager = assetManager;
		this.physicsSpace = physicsSpace;
		
		setUpAmbientLight();
		
		this.flubes = new ConcurrentHashMap<Long, Flube>();
		this.flags = new ConcurrentHashMap<Integer, Flag>();
	}

	public void loadWorld(Map level) {
		this.map = level;
		
//		Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false);
//		rootNode.attachChild(sky);

		for(Flube f : level.getFlubes()) {
			this.flubes.put(f.getId(), f);
			attachFlube(f);
		}
		for(Flag flag : level.getFlags()) {
			this.flags.put(flag.getId(), flag);
			this.worldNode.attachChild(flag.getBaseModel());
			this.physicsSpace.addCollisionObject(flag.getBaseControl());
			flag.getBaseModel().attachChild(flag.getModel());
		}
		
//		addflubestoworld();
//		addflubestoworld2();

		rootNode.attachChild(worldNode);
		
//		try {
//			String xml = new MapXMLParser(assetManager).writeMap(level);
//			System.out.println(xml);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	
	private void addFlube(long id, Vector3f pos, int type, boolean doubleSymmetric) {
		Flube m1 = new Flube(id, pos, type, assetManager);
		this.flubes.put(m1.getId(), m1);
		map.getFlubes().add(m1);
		m1.getControl().setPhysicsLocation(m1.getOrigin());
		attachFlube(m1);
		
		if(doubleSymmetric) {
			Flube m2 = new Flube(++id, pos.mult(new Vector3f(1, 1, -1)), type, assetManager);
			this.flubes.put(m2.getId(), m2);
			map.getFlubes().add(m2);
			m2.getControl().setPhysicsLocation(m2.getOrigin());
			attachFlube(m2);
		}
	}
	
	public SpawnPoint getSpawnPoint(int team) {
		for(SpawnPoint sp : map.getSpawnPoints()) {
			if(sp.getTeam() == team) {
				return sp;
			}
		}
		return null;
	}
	
	public void resetWorld() {
		for (Flube f : flubes.values()) {
			detachFlube(f);
			f.getControl().setPhysicsLocation(f.getOrigin());
			attachFlube(f);
		}
		for(Flag flag : flags.values()) {
			returnFlag(flag);
		}
	}
	
	public void returnFlag(Flag flag) {
		if(flag.getPlayer() != null) {
			flag.getPlayer().setFlag(null);
			flag.setPlayer(null);
		}
		Node parent = flag.getModel().getParent();
		if(parent != null) {
			parent.detachChild(flag.getModel());
		}
		flag.getModel().setLocalScale(1);
		flag.getBaseModel().attachChild(flag.getModel());
		flag.getModel().setLocalTranslation(0, 0, 0);
		flag.setInBase(true);
	}
	
	/**
	 * Attaches the objectes to the scenes root node.
	 * @param obj the object to attach
	 */
	public void attachObject(Spatial obj) {
		worldNode.attachChild(obj);
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.addCollisionObject(control);
		}
	}
	
	/**
	 * Detaches the object from the scenes root node.
	 * @param obj the object to remove
	 */
	public void detachObject(Spatial obj) {
		RigidBodyControl control = obj.getControl(RigidBodyControl.class);
		if(control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		worldNode.detachChild(obj);
	}
	
	public void attachPlayer(Player player) {
		rootNode.attachChild(player.getModel());
		CharacterControl control = player.getControl();
		if(control != null) {
			physicsSpace.addCollisionObject(control);
		}
		GhostControl ghostcontrol = player.getGhostControl();
		if(ghostcontrol != null) {
			physicsSpace.addCollisionObject(ghostcontrol);
		}
	}
	
	public void detachPlayer(Player player) {
		GhostControl ghostcontrol = player.getGhostControl();
		if(ghostcontrol != null) {
			physicsSpace.removeCollisionObject(ghostcontrol);
		}
		CharacterControl control = player.getControl();
		if(control != null) {
			physicsSpace.removeCollisionObject(control);
		}
		rootNode.detachChild(player.getModel());
	}
	
	public void attachFlube(Flube flube) {
		worldNode.attachChild(flube.getModel());
		physicsSpace.addCollisionObject(flube.getControl());
	}
	
	public void detachFlube(Flube flube) {
		worldNode.detachChild(flube.getModel());
		physicsSpace.removeCollisionObject(flube.getControl());
	}
	
	/**
	 * Updates the logical state of the scene.
	 * Only called by global physics update loop.
	 */
	public void updateLogicalState(float tpf) {
		rootNode.updateLogicalState(tpf);
	}
	
	/**
	 * Updates the geometric state of the scene.
	 * Only called by global physics update loop.
	 */
	public void updateGeometricState() {
		rootNode.updateGeometricState();
	}
	
	/**
	 * Returns the root node of the world.
	 * Called by <code>GameController</code> to attach the root node to the ViewPort.
	 * DO NOT USE OTHER THAN THAT.
	 * @return the root node 
	 */
	public Node getRootNode() {
		return this.rootNode;
	}
	
	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}

	private void setUpAmbientLight() {
		rootNode.setShadowMode(ShadowMode.Off);
		
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
//        rootNode.addLight(al);
        
        DirectionalLight dl1 = new DirectionalLight();
        dl1.setColor(ColorRGBA.White.mult(0.8f));
        dl1.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
//        rootNode.addLight(dl1);
        
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White.mult(0.8f));
        dl2.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
//        rootNode.addLight(dl2);
        
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        PointLight pl1 = new PointLight();
        pl1.setColor(ColorRGBA.White.mult(0.3f));
        pl1.setPosition(new Vector3f(0, -3, 12));
        pl1.setRadius(30);
        rootNode.addLight(pl1);
        
        Geometry g1 = new Geometry("pl1-g", new Box(pl1.getPosition(), 0.05f, 0.05f, 0.05f));
        g1.setMaterial(mat);
        rootNode.attachChild(g1);
        
        PointLight pl2 = new PointLight();
        pl2.setColor(ColorRGBA.White.mult(0.3f));
        pl2.setPosition(new Vector3f(0, -3, -12));
        pl2.setRadius(30);
        rootNode.addLight(pl2);
        
        Geometry g2 = new Geometry("pl2-g", new Box(pl2.getPosition(), 0.05f, 0.05f, 0.05f));
        g2.setMaterial(mat);
        rootNode.attachChild(g2);
        
        PointLight pl3 = new PointLight();
        pl3.setColor(ColorRGBA.White.mult(0.3f));
        pl3.setPosition(new Vector3f(10, 10, 0));
        pl3.setRadius(50);
        rootNode.addLight(pl3);
        
        Geometry g3 = new Geometry("pl3-g", new Box(pl3.getPosition(), 0.05f, 0.05f, 0.05f));
        g3.setMaterial(mat);
        rootNode.attachChild(g3);
        
        PointLight pl4 = new PointLight();
        pl4.setColor(ColorRGBA.White.mult(0.3f));
        pl4.setPosition(new Vector3f(-10, 10, 0));
        pl4.setRadius(50);
        rootNode.addLight(pl4);
        
        Geometry g4 = new Geometry("pl4-g", new Box(pl4.getPosition(), 0.05f, 0.05f, 0.05f));
        g4.setMaterial(mat);
        rootNode.attachChild(g4);
        
        PointLight pl5 = new PointLight();
        pl5.setColor(ColorRGBA.White.mult(0.3f));
        pl5.setPosition(new Vector3f(0, -10, 0));
        pl5.setRadius(100);
//        rootNode.addLight(pl4);
        
        Geometry g5 = new Geometry("pl5-g", new Box(pl5.getPosition(), 0.05f, 0.05f, 0.05f));
        g5.setMaterial(mat);
        rootNode.attachChild(g5);
    }
	
	public LightList getLights() {
		return rootNode.getLocalLightList();
	}
	
	public Flube getFlube(long id) {
		return flubes.get(id);
	}
	
	public Collection<Flube> getAllFlubes() {
		return flubes.values();
	}
	
	public Flag getFlag(int id) {
		return flags.get(id);
	}
	
	public Collection<Flag> getAllFlags() {
		return flags.values();
	}
	
	public CollisionResult pickWorld(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		worldNode.collideWith(ray, results);
		return results.getClosestCollision();
	}
	
	public CollisionResult pickRoot(Vector3f source, Vector3f direction) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(source, direction);
		rootNode.collideWith(ray, results);
		return results.getClosestCollision();
	}
	
	public boolean isFlube(Spatial obj) {
		return (obj.getName().startsWith("Flube_"));
	}
	
	public boolean isPlaceableSurface(Spatial obj) {
		if(obj.getUserData("Type") != null) {
			int type = obj.getUserData("Type");
			return (type >= -1);
		}
		return false;
	}

}
