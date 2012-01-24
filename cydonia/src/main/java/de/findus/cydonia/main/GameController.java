package de.findus.cydonia.main;

import java.util.HashMap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsView;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;

import de.findus.cydonia.appstates.GameInputAppState;
import de.findus.cydonia.appstates.MenuAppState;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.server.Player;
import de.findus.cydonia.server.PlayerPhysic;
import de.findus.cydonia.server.WorldStateUpdate;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * GameController is the central controller of the game.
 * It loads all components, changes appStates, organizes the communication between the components.
 * 
 * @author Findus
 */
public class GameController extends Application implements ActionListener, ScreenController, MessageListener<Client>{

	public static final String TEXTURES_PATH = "de/findus/cydonia/textures/";
	public static float MAX_STEP_HEIGHT = 0.2f;
    public static float PLAYER_SPEED = 5f;
    public static float PHYSICS_ACCURACY = (1f / 240);
    
    protected boolean showSettings = true;
    
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
    private ServerConnector connector;
    
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
    	connector.stopInputSender();
    	connector.disconnectFromServer();
    	super.stop();
    }

    @Override
    public void initialize() {
        super.initialize();

        players = new HashMap<Integer, Player>();
        
        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        loadFPSText();
        loadStatsView();
        worldController = new WorldController();
        worldController.loadWorld(assetManager);
        viewPort.attachScene(worldController.getRootNode());
        guiViewPort.attachScene(guiNode);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
    			inputManager,
    			audioRenderer,
    			guiViewPort);
    	nifty = niftyDisplay.getNifty();
    	guiViewPort.addProcessor(niftyDisplay);

    	menuAppState = new MenuAppState(this);
    	stateManager.attach(menuAppState);
    	
    	gameInputAppState = new GameInputAppState(this);
    	

    	bulletAppState = new BulletAppState();
    	bulletAppState.setEnabled(false);
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
        bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
        
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
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
        
        connector = new ServerConnector(this);
    }
    
    /**
     * Starts the actual game eg. the game loop.
     */
    public void startGame() {
    	stateManager.detach(menuAppState);
    	stateManager.attach(gameInputAppState);
    	bulletAppState.setEnabled(true);
    	connector.connectToServer("localhost", 6173);
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
    	stateManager.attach(menuAppState);
    	connector.stopInputSender();
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
        movePlayer(tpf);
        
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
    
    @Override
	public void messageReceived(Client source, Message m) {
    	if (m instanceof WorldStateUpdate) {
    		// do something with the message
    		WorldStateUpdate worldState = (WorldStateUpdate) m;
    		for (PlayerPhysic physic : worldState.getPlayerPhysics()) {
				Player p = players.get(physic.getId());
				if(p == null) {
					p = new Player(physic.getId(), assetManager);
					players.put(p.getId(), p);
					bulletAppState.getPhysicsSpace().add(p.getControl());
					worldController.attachObject(p.getModel());
				}
				p.getControl().setPhysicsLocation(physic.getTranslation());
				p.getControl().setViewDirection(physic.getOrientation());
			}
    	}
    }

    /**
     * Moves the player according to user input state.
     * @param tpf time per frame
     */
	private void movePlayer(float tpf) {
        Vector3f camDir = cam.getDirection().clone().setY(0).normalizeLocal();
        Vector3f camLeft = cam.getLeft().clone().setY(0).normalizeLocal();
        walkDirection.set(0, 0, 0);
        if(player.getInputState().isLeft()) walkDirection.addLocal(camLeft);
        if(player.getInputState().isRight()) walkDirection.addLocal(camLeft.negate());
        if(player.getInputState().isForward()) walkDirection.addLocal(camDir);
        if(player.getInputState().isBack()) walkDirection.addLocal(camDir.negate());
        
        walkDirection.normalizeLocal().multLocal(PHYSICS_ACCURACY * PLAYER_SPEED);
        
        player.getControl().setWalkDirection(walkDirection);
        cam.setLocation(player.getControl().getPhysicsLocation());
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

    /**
     * Creates a ball and throws it i view direction.
     */
	public void attack() {
    	Material mat_felsen = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex2 = assetManager.loadTexture(TEXTURES_PATH + "felsen1.jpg");
        mat_felsen.setTexture("ColorMap", tex2);
    	
    	Sphere sphere = new Sphere(10, 10, 0.1f);
    	Geometry bullet = new Geometry("bullet", sphere);
    	bullet.setMaterial(mat_felsen);
    	worldController.attachObject(bullet);
    	/** Position the cannon ball  */
    	bullet.setLocalTranslation(cam.getLocation().add(cam.getDirection().normalize()));
    	/** Make the ball physcial with a mass > 0.0f */
    	RigidBodyControl phy_bullet = new RigidBodyControl(2f);
    	/** Add physical ball to physics space. */
    	bullet.addControl(phy_bullet);
    	bulletAppState.getPhysicsSpace().add(phy_bullet);
    	/** Accelerate the physcial ball to shoot it. */
    	phy_bullet.setLinearVelocity(cam.getDirection().normalize().mult(25));
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
