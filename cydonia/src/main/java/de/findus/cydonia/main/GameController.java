package de.findus.cydonia.main;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

import de.findus.cydonia.appstates.GameInputAppState;
import de.findus.cydonia.appstates.MenuController;
import de.findus.cydonia.bullet.Bullet;
import de.findus.cydonia.events.ConnectionDeniedEvent;
import de.findus.cydonia.events.ConnectionEstablishedEvent;
import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.EventListener;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.HitEvent;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.messages.AttackMessage;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.HitMessage;
import de.findus.cydonia.messages.JumpMessage;
import de.findus.cydonia.messages.PlayerInputMessage;
import de.findus.cydonia.messages.PlayerJoinMessage;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.messages.PlayerQuitMessage;
import de.findus.cydonia.messages.RespawnMessage;
import de.findus.cydonia.messages.WorldStateMessage;
import de.findus.cydonia.player.Player;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * GameController is the central controller of the game.
 * It loads all components, changes appStates, organizes the communication between the components.
 * 
 * @author Findus
 */
public class GameController extends Application implements ScreenController, PhysicsCollisionListener, EventListener{
	
	public static final String TEXTURES_PATH = "de/findus/cydonia/textures/";
	
	/**
	 * The time in seconds it should take to compensate a deviation from the accurate (=server defined) physical location of an object. 
	 */
	private static final float SMOOTHING = 0.2f;
	
	private static final int RELOAD_TIME = 500;
	
    public static float PLAYER_SPEED = 5f;
    public static float PHYSICS_ACCURACY = (1f / 192);
    
    public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));
    
    protected boolean showSettings = true;
    
    protected GameState gamestate;
    
    protected WorldController worldController;
    
    protected MenuController menuController;

	protected Node guiNode = new Node("Gui Node");
    
    private  boolean showFps = true;
    protected float secondCounter = 0.0f;
    protected int frameCounter = 0;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    
    private BulletAppState bulletAppState;
    private GameInputAppState gameInputAppState;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false, right=false, up=false, down=false;
    
    private Nifty nifty;
    private TextField serverAddressInput;
    private TextField playerNameInput;
    private DropDown<String> teamInput;
    
    private Player player;
    private ConcurrentHashMap<Integer, Player> players;
    private ConcurrentHashMap<Long, Bullet> bullets;
    private ServerConnector connector;
    
    private Thread inputSender;
    
    private LinkedList<Message> updateQueue;
    
    private EventMachine eventMachine;
    
    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }
    
    @Override
    public void stop() {
    	super.stop();
    }

    @Override
    public void initialize() {
        super.initialize();
        
        eventMachine = new EventMachine();
        
        this.gamestate = GameState.LOBBY;

        updateQueue = new LinkedList<Message>();
        players = new ConcurrentHashMap<Integer, Player>();
        bullets = new ConcurrentHashMap<Long, Bullet>();
        
        Bullet.setAssetManager(assetManager);
        Bullet.preloadTextures();
        
        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        loadFPSText();
        loadStatsView();
        worldController = new WorldController();
        worldController.setAssetManager(assetManager);
        viewPort.attachScene(worldController.getRootNode());
        guiViewPort.attachScene(guiNode);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
    			inputManager,
    			audioRenderer,
    			guiViewPort);
    	nifty = niftyDisplay.getNifty();
    	guiViewPort.addProcessor(niftyDisplay);

    	menuController = new MenuController(this);
    	menuController.actualizeScreen();
    	
    	gameInputAppState = new GameInputAppState(this);
    	

    	bulletAppState = new BulletAppState();
    	bulletAppState.setEnabled(false);
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
//        bulletAppState.setThreadingType(ThreadingType.SEQUENTIAL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
//        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        FogFilter fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(100);
        fog.setFogDensity(1.5f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        
        
        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
        bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        viewPort.addProcessor(bsr);
        
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.5f, 1000f);
        
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        eventMachine.registerListener(this);
        
        connector = new ServerConnector(eventMachine);
    }
    
    public void connect() {
    	gamestate = GameState.LOADING;
    	menuController.actualizeScreen();
    	String serveraddress = this.serverAddressInput.getText();
    	String playername = this.playerNameInput.getText();
    	int team = this.teamInput.getSelectedIndex() + 1;
    	connector.connectToServer(serveraddress, 6173);
    	player = new Player(connector.getConnectionId(), assetManager);
    	player.setName(playername);
    	player.setTeam(team);
    	players.put(player.getId(), player);
    }
    
    

	/**
     * Starts the actual game eg. the game loop.
     */
    public void startGame(String level) {
        worldController.loadWorld(level);
    	bulletAppState.getPhysicsSpace().add(worldController.getWorldCollisionControll());
        
        PlayerJoinMessage join = new PlayerJoinMessage();
    	join.setId(player.getId());
    	join.setName(player.getName());
    	join.setTeam(player.getTeam());
    	connector.sendMessage(join);
        
        bulletAppState.getPhysicsSpace().add(player.getControl());
        player.getControl().setPhysicsLocation(new Vector3f(0, 10, 0));
    	bulletAppState.setEnabled(true);
    	gamestate = GameState.DEAD;
    	stateManager.attach(gameInputAppState);
    	menuController.actualizeScreen();
    }
    
    /**
     * Resumes the game after pausing.
     */
    public void resumeGame() {
    	gamestate = GameState.RUNNING;
    	menuController.actualizeScreen();
    	stateManager.attach(gameInputAppState);
    	bulletAppState.setEnabled(true);
    	startInputSender();
    }
    
    /**
     * pauses the game and opens the menu.
     */
    public void pauseGame() {
    	bulletAppState.setEnabled(false);
    	stateManager.detach(gameInputAppState);
    	gamestate = GameState.PAUSED;
    	menuController.actualizeScreen();
    	stopInputSender();
    }
    
    public void stopGame() {
    	stopInputSender();
    	connector.disconnectFromServer();
    	stop();
    }
    
    public void gameOver() {
//    	stateManager.detach(gameInputAppState);
    	gamestate = GameState.DEAD;
    	menuController.actualizeScreen();
    	stopInputSender();
    }
    
    public void respawn() {
    	RespawnMessage respawn = new RespawnMessage();
    	respawn.setPlayerid(player.getId());
    	connector.sendMessage(respawn);
    }
    
    public void jump() {
    	player.getControl().jump();
    	JumpMessage jump = new JumpMessage();
    	jump.setPlayerid(player.getId());
    	connector.sendMessage(jump);
    }

	@Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        if (showFps) {
            secondCounter += timer.getTimePerFrame();
            frameCounter ++;
            if (secondCounter >= 1.0f) {
                int fps = (int) (frameCounter / secondCounter);
                fpsText.setText("Frames per second: " + fps);
                secondCounter = 0.0f;
                frameCounter = 0;
            }          
        }

        // update states
        stateManager.update(tpf);

        // update game specific things
        handleMessages();
        movePlayers(tpf);
        
        // update world and gui
        worldController.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        worldController.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        simpleRender(renderManager);
        stateManager.postRender();
    }
    
    private void handleMessages() {
    	while (!updateQueue.isEmpty()) {
    		Message msg = updateQueue.poll();
    		if(msg instanceof ConnectionInitMessage) {
    			ConnectionInitMessage init = (ConnectionInitMessage) msg;
    			if(init.isDenied()) {
    				System.out.println("Server denied connection! Reason: '" + init.getReason() + "'");
    				gamestate = GameState.LOBBY;
    				menuController.actualizeScreen();
    				clean();
    				connector.disconnectFromServer();
    			}else {
    				startGame(init.getLevel());
    			}
    		}
    		if(msg instanceof WorldStateMessage) {
    			WorldStateMessage worldState = (WorldStateMessage) msg;

    			for (PlayerPhysic physic : worldState.getPlayerPhysics()) {
    				Player p = players.get(physic.getId());
//    				if(p == null) {
//    					p = new Player(physic.getId(), assetManager);
//    					System.out.println("generated playermodel client: " + p.getId());
//    					p.getControl().setPhysicsLocation(new Vector3f(5, 5, 5));
//    					players.put(p.getId(), p);
//    					bulletAppState.getPhysicsSpace().add(p.getControl());
//    					worldController.attachObject(p.getModel());
//    				}
    				if(p != null) {
    					p.setExactLoc(physic.getTranslation());
    					p.getControl().setViewDirection(physic.getOrientation());
    				}
    			}

    			for (BulletPhysic physic : worldState.getBulletPhysics()) {
    				Bullet b = bullets.get(physic.getId());
    				if(b == null) {
    					b = new Bullet(physic.getId(), physic.getSourceid());
    					bullets.put(b.getId(), b);
    					bulletAppState.getPhysicsSpace().add(b.getControl());
    					worldController.attachObject(b.getModel());
    				}
    				if(b != null) {	
    					b.setExactLoc(physic.getTranslation());
    					b.getControl().setPhysicsLocation(physic.getTranslation());
    					b.getControl().setLinearVelocity(physic.getVelocity());
    				}
    			}
    		}else if(msg instanceof AttackMessage) {
    			AttackMessage attack = (AttackMessage) msg;
    			int playerid = attack.getPlayerid();
    			player.setLastShot(System.currentTimeMillis());
    			BulletPhysic physic = attack.getPhysic();
    			Bullet b = new Bullet(physic.getId(), playerid);
    			b.getControl().setPhysicsLocation(physic.getTranslation());
    			bullets.put(b.getId(), b);
    			bulletAppState.getPhysicsSpace().add(b.getControl());
    			b.getModel().setLocalTranslation(physic.getTranslation());
    			worldController.attachObject(b.getModel());
    		}else if(msg instanceof HitMessage) {
    			HitMessage hit = (HitMessage) msg;
    			hitPlayer(hit.getSourcePlayerid(), hit.getVictimPlayerid(), hit.getHitpoints());
    		}else if(msg instanceof PlayerJoinMessage) {
    			PlayerJoinMessage join = (PlayerJoinMessage) msg;
    			int playerid = join.getId();
    			if(player.getId() != playerid) {
    				Player p = new Player(playerid, assetManager);
    				p.setName(join.getName());
    				p.setTeam(join.getTeam());
    				System.out.println("player joined client: " + join.getTeam());
    				players.put(p.getId(), p);
    			}
    		}else if(msg instanceof RespawnMessage) {
    			RespawnMessage respawn = (RespawnMessage) msg;
        		int playerid = respawn.getPlayerid();
        		Player p = players.get(playerid);
        		p.setHealthpoints(100);
        		p.setAlive(true);
        		bulletAppState.getPhysicsSpace().add(p.getControl());
        		p.getControl().setPhysicsLocation(worldController.getLevel().getSpawnPoint(p.getTeam()).getPosition());
        		worldController.attachObject(p.getModel());
        		if(p.getId() == player.getId()) {
        			resumeGame();
        		}
    		}else if(msg instanceof PlayerQuitMessage) {
    			PlayerQuitMessage quit = (PlayerQuitMessage) msg;
    			int playerid = quit.getId();
    			Player p = players.get(playerid);
    			bulletAppState.getPhysicsSpace().remove(p.getControl());
    			worldController.detachObject(p.getModel());
    			players.remove(playerid);
    		}else if (msg instanceof JumpMessage) {
				JumpMessage jump = (JumpMessage) msg;
				if(jump.getPlayerid() != player.getId()) {
					players.get(jump.getPlayerid()).getControl().jump();
				}
			}
    	}
    }

	@Override
	public void newEvent(Event e) {
		if(e instanceof ConnectionDeniedEvent) {
				System.out.println("Server denied connection! Reason: '" + ((ConnectionDeniedEvent) e).getReason() + "'");
				gamestate = GameState.LOBBY;
				menuController.actualizeScreen();
				clean();
				connector.disconnectFromServer();
		}else if (e instanceof ConnectionEstablishedEvent) {
			startGame(((ConnectionEstablishedEvent) e).getLevel());
		}
		if(e instanceof WorldStateEvent) {
			WorldStateEvent worldState = (WorldStateEvent) e;

			for (PlayerPhysic physic : worldState.getPlayerPhysics()) {
				Player p = players.get(physic.getId());
				if(p != null) {
					p.setExactLoc(physic.getTranslation());
					p.getControl().setViewDirection(physic.getOrientation());
				}
			}

			for (BulletPhysic physic : worldState.getBulletPhysics()) {
				Bullet b = bullets.get(physic.getId());
				if(b == null) {
					b = new Bullet(physic.getId(), physic.getSourceid());
					bullets.put(b.getId(), b);
					bulletAppState.getPhysicsSpace().add(b.getControl());
					worldController.attachObject(b.getModel());
				}
				if(b != null) {	
					b.setExactLoc(physic.getTranslation());
					b.getControl().setPhysicsLocation(physic.getTranslation());
					b.getControl().setLinearVelocity(physic.getVelocity());
				}
			}
		}else if(e instanceof AttackEvent) {
			AttackEvent attack = (AttackEvent) e;
			int playerid = attack.getPlayerid();
			Player p = players.get(playerid);
			p.setLastShot(System.currentTimeMillis());
			Bullet b = new Bullet(attack.getBulletid(), playerid);
			b.getControl().setPhysicsLocation(p.getControl().getPhysicsLocation());
			bullets.put(b.getId(), b);
			bulletAppState.getPhysicsSpace().add(b.getControl());
			worldController.attachObject(b.getModel());
		}else if(e instanceof HitEvent) {
			HitEvent hit = (HitEvent) e;
			hitPlayer(hit.getAttackerPlayerid(), hit.getVictimPlayerid(), hit.getHitpoints());
		}else if(e instanceof PlayerJoinEvent) {
			PlayerJoinEvent join = (PlayerJoinEvent) e;
			int playerid = join.getPlayerId();
			if(player.getId() != playerid) {
				Player p = new Player(playerid, assetManager);
				p.setName(join.getName());
				p.setTeam(join.getTeam());
				System.out.println("player joined client: " + join.getTeam());
				players.put(p.getId(), p);
			}
		}else if(e instanceof RespawnEvent) {
			RespawnEvent respawn = (RespawnEvent) e;
    		int playerid = respawn.getPlayerid();
    		Player p = players.get(playerid);
    		p.setHealthpoints(100);
    		p.setAlive(true);
    		bulletAppState.getPhysicsSpace().add(p.getControl());
    		p.getControl().setPhysicsLocation(worldController.getLevel().getSpawnPoint(p.getTeam()).getPosition());
    		worldController.attachObject(p.getModel());
    		if(p.getId() == player.getId()) {
    			resumeGame();
    		}
		}else if(e instanceof PlayerQuitEvent) {
			PlayerQuitEvent quit = (PlayerQuitEvent) e;
			int playerid = quit.getId();
			Player p = players.get(playerid);
			bulletAppState.getPhysicsSpace().remove(p.getControl());
			worldController.detachObject(p.getModel());
			players.remove(playerid);
		}else if (msg instanceof JumpMessage) {
			JumpMessage jump = (JumpMessage) msg;
			if(jump.getPlayerid() != player.getId()) {
				players.get(jump.getPlayerid()).getControl().jump();
			}
		}
	}

	private void clean() {
		players.clear();
	}

    /**
     * Moves the player according to user input state.
     * @param tpf time per frame
     */
	private void movePlayers(float tpf) {
        
		for (Player p : players.values()) {
			if(p.getId() == connector.getConnectionId()) {
				p.getControl().setViewDirection(cam.getDirection());
			}
			Vector3f viewDir = p.getControl().getViewDirection().clone().setY(0).normalizeLocal();
			Vector3f viewLeft = new Vector3f();
			ROTATE90LEFT.transformVector(viewDir, viewLeft);

			walkDirection.set(0, 0, 0);
			if(p.getInputState().isLeft()) walkDirection.addLocal(viewLeft);
			if(p.getInputState().isRight()) walkDirection.addLocal(viewLeft.negate());
			if(p.getInputState().isForward()) walkDirection.addLocal(viewDir);
			if(p.getInputState().isBack()) walkDirection.addLocal(viewDir.negate());

			
			walkDirection.normalizeLocal().multLocal(PLAYER_SPEED);
			
			Vector3f correction = p.getExactLoc().subtract(p.getControl().getPhysicsLocation()).divide(SMOOTHING);
			walkDirection.addLocal(correction);

			walkDirection.multLocal(PHYSICS_ACCURACY);
			p.getControl().setWalkDirection(walkDirection);
			
			if(p.getId() == connector.getConnectionId()) {
				cam.setLocation(p.getEyePosition());
			}
		}
		
    }

    public void simpleRender(RenderManager rm) {
        //TODO: add render code
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
    		worldController.detachObject(bullet);
			bulletAppState.getPhysicsSpace().remove(bullet.getControl(RigidBodyControl.class));
			bullet.removeControl(RigidBodyControl.class);
    		if(other.getName().startsWith("player")) {
    			// Hit Player not here. only when message from server.
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

	/**
     * Creates a ball and throws it i view direction.
     */
    public void attack() {
    	switch(getGamestate()) {
    	case RUNNING:
    		long passedTime = System.currentTimeMillis() - player.getLastShot();
    		if(passedTime >= RELOAD_TIME) {
    			AttackMessage msg = new AttackMessage();
    			msg.setPlayerid(player.getId());
    			this.connector.sendMessage(msg);
    		}
    		break;

    	case DEAD:
    		respawn();
    		break;
    	}
    }
	
	private void hitPlayer(int sourceid, int victimid, double hitpoints) {
		Player victim = players.get(victimid);
		if(victim == null) {
			return;
		}
		double hp = victim.getHealthpoints();
		hp -= hitpoints;
		System.out.println("hit - new hp: " + hp);
		if(hp <= 0) {
			hp = 0;
			this.killPlayer(victimid);
			Player source = this.players.get(sourceid);
			if(source != null) {
				source.setKills(source.getKills() + 1);
			}
		}
		victim.setHealthpoints(hp);
	}
	
	private void killPlayer(int id) {
		Player p = players.get(id);
		if(p != null) {
			bulletAppState.getPhysicsSpace().remove(p.getControl());
			worldController.detachObject(p.getModel());
			p.setAlive(false);
			p.setDeaths(p.getDeaths() + 1);
			if(id == player.getId()) {
				gameOver();
			}
		}
	}
	
	public String getScores() {
		StringBuilder sb_team1 = new StringBuilder();
		StringBuilder sb_team2 = new StringBuilder();
		for (Player p : players.values()) {
			if(p.getTeam() == 1) {
				sb_team1.append("\n" + p.getName() + "\t\t" + p.getKills() + "\t\t" + p.getDeaths());
			}else if(p.getTeam() == 2) {
				sb_team2.append("\n" + p.getName() + "\t\t" + p.getKills() + "\t\t" + p.getDeaths());
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Team 1 \t\t Kills \t\t Deaths");
		sb.append(sb_team1);
		sb.append("\n\n\n\n");
		sb.append("Team 2 \t\t Kills \t\t Deaths");
		sb.append(sb_team2);
		return sb.toString();
	}
	
	public void scoreboard(boolean show) {
		if(gamestate == GameState.RUNNING || gamestate == GameState.DEAD || gamestate == GameState.SPECTATE) {
			if(show) {
				menuController.showScorebord();
			}else {
				menuController.actualizeScreen();
			}
		}
	}
	
	/**
	 * Attaches FPS statistics to guiNode and displays it on the screen.
	 */
	public void loadFPSText() {
	    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
	    fpsText = new BitmapText(guiFont, false);
	    fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
	    fpsText.setText("Frames per second");
	    guiNode.attachChild(fpsText);
	}

	/**
	 * Attaches Statistics View to guiNode and displays it on the screen above FPS statistics line.
	 */
	public void loadStatsView() {
		statsView = new StatsView("Statistics View", assetManager, renderer.getStatistics());
		//         move it up so it appears above fps text
		statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		guiNode.attachChild(statsView);
	}

	/**
	 * Sets the displayFPS property.
	 * @param show if true fps are painted
	 */
	public void setDisplayFps(boolean show) {
		showFps = show;
		fpsText.setCullHint(show ? CullHint.Never : CullHint.Always);
	}

	/**
	 * Sets the displayStats property.
	 * @param show if true stats are painted
	 */
    public void setDisplayStatView() {
        boolean show = !statsView.isEnabled();
    	statsView.setEnabled(show);
        statsView.setCullHint(show ? CullHint.Never : CullHint.Always);
    }

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.serverAddressInput = screen.findNiftyControl("serveraddress", TextField.class);
		this.playerNameInput = screen.findNiftyControl("playername", TextField.class);
		this.teamInput = screen.findNiftyControl("team", DropDown.class);
		
		this.teamInput.addItem("Team 1");
		this.teamInput.addItem("Team 2");
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
	}
	
	public GameState getGamestate() {
		return gamestate;
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	/**
	 * Retrieves nifty.
	 * @return nifty object
	 */
	public Nifty getNifty() {
		return nifty;
	}

	/**
     * Retrieves guiNode
     * @return guiNode Node object
     */
    public Node getGuiNode() {
        return guiNode;
    }
    
    /**
	 * Starts the input sender loop.
	 */
	public void startInputSender() {
		inputSender = new Thread(new InputSenderLoop());
		inputSender.start();
	}
    
    /**
	 * Stops the input sender loop.
	 */
	public void stopInputSender() {
		inputSender.interrupt();
	}
    
    /**
	 * This class is used to send the user input state to the server in constant time intervals.
	 * @author Findus
	 *
	 */
	private class InputSenderLoop implements Runnable {

		@Override
		public void run() {
			while(!Thread.interrupted()) {
				PlayerInputMessage m = new PlayerInputMessage();
				m.setInputs(player.getInputState());
				m.setPlayerId(player.getId());
				m.setViewDir(player.getControl().getViewDirection());
				m.setReliable(false);
				connector.sendMessage(m);
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
	}
}
