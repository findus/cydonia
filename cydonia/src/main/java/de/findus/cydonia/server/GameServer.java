/**
 * 
 */
package de.findus.cydonia.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;

import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.events.AttackEvent;
import de.findus.cydonia.events.ChooseTeamEvent;
import de.findus.cydonia.events.ConnectionAddedEvent;
import de.findus.cydonia.events.ConnectionRemovedEvent;
import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.EventListener;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.HitEvent;
import de.findus.cydonia.events.InputEvent;
import de.findus.cydonia.events.JumpEvent;
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.level.Level;
import de.findus.cydonia.level.Level2;
import de.findus.cydonia.level.Level3;
import de.findus.cydonia.main.GameState;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.WorldStateUpdatedMessage;
import de.findus.cydonia.player.InputCommand;
import de.findus.cydonia.player.Player;

/**
 * @author Findus
 *
 */
public class GameServer extends Application implements EventListener, PhysicsCollisionListener {
	
	public static float PLAYER_SPEED = 5f;
	public static float PHYSICS_ACCURACY = (1f / 192);
	
	private static final int RELOAD_TIME = 500;
	
	public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));

	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
	
	private Thread senderLoop;
	
	protected Node rootNode = new Node("Root Node");
	
	private RigidBodyControl landscape;
    
    private ConcurrentHashMap<Integer, Player> players;
    
    private ConcurrentHashMap<Long, Bullet> bullets;
    
	private BulletAppState bulletAppState;
    
    private Level level;
    
    private GameplayController gameplayController;
    
    /**
     * Used for moving players.
     * Allocated only once and reused for performance reasons.
     */
    private Vector3f walkdirection = new Vector3f();
    
	private NetworkController networkController;
	
	private EventMachine eventMachine;
	
	private ConcurrentLinkedQueue<Event> eventQueue;
	
	@Override
	public void start() {
		super.start(JmeContext.Type.Headless);
	}
	
	@Override
	public void stop() {
		cleanup();
		super.stop();
	}
	
	@Override
	public void stop(boolean waitfor) {
		cleanup();
		super.stop(waitfor);
	}
	
	private void cleanup() {
		networkController.stop();
		bulletAppState.setEnabled(false);
		senderLoop.interrupt();
		gameplayController.dispose();
	}
	
    @Override
    public void initialize() {
        super.initialize();

        eventQueue = new ConcurrentLinkedQueue<Event>();
        
        eventMachine = new EventMachine();
        
        this.players = new ConcurrentHashMap<Integer, Player>();
        this.bullets = new ConcurrentHashMap<Long, Bullet>();
        
        Bullet.setAssetManager(assetManager);

    	bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        level = new Level3();
        Spatial scene = null;
        //scene = assetManager.loadModel("Scenes/firstworld.j3o");
        scene = level.getScene(assetManager);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
        
        rootNode.attachChild(scene);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        Bullet.preloadTextures();
        
        eventMachine.registerListener(this);
        
        networkController = new NetworkController(this, eventMachine);
		
        bulletAppState.setEnabled(true);
		senderLoop = new Thread(new WorldStateSenderLoop());
		senderLoop.start();
		
		gameplayController = new GameplayController(eventMachine);
		gameplayController.restartRound();
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
        handleEvents();
        movePlayers(tpf);
        
        // update world and gui
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();

        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();
    }
    
    private void handleEvents() {
    	Event e = null;
    	int size = eventQueue.size();
    	for (int i = 0; i < size; i++) {
    		e = eventQueue.poll();
    		if(e instanceof ConnectionAddedEvent) {
    			connectionAdded(((ConnectionAddedEvent) e).getClientid());
    		}else if(e instanceof ConnectionRemovedEvent) {
    			connectionRemoved(((ConnectionRemovedEvent) e).getClientid());
    		}else if (e instanceof InputEvent) {
    			InputEvent input = (InputEvent) e;
    			handlePlayerInput(input.getPlayerid(), input.getCommand(), input.isValue());
    		}else if (e instanceof RestartRoundEvent) {
				for (Player p : players.values()) {
					if(p.isAlive()) {
						killPlayer(p);
					}
					p.setKills(0);
					p.setDeaths(0);
				}
				removeAllBullets();
			}
    	}
    }

	@Override
	public void newEvent(Event e) {
		if(e instanceof PlayerJoinEvent) {
			System.out.println("test");
		}
		eventQueue.offer(e);
	}

	private void movePlayers(float tpf) {
		if(gameplayController.getGameState() != GameState.RUNNING) {
			return;
		}
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
	
	private void removeAllBullets() {
		for (Bullet b : bullets.values()) {
			removeBullet(b);
		}
	}
	
	private void removeBullet(Bullet b) {
		b.getModel().removeFromParent();
		bullets.remove(b.getId());
	}

	@Override
	public void collision(PhysicsCollisionEvent e) {
		Spatial bullet = null;
		Spatial other = null;

		if(e.getNodeA() != null) {
			Boolean sticky = e.getNodeA().getUserData("Sticky");
			if (sticky != null && sticky.booleanValue() == true) {
				bullet = e.getNodeA();
				other = e.getNodeB();
			}
		}
		if (e.getNodeB() != null) {
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
			if(other.getName().startsWith("player") && bullet.getName().startsWith("bullet")) {
				int victimid = Integer.parseInt(other.getName().substring(6));
				long bulletid = Long.parseLong(bullet.getName().substring(6));
				Bullet bul = bullets.get(bulletid);
				this.hitPlayer(bul.getPlayerid(), victimid, 20);
			}else {
				if(other != null) {
					if (other instanceof Node) {
						((Node) other).attachChild(bullet);
					}else {
						other.getParent().attachChild(bullet);
					}
				}
			}
		}
	}
	
	private void hitPlayer(int sourceid, int victimid, double hitpoints) {
		Player victim = players.get(victimid);
		Player attacker = players.get(sourceid);
		if(victim == null) {
			System.out.println("cannot prozess hit. player not available.");
			return;
		}
		if(attacker == null || victim.getTeam() != attacker.getTeam()) {
			double hp = victim.getHealthpoints();
			hp -= hitpoints;
			if(hp <= 0) {
				hp = 0;
				this.killPlayer(victim);
				Player source = players.get(sourceid);
				if(source != null) {
					source.setKills(source.getKills() + 1);
				}
			}
			victim.setHealthpoints(hp);

			HitEvent hit = new HitEvent(victimid, sourceid, hitpoints, true);
			eventMachine.fireEvent(hit);
		}
	}
	
	private void killPlayer(Player p) {
		if(p == null) return;
		bulletAppState.getPhysicsSpace().remove(p.getControl());
		rootNode.detachChild(p.getModel());
		p.setAlive(false);
		p.setDeaths(p.getDeaths() + 1);
	}
	
	private void attack(Player p) {
		if(p == null) return;
		long passedTime = System.currentTimeMillis() - p.getLastShot();
		if(passedTime >= RELOAD_TIME) {
			p.setLastShot(System.currentTimeMillis());
			Vector3f pos = p.getControl().getPhysicsLocation();
			Vector3f dir = p.getViewDir();

			Bullet bul = Bullet.createBullet(p.getId());
			bul.getModel().setLocalTranslation(pos.add(dir.normalize()));
			rootNode.attachChild(bul.getModel());
			bul.getControl().setPhysicsLocation(pos.add(dir.normalize()));
			bulletAppState.getPhysicsSpace().add(bul.getControl());
			bul.getControl().setLinearVelocity(dir.normalize().mult(25));

			bullets.put(bul.getId(), bul);
			
			AttackEvent attack = new AttackEvent(p.getId(), bul.getId(), true);
			eventMachine.fireEvent(attack);
		}
	}
	
	private void jump(Player p) {
		if(p == null) return;
		p.getControl().jump();
		
		JumpEvent jump = new JumpEvent(p.getId(), true);
		eventMachine.fireEvent(jump);
	}
	
	private void joinPlayer(int playerid) {
		Player p = new Player(playerid, assetManager);
		players.put(playerid, p);
		
		PlayerJoinEvent join = new PlayerJoinEvent(playerid, true);
		eventMachine.fireEvent(join);
		
		sendInitialState(playerid);
		if(players.size() == 1) {
			gameplayController.restartRound();
		}
	}
	
	private void quitPlayer(Player p) {
		if(p != null) {
			bulletAppState.getPhysicsSpace().remove(p.getControl());
			rootNode.detachChild(p.getModel());
			players.remove(p.getId());
		}

		PlayerQuitEvent quit = new PlayerQuitEvent(p.getId(), true);
		eventMachine.fireEvent(quit);
	}
	
	private void respawn(Player p) {
		if(p == null) return;
		p.setHealthpoints(100);
		p.setAlive(true);
		bulletAppState.getPhysicsSpace().add(p.getControl());
		p.getControl().setPhysicsLocation(level.getSpawnPoint(p.getTeam()).getPosition());
		rootNode.attachChild(p.getModel());

		RespawnEvent respawn = new RespawnEvent(p.getId(), true);
		eventMachine.fireEvent(respawn);
	}
	
	private void handlePlayerInput(int playerid, InputCommand command, boolean value) {
		Player p = players.get(playerid);
		switch (command) {
		case ATTACK:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(p.isAlive()) {
					if(value) attack(p);
				}else {
					if(value) respawn(p);
				}
			}
			break;
		case JUMP:
			jump(p);
			break;
		case JOINGAME:
			joinPlayer(playerid);
			break;
		case CHOOSETEAM1:
			chooseTeam(p, 1);
			break;
		case CHOOSETEAM2:
			chooseTeam(p, 2);
			break;
		case QUITGAME:
			quitPlayer(p);
			break;

		default:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				p.handleInput(command, value);
			}
			break;
		}
	}

	private void chooseTeam(Player p, int team) {
		if(p == null) return;
		p.setTeam(team);
		
		ChooseTeamEvent event = new ChooseTeamEvent(p.getId(), team, true);
		eventMachine.fireEvent(event);
	}
	
	private void sendInitialState(int playerid) {
		PlayerInfo[] infos = new PlayerInfo[players.size()];
		int i=0;
		for (Player p : players.values()) {
			infos[i] = new PlayerInfo(p);
			i++;
		}
		InitialStateMessage msg = new InitialStateMessage();
		msg.setInfos(infos);
		networkController.sendMessage(msg, playerid);
	}

	public void setViewDir(int playerid, Vector3f dir) {
		Player p = players.get(playerid);
		if(p == null || dir == null)  return;
		p.setViewDir(dir);
	}
	
	public void connectionAdded(int clientid) {
		ConnectionInitMessage init = new ConnectionInitMessage();
		init.setConnectionAccepted(true);
		init.setText("Welcome");
		init.setLevel(level.getClass().getName());
		networkController.sendMessage(init, clientid);
		
		//TODO: send player information
		// id, name, team, alive, hitpoints
	}

	public void connectionRemoved(int clientid) {
		Player p = players.get(clientid);
		quitPlayer(p);
	}
	
	/**
	 * This class is used to send the current state of the virtual world to all clients in constant intervals.
	 * @author Findus
	 *
	 */
	private class WorldStateSenderLoop implements Runnable {
		@Override
		public void run() {
			while(!Thread.interrupted()) {
				WorldStateUpdatedMessage worldstate = WorldStateUpdatedMessage.getUpdate(players.values(), bullets.values());
				networkController.broadcast(worldstate);
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
	}

}
