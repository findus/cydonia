/**
 * 
 */
package de.findus.cydonia.server;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdom2.JDOMException;
import org.xml.sax.InputSource;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
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
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.Map;
import de.findus.cydonia.level.MapXMLParser;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.main.GameConfig;
import de.findus.cydonia.main.GameState;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.WorldStateUpdatedMessage;
import de.findus.cydonia.player.InputCommand;
import de.findus.cydonia.player.Picker;
import de.findus.cydonia.player.Player;
import de.findus.cydonia.player.PlayerController;
import de.findus.cydonia.player.PlayerInputState;

/**
 * @author Findus
 *
 */
public class GameServer extends Application implements EventListener, PhysicsCollisionListener {
	
	public static final String APPTITLE = "Cydonia Server";
	
	public static float PLAYER_SPEED = 5f;
	public static float PHYSICS_ACCURACY = (1f / 192);
	
	public static final int RELOAD_TIME = 500;

	public static final float MAX_PICK_RANGE = 20;

	public static final float MAX_PLACE_RANGE = 20;

	public static final boolean FREE_PLACING = false;

	private static final String MAPFILENAME = "/de/findus/cydonia/level/level3.xml";
	
	public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));

	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
	
	private ServerConfigFrame configFrame;
	
	private Thread senderLoop;
	
    private ConcurrentHashMap<Long, Bullet> bullets;
    
	private BulletAppState bulletAppState;
    
    private GameplayController gameplayController;
    
    private GameConfig gameConfig;
    
    /**
     * Used for moving players.
     * Allocated only once and reused for performance reasons.
     */
    private Vector3f walkdirection = new Vector3f();
    
	private NetworkController networkController;
	
	private EventMachine eventMachine;
	
	private ConcurrentLinkedQueue<Event> eventQueue;
	
	private WorldController worldController;
	
    protected PlayerController playerController;
	
	@Override
	public void start() {
		if (settings == null){
            settings = new AppSettings(true);
            settings.setTitle(APPTITLE);
        }
		super.start(JmeContext.Type.Headless);
	}
	
	@Override
	public void stop(boolean waitfor) {
		cleanup();
		super.stop(waitfor);
		System.exit(0);
	}
	
	private void cleanup() {
		networkController.stop();
		bulletAppState.setEnabled(false);
		senderLoop.interrupt();
		gameplayController.dispose();
		configFrame.setVisible(false);
		configFrame.dispose();
	}
	
    @Override
    public void initialize() {
        super.initialize();

        gameConfig = new GameConfig(true);
        
        configFrame = new ServerConfigFrame(this);
        configFrame.pack();
        configFrame.setVisible(true);
        
        eventQueue = new ConcurrentLinkedQueue<Event>();
        
        eventMachine = new EventMachine();
        
        this.bullets = new ConcurrentHashMap<Long, Bullet>();
        
        Bullet.setAssetManager(assetManager);

    	bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        worldController = new WorldController(assetManager, bulletAppState.getPhysicsSpace());
        InputSource is = new InputSource(ClassLoader.class.getResourceAsStream(MAPFILENAME));
        MapXMLParser mapXMLParser = new MapXMLParser(assetManager);
        try {
			Map map = mapXMLParser.loadMap(is);
			worldController.loadWorld(map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		}
        
        playerController = new PlayerController(assetManager, worldController, eventMachine);
        
        eventMachine.registerListener(this);
        
        networkController = new NetworkController(this, eventMachine);
		
        bulletAppState.setEnabled(true);
		senderLoop = new Thread(new WorldStateSenderLoop());
		senderLoop.start();
		
		gameplayController = new GameplayController(eventMachine, gameConfig);
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
        worldController.updateLogicalState(tpf);
        worldController.updateGeometricState();

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
    		}else if (e instanceof RestartRoundEvent) {
				for (Player p : playerController.getAllPlayers()) {
					if(p.isAlive()) {
						killPlayer(p);
					}
					p.getCurrentEquipment().reset();
				}
				removeAllBullets();
				worldController.resetWorld();
				for (Player p : playerController.getAllPlayers()) {
					respawn(p);
				}
				bulletAppState.setEnabled(true);
			}else if (e instanceof RoundEndedEvent) {
				bulletAppState.setEnabled(false);
				RoundEndedEvent roundEnded = (RoundEndedEvent) e;
				for (Player p : playerController.getAllPlayers()) {
					p.setInputState(new PlayerInputState());
					if(p.getId() == roundEnded.getWinnerid()) {
						p.setScores(p.getScores() + 1);
					}
				}
			}
    	}
    }

	@Override
	public void newEvent(Event e) {
		eventQueue.offer(e);
	}

	private void movePlayers(float tpf) {
		if(gameplayController.getGameState() != GameState.RUNNING) {
			return;
		}
		for (Player p : this.playerController.getAllPlayers()) {
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
		Spatial target = null;
		
		if(e.getNodeA() != null) {
			Boolean sticky = e.getNodeA().getUserData("Sticky");
			if (sticky != null && sticky.booleanValue() == true) {
				bullet = e.getNodeA();
				other = e.getNodeB();
			}
			
			if(e.getNodeA().getUserData("TargetArea") != null && ((Boolean)e.getNodeA().getUserData("TargetArea")).booleanValue() == true) {
				target = e.getNodeA();
				other = e.getNodeB();
			}
		}
		if (e.getNodeB() != null) {
			Boolean sticky = e.getNodeB().getUserData("Sticky");
			if (sticky != null && sticky.booleanValue() == true) {
				bullet = e.getNodeB();
				other = e.getNodeA();
			}
			
			if(e.getNodeB().getUserData("TargetArea") != null && ((Boolean)e.getNodeB().getUserData("TargetArea")).booleanValue() == true) {
				target = e.getNodeB();
				other = e.getNodeA();
			}
		}

		if(bullet != null && other != null) {
			worldController.detachObject(bullet);
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
		
		if(target != null && other != null) {
			if(other.getName().startsWith("player")) {
				gameplayController.targetReached(Integer.parseInt(other.getName().substring(6)));
			}
		}
	}
	
	private void hitPlayer(int sourceid, int victimid, double hitpoints) {
		Player victim = playerController.getPlayer(victimid);
		Player attacker = playerController.getPlayer(sourceid);
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
				Player source = playerController.getPlayer(sourceid);
				if(source != null) {
					source.setScores(source.getScores() + 1);
				}
			}
			victim.setHealthpoints(hp);

			HitEvent hit = new HitEvent(victimid, sourceid, hitpoints, true);
			eventMachine.fireEvent(hit);
		}
	}
	
	private void killPlayer(Player p) {
		if(p == null) return;
		worldController.detachPlayer(p);
		p.setAlive(false);
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
			worldController.attachObject(bul.getModel());
			bul.getControl().setPhysicsLocation(pos.add(dir.normalize()));
			bul.getControl().setLinearVelocity(dir.normalize().mult(25));

			bullets.put(bul.getId(), bul);
			
			AttackEvent attack = new AttackEvent(p.getId(), bul.getId(), true);
			eventMachine.fireEvent(attack);
		}
	}
	
	private void usePrimary(Player p) {
		if(p == null) return;
		
		p.getCurrentEquipment().usePrimary();
	}
	
	private void useSecondary(Player p) {
		if(p == null) return;
		
		p.getCurrentEquipment().useSecondary();
	}
	
	public void joinPlayer(int playerid, String playername) {
		Player p = playerController.createNew(playerid);
		p.setName(playername);
		
		PlayerJoinEvent join = new PlayerJoinEvent(playerid, playername, true);
		eventMachine.fireEvent(join);
		
		sendInitialState(playerid);
	}
	
	private void quitPlayer(Player p) {
		if(p != null) {
			worldController.detachPlayer(p);
			playerController.removePlayer(p.getId());

			PlayerQuitEvent quit = new PlayerQuitEvent(p.getId(), true);
			eventMachine.fireEvent(quit);
		}
	}
	
	private void respawn(Player p) {
		if(p == null) return;
		p.setHealthpoints(100);
		p.setAlive(true);
		p.getControl().setPhysicsLocation(worldController.getSpawnPoint(p.getTeam()).getPosition());
		worldController.attachPlayer(p);

		RespawnEvent respawn = new RespawnEvent(p.getId(), true);
		eventMachine.fireEvent(respawn);
	}
	
	public void handlePlayerInput(int playerid, InputCommand command, boolean value) {
		Player p = playerController.getPlayer(playerid);
		switch (command) {
		case USESECONDARY:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(value && p.isAlive()) {
					usePrimary(p);
				}
			}
			break;
		case USEPRIMARY:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(value && p.isAlive()) {
					useSecondary(p);
				}
			}
			break;
		case ATTACK:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(value) {
					if(p.isAlive()) {
						attack(p);
					}else {
						respawn(p);
					}
				}
			}
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
				InputEvent event = new InputEvent(p.getId(), command, value, true);
				eventMachine.fireEvent(event);
			}
			break;
		}
	}

	private void chooseTeam(Player p, int team) {
		if(p == null) return;
		playerController.setTeam(p, team);
		
		ChooseTeamEvent event = new ChooseTeamEvent(p.getId(), team, true);
		eventMachine.fireEvent(event);
		
		if(playerController.getPlayerCount() == 1) {
			gameplayController.restartRound();
		}
	}
	
	private void sendInitialState(int playerid) {
		PlayerInfo[] playerinfos = new PlayerInfo[playerController.getPlayerCount()];
		int i=0;
		for (Player p : playerController.getAllPlayers()) {
			playerinfos[i] = new PlayerInfo(p);
			i++;
		}
		
		Collection<Flube> list = worldController.getAllFlubes();
		MoveableInfo[] moveableinfos = new MoveableInfo[list.size()];
		int j=0;
		for (Flube m : list) {
			moveableinfos[j] = new MoveableInfo(m);
			j++;
		}
		
		InitialStateMessage msg = new InitialStateMessage();
		msg.setPlayers(playerinfos);
		msg.setMoveables(moveableinfos);
		msg.setconfig(gameConfig);
		networkController.sendMessage(msg, playerid);
	}

	public void setViewDir(int playerid, Vector3f dir) {
		Player p = playerController.getPlayer(playerid);
		if(p == null || dir == null)  return;
		p.setViewDir(dir);
	}
	
	public void connectionAdded(int clientid) {
		ConnectionInitMessage init = new ConnectionInitMessage();
		init.setConnectionAccepted(true);
		init.setText("Welcome");
//		try {
//			String xml = new MapXMLParser(assetManager).writeMap(worldController.getMap());
//			init.setLevel(xml);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		init.setLevel(MAPFILENAME);
		networkController.sendMessage(init, clientid);
	}

	public void connectionRemoved(int clientid) {
		Player p = playerController.getPlayer(clientid);
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
				WorldStateUpdatedMessage worldstate = WorldStateUpdatedMessage.getUpdate(playerController.getAllPlayers(), bullets.values());
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
