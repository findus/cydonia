package de.findus.cydonia.main;

import java.util.HashMap;
import java.util.LinkedList;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsView;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

import de.findus.cydonia.appstates.GameInputAppState;
import de.findus.cydonia.appstates.MenuAppState;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.messages.AttackMessage;
import de.findus.cydonia.messages.HitMessage;
import de.findus.cydonia.messages.WorldStateMessage;
import de.findus.cydonia.server.Bullet;
import de.findus.cydonia.server.BulletPhysic;
import de.findus.cydonia.server.Player;
import de.findus.cydonia.server.PlayerPhysic;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * GameController is the central controller of the game.
 * It loads all components, changes appStates, organizes the communication between the components.
 * 
 * @author Findus
 */
public class GameController extends Application implements ActionListener, ScreenController, MessageListener<Client>, PhysicsCollisionListener{
	
	public static final String TEXTURES_PATH = "de/findus/cydonia/textures/";
	
	/**
	 * The time in seconds it should take to compensate a deviation from the accurate (=server defined) physical location of an object. 
	 */
	private static final float SMOOTHING = 0.2f;
	
	
	public static float MAX_STEP_HEIGHT = 0.2f;
    public static float PLAYER_SPEED = 5f;
    public static float PHYSICS_ACCURACY = (1f / 240);
    
    public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));
    
    protected boolean showSettings = true;
    
    protected GameState gamestate;
    
    protected WorldController worldController;
    
    protected Node guiNode = new Node("Gui Node");
    
    private  boolean showFps = true;
    protected float secondCounter = 0.0f;
    protected int frameCounter = 0;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    
    private BulletAppState bulletAppState;
    private MenuAppState menuAppState;
    private GameInputAppState gameInputAppState;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false, right=false, up=false, down=false;
    
    private Nifty nifty;
    private Player player;
    private HashMap<Integer, Player> players;
    private HashMap<Long, Bullet> bullets;
    private ServerConnector connector;
    
    private LinkedList<Message> updateQueue;
    
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
        
        this.gamestate = GameState.LOBBY;

        updateQueue = new LinkedList<Message>();
        players = new HashMap<Integer, Player>();
        bullets = new HashMap<Long, Bullet>();
        
        Bullet.setAssetManager(assetManager);
        
        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        loadFPSText();
        loadStatsView();
        worldController = new WorldController();
        worldController.setAssetManager(assetManager);
        worldController.loadWorld();
        viewPort.attachScene(worldController.getRootNode());
        guiViewPort.attachScene(guiNode);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
    			inputManager,
    			audioRenderer,
    			guiViewPort);
    	nifty = niftyDisplay.getNifty();
    	guiViewPort.addProcessor(niftyDisplay);

    	menuAppState = new MenuAppState();
    	menuAppState.initialize(stateManager, this);
    	stateManager.attach(menuAppState);
    	
    	gameInputAppState = new GameInputAppState(this);
    	

    	bulletAppState = new BulletAppState();
    	bulletAppState.setEnabled(false);
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        bulletAppState.setThreadingType(ThreadingType.SEQUENTIAL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
//        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        FogFilter fog=new FogFilter();
//        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
//        fog.setFogDistance(100);
//        fog.setFogDensity(1.5f);
//        fpp.addFilter(fog);
//        viewPort.addProcessor(fpp);
        
        
//        rootNode.setShadowMode(ShadowMode.Off);
//        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
//        bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
//        viewPort.addProcessor(bsr);
        
        player = new Player(-1, assetManager);
        
        bulletAppState.getPhysicsSpace().add(worldController.getWorldCollisionControll());
        bulletAppState.getPhysicsSpace().add(player.getControl());
        player.getControl().setPhysicsLocation(new Vector3f(0, 10, 0));
        
        Player p2 = new Player(-1, assetManager);
        bulletAppState.getPhysicsSpace().add(p2.getControl());
        p2.getControl().setPhysicsLocation(new Vector3f(5, 10, 0));
        worldController.attachObject(p2.getModel());
        
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        bulletAppState.getPhysicsSpace().addCollisionObject(p2.getControl());
        
        connector = new ServerConnector(this);
    }
    
    /**
     * Starts the actual game eg. the game loop.
     */
    public void startGame() {
    	stateManager.detach(menuAppState);
    	gamestate = GameState.RUNNING;
    	stateManager.attach(gameInputAppState);
    	bulletAppState.setEnabled(true);
    	connector.connectToServer("localhost", 6173);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	player.setId(connector.getConnectionId());
    	players.put(player.getId(), player);
    	connector.addMessageListener(this);
    	connector.startInputSender();
    }
    
    /**
     * Resumes the game after pausing.
     */
    public void resumeGame() {
    	stateManager.detach(menuAppState);
    	gamestate = GameState.RUNNING;
    	stateManager.attach(gameInputAppState);
    	bulletAppState.setEnabled(true);
    	connector.startInputSender();
    }
    
    /**
     * pauses the game and opens the menu.
     */
    public void pauseGame() {
    	bulletAppState.setEnabled(false);
    	stateManager.detach(gameInputAppState);
    	gamestate = GameState.PAUSED;
    	stateManager.attach(menuAppState);
    	connector.stopInputSender();
    }
    
    public void stopGame() {
    	connector.stopInputSender();
    	connector.disconnectFromServer();
    	stop();
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
        updateWorldState();
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
    
    private void updateWorldState() {
    	while (!updateQueue.isEmpty()) {
    		Message msg = updateQueue.poll();
    		if(msg instanceof WorldStateMessage) {
    			WorldStateMessage worldState = (WorldStateMessage) msg;

    			for (PlayerPhysic physic : worldState.getPlayerPhysics()) {
    				Player p = players.get(physic.getId());
    				if(p == null) {
    					p = new Player(physic.getId(), assetManager);
    					p.getControl().setPhysicsLocation(new Vector3f(5, 5, 5));
    					players.put(p.getId(), p);
    					bulletAppState.getPhysicsSpace().add(p.getControl());
    					worldController.attachObject(p.getModel());
    				}
    				if(p != null) {
    					p.setExactLoc(physic.getTranslation());
    					p.getControl().setViewDirection(physic.getOrientation());
    				}
    			}

    			for (BulletPhysic physic : worldState.getBulletPhysics()) {
    				Bullet b = bullets.get(physic.getId());
    				if(b != null) {	
    					b.setExactLoc(physic.getTranslation());
    					b.getControl().setPhysicsLocation(physic.getTranslation());
    					b.getControl().setLinearVelocity(physic.getVelocity());
    				}
    			}
    		}else if(msg instanceof AttackMessage) {
    			AttackMessage attack = (AttackMessage) msg;
    			int playerid = attack.getPlayerid();
    			BulletPhysic physic = attack.getPhysic();
    			Bullet b = new Bullet(physic.getId(), playerid);
    			b.getControl().setPhysicsLocation(physic.getTranslation());
    			bullets.put(b.getId(), b);
    			bulletAppState.getPhysicsSpace().add(b.getControl());
    			b.getModel().setLocalTranslation(physic.getTranslation());
    			worldController.attachObject(b.getModel());
    		}else if(msg instanceof HitMessage) {
    			HitMessage hit = (HitMessage) msg;
    			hitPlayer(hit.getPlayerid());
    		}
    	}
    }

	@Override
	public void messageReceived(Client source, Message m) {
    		updateQueue.offer(m);
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
				cam.setLocation(p.getControl().getPhysicsLocation());
			}
		}
		
    }

    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
	public void onAction(String name, boolean isPressed, float tpf) {
    	if (!isPressed) {
            return;
        }
    	if (name.equals(SimpleApplication.INPUT_MAPPING_HIDE_STATS)){
            boolean show = showFps;
            setDisplayFps(!show);
            setDisplayStatView(!show);
        }
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
    		if(other.getName().startsWith("player")) {
    			// Hit Player not here. only when message from server.
    		}else {
    			worldController.detachObject(bullet);
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
    	}
    }

	/**
     * Creates a ball and throws it i view direction.
     */
	public void attack() {
    	AttackMessage msg = new AttackMessage();
    	this.connector.sendMessage(msg);
    }
	
	private void hitPlayer(int id) {
		Player p = players.get(id);
		if(p == null) {
			return;
		}
		double hp = p.getHealthpoints();
		hp -= 20;
		if(hp <= 0) {
			hp = 0;
			this.killPlayer(id);
		}
		p.setHealthpoints(hp);
	}
	
	private void killPlayer(int id) {
		if(id == player.getId()) {
			pauseGame();
			
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
    public void setDisplayStatView(boolean show) {
        statsView.setEnabled(show);
        statsView.setCullHint(show ? CullHint.Never : CullHint.Always);
    }

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
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
}
