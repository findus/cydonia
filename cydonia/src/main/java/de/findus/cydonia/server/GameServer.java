/**
 * 
 */
package de.findus.cydonia.server;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom2.JDOMException;
import org.xml.sax.InputSource;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.events.AttackEvent;
import de.findus.cydonia.events.ChooseTeamEvent;
import de.findus.cydonia.events.ConnectionAddedEvent;
import de.findus.cydonia.events.ConnectionRemovedEvent;
import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.FlagEvent;
import de.findus.cydonia.events.InputEvent;
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.level.Flag;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.Map;
import de.findus.cydonia.level.MapXMLParser;
import de.findus.cydonia.main.GameState;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.WorldStateUpdatedMessage;
import de.findus.cydonia.player.Beamer;
import de.findus.cydonia.player.InputCommand;
import de.findus.cydonia.player.Player;
import de.findus.cydonia.player.PlayerInputState;

/**
 * @author Findus
 *
 */
public class GameServer extends MainController{
	
	public static final String APPTITLE = "Cydonia Server";
	
	public static final int RELOAD_TIME = 500;

	public static final float MAX_PICK_RANGE = 20;

	public static final float MAX_PLACE_RANGE = 20;

	public static final boolean FREE_PLACING = false;

	private static final String MAPFILENAME = "/de/findus/cydonia/level/testlevel1.xml";

	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
	
	private ServerConfigFrame configFrame;
	
	private Thread senderLoop;
	
    private ConcurrentHashMap<Long, Bullet> bullets;
    
    private GameplayController gameplayController;
    
    /**
     * Used for moving players.
     * Allocated only once and reused for performance reasons.
     */
    private Vector3f walkdirection = new Vector3f();
    
	private NetworkController networkController;
	
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
//		System.exit(0);
	}
	
	protected void cleanup() {
		super.cleanup();
		networkController.stop();
		senderLoop.interrupt();
		gameplayController.dispose();
		configFrame.setVisible(false);
		configFrame.dispose();
	}
	
    @Override
    public void initialize() {
        super.initialize();

        configFrame = new ServerConfigFrame(this);
        configFrame.pack();
        configFrame.setVisible(true);
        
        this.bullets = new ConcurrentHashMap<Long, Bullet>();
        
        Bullet.setAssetManager(assetManager);

        InputSource is = new InputSource(ClassLoader.class.getResourceAsStream(MAPFILENAME));
        MapXMLParser mapXMLParser = new MapXMLParser(assetManager);
        try {
			Map map = mapXMLParser.loadMap(is);
			getWorldController().loadWorld(map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		}
        
        networkController = new NetworkController(this, getEventMachine());
		
        getBulletAppState().setEnabled(true);
		senderLoop = new Thread(new WorldStateSenderLoop());
		senderLoop.start();
		
		gameplayController = new GameplayController(getEventMachine(), getGameConfig());
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

        computeBeams(tpf);
        
        // update game specific things
        movePlayers(tpf);
        
        // update world and gui
        getWorldController().updateLogicalState(tpf);
        getWorldController().updateGeometricState();

        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();
    }

	@Override
	protected void handleEvent(Event e) {
		if(e instanceof ConnectionAddedEvent) {
			connectionAdded(((ConnectionAddedEvent) e).getClientid());
		}else if(e instanceof ConnectionRemovedEvent) {
			connectionRemoved(((ConnectionRemovedEvent) e).getClientid());
		}else if (e instanceof RestartRoundEvent) {
			for (Player p : getPlayerController().getAllPlayers()) {
				if(p.isAlive()) {
					killPlayer(p);
				}
				getPlayerController().reset(p);
			}
			removeAllBullets();
			getWorldController().resetWorld();
			for (Player p : getPlayerController().getAllPlayers()) {
				respawn(p);
			}
			getBulletAppState().setEnabled(true);
		}else if (e instanceof RoundEndedEvent) {
			getBulletAppState().setEnabled(false);
			RoundEndedEvent roundEnded = (RoundEndedEvent) e;
			for (Player p : getPlayerController().getAllPlayers()) {
				p.setInputState(new PlayerInputState());
				if(p.getId() == roundEnded.getWinnerid()) {
					p.setScores(p.getScores() + 1);
				}
			}
		}else if(e instanceof FlagEvent) {
			FlagEvent flagev = (FlagEvent) e;
			if(flagev.getType() == FlagEvent.TAKE) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				Player p = getPlayerController().getPlayer(flagev.getPlayerid());
				takeFlag(p, f);
			}else if(flagev.getType() == FlagEvent.SCORE) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				Player p = getPlayerController().getPlayer(flagev.getPlayerid());
				scoreFlag(p, f);
			}else if(flagev.getType() == FlagEvent.RETURN) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				returnFlag(f);
			}
		}
	}

	private void movePlayers(float tpf) {
		if(gameplayController.getGameState() != GameState.RUNNING) {
			return;
		}
		for (Player p : this.getPlayerController().getAllPlayers()) {
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
	
	private void computeBeams(float tpf) {
		for(Player p : getPlayerController().getAllPlayers()) {
			if(p.getCurrentEquipment() instanceof Beamer) {
				Beamer beamer = (Beamer) p.getCurrentEquipment();
				if(beamer.isBeaming()) {
					CollisionResult result = getWorldController().pickRoot(beamer.getPlayer().getEyePosition().add(beamer.getPlayer().getViewDir().normalize().mult(0.3f)), beamer.getPlayer().getViewDir());
					if(result != null && result.getGeometry().getParent() != null && result.getGeometry().getParent().getName() != null && result.getGeometry().getParent().getName().startsWith("player")) {
						Player victim = getPlayerController().getPlayer(Integer.valueOf(result.getGeometry().getParent().getName().substring(6)));
						if(victim != null && victim.getTeam() != beamer.getPlayer().getTeam()) {
							victim.setHealthpoints(victim.getHealthpoints() - 20*tpf);
							if(victim.getHealthpoints() <= 0) {
								killPlayer(victim);
								beamer.getPlayer().setScores(beamer.getPlayer().getScores() + 1);
							}
						}
					}
				}
			}
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
//		Spatial bullet = null;
		Spatial other = null;
		Spatial target = null;
		
		if(e.getNodeA() != null) {
//			Boolean sticky = e.getNodeA().getUserData("Sticky");
//			if (sticky != null && sticky.booleanValue() == true) {
//				bullet = e.getNodeA();
//				other = e.getNodeB();
//			}
			
			if(e.getNodeA().getUserData("FlagBase") != null && ((Boolean)e.getNodeA().getUserData("FlagBase")).booleanValue() == true) {
				target = e.getNodeA();
				other = e.getNodeB();
			}
		}
		if (e.getNodeB() != null) {
//			Boolean sticky = e.getNodeB().getUserData("Sticky");
//			if (sticky != null && sticky.booleanValue() == true) {
//				bullet = e.getNodeB();
//				other = e.getNodeA();
//			}
			
			if(e.getNodeB().getUserData("FlagBase") != null && ((Boolean)e.getNodeB().getUserData("FlagBase")).booleanValue() == true) {
				target = e.getNodeB();
				other = e.getNodeA();
			}
		}

//		if(bullet != null && other != null) {
//			getWorldController().detachObject(bullet);
//			bullet.removeControl(RigidBodyControl.class);
//			if(other.getName().startsWith("player") && bullet.getName().startsWith("bullet")) {
//				int victimid = Integer.parseInt(other.getName().substring(6));
//				long bulletid = Long.parseLong(bullet.getName().substring(6));
//				Bullet bul = bullets.get(bulletid);
//				this.hitPlayer(bul.getPlayerid(), victimid, 20);
//			}else {
//				if(other != null) {
//					if (other instanceof Node) {
//						((Node) other).attachChild(bullet);
//					}else {
//						other.getParent().attachChild(bullet);
//					}
//				}
//			}
//		}
		
		if(target != null && other != null) {
			if(other.getName().startsWith("player")) {
				Player p = getPlayerController().getPlayer(Integer.parseInt(other.getName().substring(6)));
				if(p.getTeam() == (int) target.getUserData("team")) { // own target
					if(p.getFlag() != null) {
						int stolenflagid = p.getFlag().getId();
						p.setFlag(null);
						Flag f = getWorldController().getFlag((int)target.getUserData("id"));
						if(f.isInBase()) {
							System.out.println("Team " + p.getTeam() + " scored");
							FlagEvent event = new FlagEvent(FlagEvent.SCORE, p.getId(), stolenflagid, true);
							getEventMachine().fireEvent(event);
						}
					}
				}else { // opponents target
					if(p.getFlag() == null) {
						Flag f = getWorldController().getFlag((int)target.getUserData("id"));
						if(f.isInBase()) {
							f.setInBase(false);
							System.out.println("Team " + p.getTeam() + " took flag");
							FlagEvent event = new FlagEvent(FlagEvent.TAKE, p.getId(), f.getId(), true);
							getEventMachine().fireEvent(event);
						}
					}
				}
			}
		}
	}
	
//	private void hitPlayer(int sourceid, int victimid, double hitpoints) {
//		Player victim = getPlayerController().getPlayer(victimid);
//		Player attacker = getPlayerController().getPlayer(sourceid);
//		if(victim == null) {
//			System.out.println("cannot prozess hit. player not available.");
//			return;
//		}
//		if(attacker == null || victim.getTeam() != attacker.getTeam()) {
//			double hp = victim.getHealthpoints();
//			hp -= hitpoints;
//			if(hp <= 0) {
//				hp = 0;
//				this.killPlayer(victim);
//				Player source = getPlayerController().getPlayer(sourceid);
//				if(source != null) {
//					source.setScores(source.getScores() + 1);
//				}
//			}
//			victim.setHealthpoints(hp);
//
//			HitEvent hit = new HitEvent(victimid, sourceid, hitpoints, true);
//			getEventMachine().fireEvent(hit);
//		}
//	}
	
	private void attack(Player p) {
		if(p == null) return;
		long passedTime = System.currentTimeMillis() - p.getLastShot();
		if(passedTime >= RELOAD_TIME) {
			p.setLastShot(System.currentTimeMillis());
			Vector3f pos = p.getControl().getPhysicsLocation();
			Vector3f dir = p.getViewDir();

			Bullet bul = Bullet.createBullet(p.getId());
			bul.getModel().setLocalTranslation(pos.add(dir.normalize()));
			getWorldController().attachObject(bul.getModel());
			bul.getControl().setPhysicsLocation(pos.add(dir.normalize()));
			bul.getControl().setLinearVelocity(dir.normalize().mult(25));

			bullets.put(bul.getId(), bul);
			
			AttackEvent attack = new AttackEvent(p.getId(), bul.getId(), true);
			getEventMachine().fireEvent(attack);
		}
	}
	
	protected void joinPlayer(int playerid, String playername) {
		super.joinPlayer(playerid, playername);
		
		PlayerJoinEvent join = new PlayerJoinEvent(playerid, playername, true);
		getEventMachine().fireEvent(join);
		
		sendInitialState(playerid);
	}
	
	protected void quitPlayer(Player p) {
		super.quitPlayer(p);
		if(p != null) {
			PlayerQuitEvent quit = new PlayerQuitEvent(p.getId(), true);
			getEventMachine().fireEvent(quit);
		}
	}
	
	protected void respawn(Player p) {
		super.respawn(p);
		
		if(p == null) return;
		RespawnEvent respawn = new RespawnEvent(p.getId(), true);
		getEventMachine().fireEvent(respawn);
	}
	
	public void handlePlayerInput(int playerid, InputCommand command, boolean value) {
		Player p = getPlayerController().getPlayer(playerid);
		switch (command) {
		case USEPRIMARY:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(p.isAlive()) {
					p.handleInput(command, value);
					InputEvent event = new InputEvent(p.getId(), command, value, true);
					getEventMachine().fireEvent(event);
				}else {
					respawn(p);
				}
			}
			break;
		case USESECONDARY:
			if(gameplayController.getGameState() == GameState.RUNNING) {
				if(p.isAlive()) {
					p.handleInput(command, value);
					InputEvent event = new InputEvent(p.getId(), command, value, true);
					getEventMachine().fireEvent(event);
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
				getEventMachine().fireEvent(event);
			}
			break;
		}
	}

	protected void chooseTeam(Player p, int team) {
		super.chooseTeam(p, team);
		
		if(p == null) return;
		
		ChooseTeamEvent event = new ChooseTeamEvent(p.getId(), team, true);
		getEventMachine().fireEvent(event);
		
		if(getPlayerController().getPlayerCount() == 1) {
			gameplayController.restartRound();
		}
	}
	
	private void sendInitialState(int playerid) {
		PlayerInfo[] playerinfos = new PlayerInfo[getPlayerController().getPlayerCount()];
		int i=0;
		for (Player p : getPlayerController().getAllPlayers()) {
			playerinfos[i] = new PlayerInfo(p);
			i++;
		}
		
		Collection<Flube> list = getWorldController().getAllFlubes();
		MoveableInfo[] moveableinfos = new MoveableInfo[list.size()];
		int j=0;
		for (Flube m : list) {
			moveableinfos[j] = new MoveableInfo(m);
			j++;
		}
		
		InitialStateMessage msg = new InitialStateMessage();
		msg.setPlayers(playerinfos);
		msg.setMoveables(moveableinfos);
		msg.setconfig(getGameConfig());
		networkController.sendMessage(msg, playerid);
	}

	public void setViewDir(int playerid, Vector3f dir) {
		Player p = getPlayerController().getPlayer(playerid);
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
		Player p = getPlayerController().getPlayer(clientid);
		quitPlayer(p);
	}
	
	public void handleCommand(String command) {
		if("restartround".equalsIgnoreCase(command)) {
			gameplayController.endRound(-1, true);
		}
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
				WorldStateUpdatedMessage worldstate = WorldStateUpdatedMessage.getUpdate(getPlayerController().getAllPlayers(), bullets.values());
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
