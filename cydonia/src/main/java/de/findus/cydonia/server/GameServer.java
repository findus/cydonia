/**
 * 
 */
package de.findus.cydonia.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;

import de.findus.cydonia.level.Level;
import de.findus.cydonia.level.Level1;
import de.findus.cydonia.main.AttackMessage;

/**
 * @author Findus
 *
 */
public class GameServer extends Application implements MessageListener<HostedConnection>, ConnectionListener, PhysicsCollisionListener {

	
	public static float MAX_STEP_HEIGHT = 0.2f;
	public static float PLAYER_SPEED = 5f;
	public static float PHYSICS_ACCURACY = (1f / 240);
	
	public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));

	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start(JmeContext.Type.Headless);
	}
	
	private Server server;
	
	private Thread senderLoop;
	
	protected Node rootNode = new Node("Root Node");
	
	private RigidBodyControl landscape;
    
    private HashMap<Integer, Player> players;
    
    private HashMap<Long, Bullet> bullets;
    
	private BulletAppState bulletAppState;
    
    private boolean senderRunning;
    
    private ConcurrentLinkedQueue<InputUpdate> updateQueue;
    
    /**
     * Used for moving players.
     * Allocated only once and reused for performance reasons.
     */
    private Vector3f walkdirection = new Vector3f();
	
    @Override
    public void initialize() {
        super.initialize();

        this.players = new HashMap<Integer, Player>();
        this.bullets = new HashMap<Long, Bullet>();
        updateQueue = new ConcurrentLinkedQueue<InputUpdate>();
        
        Bullet.setAssetManager(assetManager);

    	bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        
        Box box1 = new Box( new Vector3f(0,3,0), 1,1,1);
        Geometry blue = new Geometry("Box", box1);
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        rootNode.attachChild(blue);
        
        Level level = new Level1();
        Spatial scene = null;
        //scene = assetManager.loadModel("Scenes/firstworld.j3o");
        scene = level.getScene(assetManager);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
        
        rootNode.attachChild(scene);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        try {
			server = Network.createServer(6173);
			server.start();
			Serializer.registerClass(WorldStateUpdate.class);
			Serializer.registerClass(PlayerPhysic.class);
			Serializer.registerClass(BulletPhysic.class);
			Serializer.registerClass(InputUpdate.class);
			Serializer.registerClass(PlayerInputState.class);
			Serializer.registerClass(AttackMessage.class);
			
			server.addMessageListener(this);
			server.addConnectionListener(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        bulletAppState.setEnabled(true);
		senderLoop = new Thread(new WorldStateSenderLoop());
		senderRunning = true;
		senderLoop.start();
    }
    
    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        stateManager.update(tpf);

        // update game specific things
        updateInputs();
        movePlayers(tpf);
        
        // update world and gui
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();

        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();
    }

	private void updateInputs() {
		while (!updateQueue.isEmpty()) {
			InputUpdate inputUpdate = updateQueue.poll();
			Player p = players.get(inputUpdate.getPlayerId());
			if(p != null) {
				p.setInputState(inputUpdate.getInputs());
				p.getControl().setViewDirection(inputUpdate.getViewDir());
			}
		}
		
	}

	private void movePlayers(float tpf) {
		for (Player p : this.players.values()) {
			Vector3f viewDir = p.getControl().getViewDirection().clone().setY(0).normalizeLocal();
			Vector3f viewLeft = new Vector3f();
			ROTATE90LEFT.transformVector(viewDir, viewLeft);
			
			walkdirection.set(0, 0, 0);
			if(p.getInputState().isLeft()) walkdirection.addLocal(viewLeft);
			if(p.getInputState().isRight()) walkdirection.addLocal(viewLeft.negate());
			if(p.getInputState().isForward()) walkdirection.addLocal(viewDir);
			if(p.getInputState().isBack()) walkdirection.addLocal(viewDir.negate());

			walkdirection.normalizeLocal().multLocal(PHYSICS_ACCURACY * PLAYER_SPEED);

			p.getControl().setWalkDirection(walkdirection);
		}
	}

	@Override
	public void collision(PhysicsCollisionEvent e) {
		Spatial bullet = null;
		Spatial other = null;
		
		try {
    	if(e.getNodeA() != null) {
    		Boolean sticky = e.getNodeA().getUserData("Sticky");
    		if (sticky != null && sticky.booleanValue() == true) {
    			bullet = e.getNodeA();
    			other = e.getNodeB();
			}
		}else if (e.getNodeB() != null) {
			Boolean sticky = e.getNodeB().getUserData("Sticky");
			if (sticky != null && sticky.booleanValue() == true) {
				bullet = e.getNodeB();
				other = e.getNodeA();
			}
		}
    	
    	if(bullet != null && other != null) {
    		rootNode.detachChild(bullet);
    		bulletAppState.getPhysicsSpace().remove(bullet.getControl(RigidBodyControl.class));
    		bullet.removeControl(RigidBodyControl.class);
    		if(other != null) {
    			if (other instanceof Node) {
					((Node) other).attachChild(bullet);
				}else {
					other.getParent().attachChild(bullet);
				}
    		}
    	}
		}catch ( Exception ex) {
			System.out.println("");
		}
	}

	@Override
	public void messageReceived(HostedConnection source, Message m) {
		if (m instanceof InputUpdate) {
    		InputUpdate inputUpdate = (InputUpdate) m;
    		updateQueue.add(inputUpdate);
    	}else if(m instanceof AttackMessage) {
    		Player p = players.get(source.getId());
    		Vector3f pos = p.getControl().getPhysicsLocation();
    		Vector3f dir = p.getControl().getViewDirection();
    		
    		Bullet bul = Bullet.createBullet();
    		bul.getModel().setLocalTranslation(pos.add(dir.normalize()));
        	rootNode.attachChild(bul.getModel());
        	bul.getControl().setPhysicsLocation(pos.add(dir.normalize()));
        	bulletAppState.getPhysicsSpace().add(bul.getControl());
        	bul.getControl().setLinearVelocity(dir.normalize().mult(25));
        	
        	bullets.put(bul.getId(), bul);
    	}
	}

	@Override
	public void connectionAdded(Server server, HostedConnection conn) {
		Player p = new Player(conn.getId(), assetManager);
		players.put(conn.getId(), p);
		bulletAppState.getPhysicsSpace().add(p.getControl());
		p.getControl().setPhysicsLocation(new Vector3f(0, 10, 0));
		rootNode.attachChild(p.getModel());
	}

	@Override
	public void connectionRemoved(Server server, HostedConnection conn) {
		Player p = players.get(conn.getId());
		bulletAppState.getPhysicsSpace().remove(p.getControl());
		players.remove(conn.getId());
		rootNode.detachChild(p.getModel());
	}
	
	/**
	 * This class is used to send the current state of the virtual world to all clients in constant intervals.
	 * @author Findus
	 *
	 */
	private class WorldStateSenderLoop implements Runnable {
		@Override
		public void run() {
			while(senderRunning) {
				WorldStateUpdate m = WorldStateUpdate.getUpdate(players.values(), bullets.values());
				m.setReliable(false);
				
				for (HostedConnection conn : server.getConnections()) {
					conn.send(m);
				}
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
