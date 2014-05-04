package de.encala.cydonia.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

import de.encala.cydonia.game.ExtendedSettingsDialog.SelectionListener;
import de.encala.cydonia.game.appstates.GameInputAppState;
import de.encala.cydonia.game.appstates.GeneralInputAppState;
import de.encala.cydonia.game.appstates.MenuController;
import de.encala.cydonia.game.equipment.ClientEquipment;
import de.encala.cydonia.game.equipment.ClientEquipmentFactory;
import de.encala.cydonia.game.equipment.ClientPicker;
import de.encala.cydonia.game.level.Flag;
import de.encala.cydonia.game.level.FlagFactory;
import de.encala.cydonia.game.level.Flube;
import de.encala.cydonia.game.level.SpawnPoint;
import de.encala.cydonia.game.level.WorldController;
import de.encala.cydonia.game.level.WorldObject;
import de.encala.cydonia.game.player.Player;
import de.encala.cydonia.game.player.PlayerController;
import de.encala.cydonia.share.GameConfig;
import de.encala.cydonia.share.events.AddEvent;
import de.encala.cydonia.share.events.BeamEvent;
import de.encala.cydonia.share.events.ChooseTeamEvent;
import de.encala.cydonia.share.events.ConfigEvent;
import de.encala.cydonia.share.events.ConnectionDeniedEvent;
import de.encala.cydonia.share.events.ConnectionInitEvent;
import de.encala.cydonia.share.events.ConnectionLostEvent;
import de.encala.cydonia.share.events.Event;
import de.encala.cydonia.share.events.EventListener;
import de.encala.cydonia.share.events.EventMachine;
import de.encala.cydonia.share.events.FlagEvent;
import de.encala.cydonia.share.events.InputEvent;
import de.encala.cydonia.share.events.KillEvent;
import de.encala.cydonia.share.events.MarkEvent;
import de.encala.cydonia.share.events.PhaseEvent;
import de.encala.cydonia.share.events.PickupEvent;
import de.encala.cydonia.share.events.PlaceEvent;
import de.encala.cydonia.share.events.PlayerJoinEvent;
import de.encala.cydonia.share.events.PlayerQuitEvent;
import de.encala.cydonia.share.events.PushEvent;
import de.encala.cydonia.share.events.RemoveEvent;
import de.encala.cydonia.share.events.RespawnEvent;
import de.encala.cydonia.share.events.RestartRoundEvent;
import de.encala.cydonia.share.events.RoundEndedEvent;
import de.encala.cydonia.share.events.SwapEvent;
import de.encala.cydonia.share.events.WorldStateEvent;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.FlagInfo;
import de.encala.cydonia.share.messages.InitialStateMessage;
import de.encala.cydonia.share.messages.InputMessage;
import de.encala.cydonia.share.messages.JoinMessage;
import de.encala.cydonia.share.messages.LocationUpdatedMessage;
import de.encala.cydonia.share.messages.MoveableInfo;
import de.encala.cydonia.share.messages.PlayerInfo;
import de.encala.cydonia.share.messages.PlayerPhysic;
import de.encala.cydonia.share.messages.SpawnPointInfo;
import de.encala.cydonia.share.messages.ViewDirMessage;
import de.encala.cydonia.share.messages.WorldState;
import de.encala.cydonia.share.player.InputCommand;
import de.encala.cydonia.share.player.PlayerInputState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * GameController is the central controller of the game. It loads all
 * components, changes appStates, organizes the communication between the
 * components.
 * 
 * @author encala
 */
public class GameController extends Application implements
PhysicsCollisionListener, EventListener, ScreenController {

	public static final String TEXTURES_PATH = "de/encala/cydonia/textures/";
	public static final String APPTITLE = "Cydonia 43";
	
	public static final boolean DEBUG = false;

	public static float PLAYER_SPEED = 5f;
	public static float PHYSICS_ACCURACY = (1f / 192);

	public static Transform ROTATE90LEFT = new Transform(
			new Quaternion().fromRotationMatrix(new Matrix3f(1, 0,
					FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));

	/**
	 * The time in seconds it should take to compensate a deviation from the
	 * accurate (=server defined) physical location of an object.
	 */
	private static final float SMOOTHING = 0.2f;
	private static final float MAXPOSDEVIATION = 1f;

	public static void main(String[] args) {
		String ip = "";
		if (args.length > 0) {
			ip = args[0];
		}
		System.out.println("ip: " + ip);

		GameController controller = new GameController();
		controller.start(ip);
	}

	protected boolean showSettings = true;

	protected Node guiNode = new Node("Gui Node");

	private boolean showFps = false;
	protected float secondCounter = 0.0f;
	protected int frameCounter = 0;
	protected BitmapText fpsText;
	protected BitmapFont guiFont;
	protected StatsView statsView;

	private Node beamNode;

	private GameInputAppState gameInputAppState;

	private MenuController menuController;

	private ClientEquipmentFactory equipmentFactory;

	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;

	private String serverAddress = "";
	private boolean network;

	private Nifty nifty;
	private TextField playerNameInput;
	private DropDown<String> teamInput;

	private Player player;

	private AudioNode pickupSound;
	private AudioNode placeSound;

	private ServerConnector connector;

	private LocationUpdatedMessage latestLocationUpdate;

	private long roundStartTime;

	private long gameOverTime = 0;

	private GameState gamestate;

	private ClientState clientState;

	private int winTeam;
	private int team1score;
	private int team2score;
	private GameConfig gameConfig;
	private WorldController worldController;
	private PlayerController playerController;
	private BulletAppState bulletAppState;
	private EventMachine eventMachine;
	private ConcurrentLinkedQueue<Event> eventQueue;

	public GameController() {
		super();
		
		gameConfig = new GameConfig(true);
	}
	
	public void start(String server) {
		if(server == null || server.isEmpty()) {
			this.serverAddress = null;
			this.network = false;
		} else {
			this.serverAddress = server;
			this.network = true;
		}
		this.start();
	}

	@Override
	public void start() {
		// set some default settings in-case
		// settings dialog is not shown
		boolean loadSettings = false;
		if (settings == null) {
			setSettings(new AppSettings(true));
			loadSettings = true;
			settings.setTitle(APPTITLE);
		}
		try {
			BufferedImage favicon = ImageIO.read(GameController.class.getResourceAsStream("/de/encala/cydonia/gui/favicon.png"));
			settings.setIcons(new BufferedImage[]{favicon});
		} catch (IOException e) {
			e.printStackTrace();
		}
		settings.setSettingsDialogImage("/de/encala/cydonia/gui/logo43_05.jpg");

		// show settings dialog
		if (showSettings) {

			/* *********************************************** */
			/* show own settings dialog instead of JMESystem's */
			/* *********************************************** */
			if (!this.showSettingsDialog(settings, loadSettings)) {
				return;
			}
		}

		// limit frame rate
		settings.setFrameRate(100);

		// re-setting settings they can have been merged from the registry.
		setSettings(settings);
		super.start();
	}

	/**
	 * Shows settings dialog. Copied from JmeDesktopSystem, because couldn't
	 * change the used dialog other way.
	 * 
	 * @param sourceSettings
	 * @param loadFromRegistry
	 * @return
	 */
	private boolean showSettingsDialog(AppSettings sourceSettings,
			final boolean loadFromRegistry) {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Cannot run from EDT");
		}

		final AppSettings settings = new AppSettings(false);
		settings.copyFrom(sourceSettings);
		String iconPath = sourceSettings.getSettingsDialogImage();
		final URL iconUrl = JmeSystem.class.getResource(iconPath
				.startsWith("/") ? iconPath : "/" + iconPath);
		if (iconUrl == null) {
			throw new AssetNotFoundException(
					sourceSettings.getSettingsDialogImage());
		}

		final AtomicBoolean done = new AtomicBoolean();
		final AtomicInteger result = new AtomicInteger();
		final Object lock = new Object();

		final SelectionListener selectionListener = new SelectionListener() {

			public void onSelection(int selection) {
				synchronized (lock) {
					done.set(true);
					result.set(selection);
					lock.notifyAll();
				}
			}
		};
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				synchronized (lock) {
					ExtendedSettingsDialog dialog = new ExtendedSettingsDialog(
							settings, iconUrl, loadFromRegistry);
					dialog.setSelectionListener(selectionListener);
					dialog.showDialog();
				}
			}
		});

		synchronized (lock) {
			while (!done.get()) {
				try {
					lock.wait();
				} catch (InterruptedException ex) {
				}
			}
		}

		sourceSettings.copyFrom(settings);

		return result.get() == ExtendedSettingsDialog.APPROVE_SELECTION;
	}

	@Override
	public void stop(boolean waitfor) {
		cleanup();
		
		super.stop(waitfor);
		// System.exit(0);
	}

	@Override
	public void initialize() {
		super.initialize();
		
		eventMachine = new EventMachine();
		eventQueue = new ConcurrentLinkedQueue<Event>();

		bulletAppState = new BulletAppState();
		bulletAppState.setEnabled(false);
		bulletAppState.setThreadingType(ThreadingType.PARALLEL);
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setMaxSubSteps(16);
		bulletAppState.getPhysicsSpace().setAccuracy(PHYSICS_ACCURACY);
		bulletAppState.getPhysicsSpace().addCollisionListener(this);

		FlagFactory.init(assetManager);

		worldController = new WorldController(assetManager,
				bulletAppState.getPhysicsSpace());
		eventMachine.registerListener(this);

		playerController = new PlayerController(assetManager, this);

		setPauseOnLostFocus(false);

		this.equipmentFactory = new ClientEquipmentFactory(this);

		guiNode.setQueueBucket(Bucket.Gui);
		guiNode.setCullHint(CullHint.Never);
		loadFPSText();

		if (DEBUG) {
			loadStatsView();
		}

		guiViewPort.attachScene(guiNode);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();
		guiViewPort.addProcessor(niftyDisplay);

		menuController = new MenuController(this);
		this.setClientstate(ClientState.LOADING);
		this.setGamestate(GameState.DOWN);
		menuController.actualizeScreen();

		connector = new ServerConnector(this, getEventMachine());
		
		gameInputAppState = new GameInputAppState(this, this.network);

		GeneralInputAppState generalInputAppState = new GeneralInputAppState(
				this);
		stateManager.attach(generalInputAppState);

		// getBulletAppState().setDebugEnabled(true);

		viewPort.attachScene(getWorldController().getRootNode());
		// viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));

		if (settings.getInteger("shadowLevel") > 0) {
			for (Light l : getWorldController().getLights()) {
				if (l instanceof DirectionalLight) {
					DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(
							assetManager, 1024,
							settings.getInteger("shadowLevel"));
					shadowRenderer.setLight((DirectionalLight) l);
					shadowRenderer.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
					shadowRenderer.setShadowCompareMode(CompareMode.Hardware);
					viewPort.addProcessor(shadowRenderer);
				}
			}
		}

		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

		FogFilter fog = new FogFilter();
		fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 0.5f));
		fog.setFogDistance(100);
		fog.setFogDensity(1.5f);
		// fpp.addFilter(fog);

		// SSAOFilter ssaoFilter = new SSAOFilter();
		// fpp.addFilter(ssaoFilter);

		// FXAAFilter fxaaFilter = new FXAAFilter();
		// fpp.addFilter(fxaaFilter);

		BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
		fpp.addFilter(bloom);

		// if (renderer.getCaps().contains(Caps.GLSL100)){
		// //fpp.setNumSamples(4);
		// CartoonEdgeFilter toon=new CartoonEdgeFilter();
		// toon.setEdgeColor(ColorRGBA.Yellow);
		// fpp.addFilter(toon);
		// viewPort.addProcessor(fpp);
		// }

		beamNode = new Node("Beams");
		getWorldController().attachObject(beamNode);
		// refractionProcessor = new SimpleRefractionProcessor(assetManager);
		// refractionProcessor.setRefractionScene(getWorldController().getRootNode());
		// refractionProcessor.setDebug(true);
		// refractionProcessor.setRenderSize(256, 256);
		// refractionProcessor.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		// viewPort.addProcessor(refractionProcessor);

		viewPort.addProcessor(fpp);

		cam.setFrustumPerspective(45f,
				(float) cam.getWidth() / cam.getHeight(), 0.3f, 300f);

		pickupSound = new AudioNode(assetManager,
				"de/encala/cydonia/sounds/pickup_mono.wav", false);
		pickupSound.setLooping(false);
		pickupSound.setPositional(true);
		pickupSound.setLocalTranslation(Vector3f.ZERO);
		pickupSound.setVolume(0.5f);
		pickupSound.setDirectional(false);
		pickupSound.setRefDistance(5f);
		getWorldController().attachObject(pickupSound);

		placeSound = new AudioNode(assetManager,
				"de/encala/cydonia/sounds/place_mono.wav", false);
		placeSound.setLooping(false);
		placeSound.setPositional(true);
		placeSound.setLocalTranslation(Vector3f.ZERO);
		placeSound.setVolume(0.5f);
		placeSound.setDirectional(false);
		placeSound.setRefDistance(5f);
		getWorldController().attachObject(placeSound);

		if(this.network) {
			connector.connectToServer(serverAddress, 6173);
		}
	}

	/**
	 * Starts the actual game eg. the game loop.
	 */
	public void startGame(String level) {
		getBulletAppState().setEnabled(true);

		InitialStateMessage init = new InitialStateMessage();
		connector.sendMessage(init);
	}
	
	protected void cleanup() {
		stateManager.detach(gameInputAppState);
		connector.disconnectFromServer();
		getEventMachine().stop();
		bulletAppState.setEnabled(false);
	}

	private void setWorldState(WorldState state) {
		this.roundStartTime = System.currentTimeMillis()
				- state.getPassedRoundTime();

		getGameConfig().copyFrom(state.getConfig());

		team1score = state.getTeam1score();
		team2score = state.getTeam2score();

		getWorldController().unloadCurrentWorld();

		for (PlayerInfo info : state.getPlayers()) {
			final int playerid = info.getPlayerid();
			Player p = getPlayerController().getPlayer(playerid);
			if (p == null) {
				p = getPlayerController().createNew(info.getPlayerid());
			}
			p.setName(info.getName());
			getPlayerController().setTeam(p, info.getTeam());
			p.setAlive(info.isAlive());
			getPlayerController().setHealthpoints(p, info.getHealthpoints());
			p.setScores(info.getScores());

			ClientEquipment cur = p.getCurrentEquipment();
			if (cur != null && cur.getGeometry() != null) {
				p.getNode().detachChild(cur.getGeometry());
			}
			p.getEquips().clear();
			for (EquipmentInfo ei : info.getEquipInfos()) {
				ClientEquipment equip = getEquipmentFactory()
						.create(ei.getTypeName());
				if (equip != null) {
					equip.setPlayer(p);
					equip.loadInfo(ei);
					p.getEquips().add(equip);
				}
			}
			p.setCurrEquip(info.getCurrEquip());

			p.getControl().setPhysicsLocation(info.getLocation());
			p.getControl().setViewDirection(info.getOrientation());

			if (p.isAlive()) {
				getWorldController().attachPlayer(
						getPlayerController().getPlayer(playerid));
			}
		}
		for (MoveableInfo info : state.getFlubes()) {
			final Vector3f loc = info.getLocation();
			final boolean inWorld = info.isInWorld();
			final long id = info.getId();
			final Vector3f origin = info.getOrigin();
			final int type = info.getType();
			Flube m = getWorldController().addNewFlube(id, origin, type);
			getWorldController().detachFlube(m);
			m.getControl().setPhysicsLocation(loc);
			if (inWorld) {
				getWorldController().attachFlube(m);
			}
		}

		for (FlagInfo info : state.getFlags()) {
			final int flagid = info.getId();
			final int playerid = info.getPlayerid();
			final Vector3f origin = info.getOrigin();
			final int team = info.getTeam();
			final boolean inBase = info.isInBase();
			Flag f = getWorldController().addNewFlag(flagid, origin, team);
			if (!inBase && playerid >= 0) {
				takeFlag(getPlayerController().getPlayer(playerid), f);
			} else if (inBase) {
				returnFlag(f);
			}
		}

		for (SpawnPointInfo info : state.getSpawnPoints()) {
			final int id = info.getId();
			final Vector3f position = info.getPosition();
			final int team = info.getTeam();
			SpawnPoint spawn = getWorldController().addNewSpawnPoint(id,
					position, team);
			if ("editor".equalsIgnoreCase(getGameConfig().getString(
					"gamemode"))) {
				spawn.getNode().setCullHint(CullHint.Inherit);
			} else {
				spawn.getNode().setCullHint(CullHint.Always);
			}
		}

		// getWorldController().setUpWorldLights();
		if ("editor".equalsIgnoreCase(getGameConfig().getString("gamemode"))) {
			getWorldController().setAmbientBrightness(0.3f);
		} else {
			getWorldController().setAmbientBrightness(0.15f);
		}

		if (getClientstate() == ClientState.LOADING) {
			setClientstate(ClientState.LOBBY);
			menuController.actualizeScreen();
		}
	}

	public void joinGame() {
		String playername = this.playerNameInput.getRealText();
		int team = this.teamInput.getSelectedIndex() + 1;

		setGamestate(GameState.SPECTATE);
		setClientstate(ClientState.GAME);
		menuController.actualizeScreen();

		JoinMessage join = new JoinMessage(connector.getConnectionId(),
				playername);
		connector.sendMessage(join);

		InputMessage chooseteam = null;
		if (team == 1) {
			chooseteam = new InputMessage(connector.getConnectionId(),
					InputCommand.CHOOSETEAM1, true);
		} else if (team == 2) {
			chooseteam = new InputMessage(connector.getConnectionId(),
					InputCommand.CHOOSETEAM2, true);
		}
		connector.sendMessage(chooseteam);
	}

	/**
	 * Resumes the game after pausing.
	 */
	public void resumeGame() {
		setGamestate(GameState.RUNNING);
		// stateManager.attach(gameInputAppState);
//		startInputSender();
		menuController.actualizeScreen();
	}

	/**
	 * pauses the game and opens the menu.
	 */
	public void openMenu() {
		stateManager.detach(gameInputAppState);
		clientState = ClientState.MENU;
		menuController.actualizeScreen();
	}

	public void closeMenu() {
		if (getGamestate() == GameState.RUNNING
				|| getGamestate() == GameState.SPECTATE) {
			stateManager.attach(gameInputAppState);
		}
		clientState = ClientState.GAME;
		menuController.actualizeScreen();
	}

	public void stopGame() {
		stop();
	}

	public void gameOver() {
		gameOverTime = System.currentTimeMillis();
//		stopInputSender();
		setGamestate(GameState.SPECTATE);
		menuController.actualizeScreen();
		menuController
				.showMessage("You were beamed into another dimension.\nPress 'Fire' to respawn at your base!");
	}

	@Override
	public void update() {
		super.update(); // makes sure to execute AppTasks
		
		handleEvents();
		
		if (speed == 0 || paused) {
			return;
		}

		float tpf = timer.getTimePerFrame() * speed;

		if (showFps) {
			secondCounter += timer.getTimePerFrame();
			frameCounter++;
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
		useLatestLocationUpdate();
		movePlayers(tpf);
		menuController.actualizeScreen();
		menuController.updateHUD();

		// update world and gui
		getWorldController().updateLogicalState(tpf);
		guiNode.updateLogicalState(tpf);
		getWorldController().updateGeometricState();
		guiNode.updateGeometricState();

		// render states
		stateManager.render(renderManager);
		renderManager.render(tpf, context.isRenderable());
		stateManager.postRender();
	}

	private void useLatestLocationUpdate() {
		LocationUpdatedMessage worldState;
		if (latestLocationUpdate != null) {
			synchronized (latestLocationUpdate) {
				worldState = latestLocationUpdate;
				latestLocationUpdate = null;
			}

			for (PlayerPhysic physic : worldState.getPlayerPhysics()) {
				Player p = getPlayerController().getPlayer(physic.getId());
				if (p != null) {
					p.setExactLoc(physic.getTranslation());
					p.setViewDir(physic.getOrientation());
					getPlayerController().setHealthpoints(p,
							physic.getHealthpoints());
				}
			}
		}
	}

	protected void handleEvent(Event e) {
		if (e instanceof ConnectionDeniedEvent) {
			System.out.println("Server denied connection! Reason: '"
					+ ((ConnectionDeniedEvent) e).getReason() + "'");
			setClientstate(ClientState.LOADING);
			menuController.actualizeScreen();
			connector.disconnectFromServer();
			stopGame();
		} else if (e instanceof ConnectionInitEvent) {
			startGame(((ConnectionInitEvent) e).getLevel());
		} else if (e instanceof ConnectionLostEvent) {
			JOptionPane
					.showMessageDialog(
							null,
							"Connection to the server was lost! \nThe game will quit now.",
							"Connection lost", JOptionPane.OK_OPTION);
			stopGame();
		} else if (e instanceof KillEvent) {
			KillEvent kill = (KillEvent) e;
			Player p = getPlayerController().getPlayer(kill.getPlayerid());
			killPlayer(p);
		} else if (e instanceof PickupEvent) {
			PickupEvent pickup = (PickupEvent) e;
			Player p = getPlayerController().getPlayer(pickup.getPlayerid());
			Flube flube = getWorldController().getFlube(pickup.getMoveableid());
			pickup(p, flube);
		} else if (e instanceof PlaceEvent) {
			PlaceEvent place = (PlaceEvent) e;
			Player p = getPlayerController().getPlayer(place.getPlayerid());
			Flube f = getWorldController().getFlube(place.getMoveableid());
			Vector3f loc = place.getLocation();
			place(p, f, loc);
		} else if (e instanceof MarkEvent) {
			MarkEvent mark = (MarkEvent) e;
			WorldObject o = null;
			if (mark.getTargetPlayerId() >= 0) {
				o = getPlayerController().getPlayer(mark.getTargetPlayerId());
			} else if (mark.getTargetFlubeId() > 0) {
				o = getWorldController().getFlube(mark.getTargetFlubeId());
			}

			if (mark.isUnmark()) {
				o.removeMark(getPlayerController()
						.getPlayer(mark.getPlayerId()));
				if (player != null && mark.getPlayerId() == player.getId()) {
					unhighlight(o);
				}
			} else {
				o.addMark(getPlayerController().getPlayer(mark.getPlayerId()));
				if (player != null && mark.getPlayerId() == player.getId()) {
					highlight(o);
				}
			}
		} else if (e instanceof SwapEvent) {
			SwapEvent swap = (SwapEvent) e;
			WorldObject a = null;
			if (swap.getPlayerA() >= 0) {
				a = getPlayerController().getPlayer(swap.getPlayerA());
			} else if (swap.getFlubeA() > 0) {
				a = getWorldController().getFlube(swap.getFlubeA());
			}
			WorldObject b = null;
			if (swap.getPlayerB() >= 0) {
				b = getPlayerController().getPlayer(swap.getPlayerB());
			} else if (swap.getFlubeB() > 0) {
				b = getWorldController().getFlube(swap.getFlubeB());
			}
			swap(a, b);
		} else if (e instanceof PlayerJoinEvent) {
			PlayerJoinEvent join = (PlayerJoinEvent) e;
			int playerid = join.getPlayerId();
			joinPlayer(playerid, join.getPlayername());
		} else if (e instanceof ChooseTeamEvent) {
			ChooseTeamEvent choose = (ChooseTeamEvent) e;
			Player p = getPlayerController().getPlayer(choose.getPlayerId());
			chooseTeam(p, choose.getTeam());
		} else if (e instanceof RespawnEvent) {
			RespawnEvent respawn = (RespawnEvent) e;
			Player p = getPlayerController().getPlayer(respawn.getPlayerid());
			respawn(p);
		} else if (e instanceof PlayerQuitEvent) {
			PlayerQuitEvent quit = (PlayerQuitEvent) e;
			Player p = getPlayerController().getPlayer(quit.getPlayerId());
			quitPlayer(p);
		} else if (e instanceof InputEvent) {
			InputEvent input = (InputEvent) e;
			// only use inputs from other players, not our own inputs, that are
			// sent back to us from the server
			if ((player != null && player.getId() != input.getPlayerid())
					|| !InputCommand.usedirect.contains(input.getClass())) {
				Player p = getPlayerController().getPlayer(input.getPlayerid());
				if (p != null) {
					getPlayerController().handleInput(p, input.getCommand(),
							input.isValue());
				}
			}
		} else if (e instanceof RestartRoundEvent) {
			for (Player p : getPlayerController().getAllPlayers()) {
				if (p.isAlive()) {
					killPlayer(p);
				}
				getPlayerController().reset(p);
			}
			getWorldController().resetWorld();
			team1score = 0;
			team2score = 0;
			this.roundStartTime = System.currentTimeMillis();
			menuController.clearEventPanel();
			menuController.updateScoreboard();
			getBulletAppState().setEnabled(true);
		} else if (e instanceof RoundEndedEvent) {
			getBulletAppState().setEnabled(false);
			RoundEndedEvent roundEnded = (RoundEndedEvent) e;
			for (Player p : getPlayerController().getAllPlayers()) {
				p.setInputState(new PlayerInputState());
			}
			winTeam = roundEnded.getWinteam();
			setGamestate(GameState.ROUNDOVER);
			menuController.actualizeScreen();
		} else if (e instanceof FlagEvent) {
			FlagEvent flagev = (FlagEvent) e;
			if (flagev.getType() == FlagEvent.TAKE) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				Player p = getPlayerController()
						.getPlayer(flagev.getPlayerid());
				takeFlag(p, f);
			} else if (flagev.getType() == FlagEvent.SCORE) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				Player p = getPlayerController()
						.getPlayer(flagev.getPlayerid());
				scoreFlag(p, f);
				menuController.updateScoreboard();
			} else if (flagev.getType() == FlagEvent.RETURN) {
				Flag f = getWorldController().getFlag(flagev.getFlagid());
				returnFlag(f);
			}
		} else if (e instanceof PhaseEvent) {
			PhaseEvent phase = (PhaseEvent) e;
			Player attacker = getPlayerController().getPlayer(
					phase.getAttackerId());
			Player victim = getPlayerController()
					.getPlayer(phase.getVictimId());
			phase(attacker, victim, phase.getDamage());
		} else if (e instanceof PushEvent) {
			PushEvent push = (PushEvent) e;
			Player attacker = getPlayerController().getPlayer(
					push.getAttackerId());
			Player victim = getPlayerController().getPlayer(push.getVictimId());
			push(attacker, victim, push.getForce());
		} else if (e instanceof BeamEvent) {
			BeamEvent beam = (BeamEvent) e;
			Player p = getPlayerController().getPlayer(beam.getSourceid());
			Player victim = getPlayerController().getPlayer(beam.getTargetid());
			beam(p, victim);
		} else if (e instanceof RemoveEvent) {
			RemoveEvent remove = (RemoveEvent) e;
			if ("flube".equalsIgnoreCase(remove.getObjectType())) {
				Flube f = getWorldController().getFlube(remove.getObjectid());
				getWorldController().removeFlube(f);
			} else if ("flag".equalsIgnoreCase(remove.getObjectType())) {
				Flag f = getWorldController().getFlag(
						(int) remove.getObjectid());
				getWorldController().removeFlag(f);
			} else if ("spawnpoint".equalsIgnoreCase(remove.getObjectType())) {
				SpawnPoint sp = getWorldController().getSpawnPoint(
						(int) remove.getObjectid());
				getWorldController().removeSpawnPoint(sp);
			}
		} else if (e instanceof AddEvent) {
			AddEvent add = (AddEvent) e;
			if ("flube".equalsIgnoreCase(add.getObjectType())) {
				Flube f = getWorldController().addNewFlube(add.getObjectid(),
						add.getLocation(), add.getObjectSpec());
				getWorldController().attachFlube(f);
			} else if ("flag".equalsIgnoreCase(add.getObjectType())) {
				Flag f = getWorldController().addNewFlag(
						(int) add.getObjectid(), add.getLocation(),
						add.getObjectSpec());
			} else if ("spawnpoint".equalsIgnoreCase(add.getObjectType())) {
				SpawnPoint sp = getWorldController().addNewSpawnPoint(
						(int) add.getObjectid(), add.getLocation(),
						add.getObjectSpec());
				if ("editor".equalsIgnoreCase(getGameConfig().getString(
						"gamemode"))) {
					sp.getNode().setCullHint(CullHint.Dynamic);
				} else {
					sp.getNode().setCullHint(CullHint.Always);
				}
			}
		} else if (e instanceof ConfigEvent) {
			ConfigEvent event = (ConfigEvent) e;
			if ("gamemode".equalsIgnoreCase(event.getKey())) {
				switchGameMode((String) event.getNewValue());
			} else if ("scorelimit".equalsIgnoreCase(event.getKey())) {
				getGameConfig().putObject("scorelimit",
						(Integer) event.getNewValue());
			}
		} else if (e instanceof WorldStateEvent) {
			setWorldState(((WorldStateEvent) e).getWorldState());
		}
	}

	public void setlatestLocationUpdate(LocationUpdatedMessage update) {
		latestLocationUpdate = update;
	}

	/**
	 * Moves the player according to user input state.
	 * 
	 * @param tpf
	 *            time per frame
	 */
	private void movePlayers(float tpf) {

		for (Player p : getPlayerController().getAllPlayers()) {
			if (player != null && p.getId() == player.getId()) {
				p.setViewDir(cam.getDirection());
				listener.setLocation(cam.getLocation());
				listener.setRotation(cam.getRotation());
			}

			if (p.isAlive()) {
				Vector3f viewDir = p.getViewDir().clone();
				if ("ctf".equalsIgnoreCase(getGameConfig().getString(
						"gamemode"))) {
					viewDir.setY(0).normalizeLocal();
				}
				Vector3f viewLeft = new Vector3f();
				ROTATE90LEFT.transformVector(viewDir.clone().setY(0)
						.normalizeLocal(), viewLeft);

				walkDirection.set(0, 0, 0);
				if (p.getInputState().isLeft())
					walkDirection.addLocal(viewLeft);
				if (p.getInputState().isRight())
					walkDirection.addLocal(viewLeft.negate());
				if (p.getInputState().isForward())
					walkDirection.addLocal(viewDir);
				if (p.getInputState().isBack())
					walkDirection.addLocal(viewDir.negate());

				walkDirection.normalizeLocal().multLocal(PLAYER_SPEED);
				if ("editor".equalsIgnoreCase(getGameConfig().getString(
						"gamemode"))) {
					walkDirection.multLocal(1.5f);
				}

				Vector3f deviation = p.getExactLoc().subtract(
						p.getControl().getPhysicsLocation());
				if (deviation.length() > MAXPOSDEVIATION) {

					p.getControl().warp(p.getExactLoc());
				} else {
					Vector3f correction = p.getExactLoc()
							.subtract(p.getControl().getPhysicsLocation())
							.mult(SMOOTHING);
					walkDirection.addLocal(correction);
				}

				walkDirection.multLocal(PHYSICS_ACCURACY);
				p.getControl().setWalkDirection(walkDirection);
			}

			if (p.getId() == connector.getConnectionId()) {
				cam.setLocation(p.getEyePosition());
			}
		}

	}

	@Override
	public void collision(PhysicsCollisionEvent e) {

	}

	public void killPlayer(Player p) {
		if (p == null)
			return;
		if (p.getFlag() != null) {
			returnFlag(p.getFlag());
		}
		p.setGameOverTime(System.currentTimeMillis());
		worldController.detachPlayer(p);
		p.setAlive(false);

		getPlayerController().playDieAnim(p);
		if (p.getId() == player.getId()) {
			gameOver();
		}
	}

	protected boolean respawn(final Player p) {
		if (p == null)
			return false;
	
		if ("ctf".equalsIgnoreCase(getGameConfig().getString("gamemode"))) {
			SpawnPoint sp = worldController.getSpawnPointForTeam(p.getTeam());
			if (sp != null) {
				playerController.setHealthpoints(p, 100);
				playerController.resetEquips(p);
				p.setAlive(true);
				p.getControl().zeroForce();
				p.getControl().setPhysicsLocation(sp.getPosition());
				worldController.attachPlayer(p);
				if (player != null && p.getId() == player.getId()) {
					p.getModel().setCullHint(CullHint.Always);
					resumeGame();
				}
				return true;
			}
		} else if ("editor".equalsIgnoreCase(getGameConfig().getString(
				"gamemode"))) {
			playerController.setHealthpoints(p, 100);
			p.setAlive(true);
			p.getControl().zeroForce();
			p.getControl().setPhysicsLocation(Vector3f.UNIT_Y);
			worldController.attachPlayer(p);
			if (player != null && p.getId() == player.getId()) {
				p.getModel().setCullHint(CullHint.Always);
				resumeGame();
			}
			return true;
		}
	
		return false;
	}

	protected void joinPlayer(int playerid, String playername) {
		Player p = playerController.createNew(playerid);
		p.setName(playername);
	
		getPlayerController().setDefaultEquipment(p);
		
		if (playerid == connector.getConnectionId()) {
			player = getPlayerController().getPlayer(playerid);
			stateManager.attach(gameInputAppState);
			
		}
		menuController.displayEvent(playername + " joined the game");
		menuController.updateScoreboard();
	}

	protected void quitPlayer(Player p) {
		if (p == null)
			return;
		if (p.getFlag() != null) {
			returnFlag(p.getFlag());
		}
		worldController.detachPlayer(p);
		playerController.removePlayer(p.getId());

		menuController.displayEvent(p.getName() + " left the game");
	}

	protected void chooseTeam(Player p, int team) {
		if (p == null)
			return;
		playerController.setTeam(p, team);
	}

	protected void beam(Player p, Player victim) {
		p.setScores(p.getScores() + 1);
		killPlayer(victim);
		
		menuController
				.displayEvent(p.getName() + " beamed " + victim.getName());
	}

	protected void scoreFlag(Player p, Flag flag) {
		p.setFlag(null);
		flag.setPlayer(null);
		p.setScores(p.getScores() + 3);
		returnFlag(flag);

		menuController.displayEvent(p.getName() + " scored.");
		if (p.getTeam() == 1) {
			team1score++;
		} else if (p.getTeam() == 2) {
			team2score++;
		}
	}

	protected void pickup(Player p, Flube f) {
		if (f != null) {
			Vector3f loc = f.getModel().getWorldTranslation();
			pickupSound.setLocalTranslation(loc);
			pickupSound.playInstance();
		}

		if (f != null) {
			getWorldController().detachFlube(f);
			if (p != null) {
				if (p.getCurrentEquipment() instanceof ClientPicker) {
					ClientPicker picker = (ClientPicker) p.getCurrentEquipment();
					picker.getRepository().add(f);
				}
			}
		}
	}

	protected void place(Player p, Flube f, Vector3f loc) {
		f.getControl().setPhysicsLocation(loc);
		getWorldController().attachFlube(f);
		if (p != null) {
			if (p.getCurrentEquipment() instanceof ClientPicker) {
				ClientPicker picker = (ClientPicker) p.getCurrentEquipment();
				picker.getRepository().remove(f);
			}
		}

		if (f != null) {
			Vector3f l = f.getModel().getWorldTranslation();
			placeSound.setLocalTranslation(l);
			placeSound.playInstance();
		}
	}

	protected void swap(WorldObject a, WorldObject b) {
		a.removeAllMarks();
		b.removeAllMarks();
	
		Vector3f posA = null;
		if (a instanceof Player) {
			posA = ((Player) a).getControl().getPhysicsLocation();
		} else if (a instanceof Flube) {
			posA = ((Flube) a).getControl().getPhysicsLocation();
		}
	
		Vector3f posB = null;
		if (b instanceof Player) {
			posB = ((Player) b).getControl().getPhysicsLocation();
		} else if (b instanceof Flube) {
			posB = ((Flube) b).getControl().getPhysicsLocation();
		}
	
		if (posA != null && posB != null) {
			if (a instanceof Player) {
				if (((Player) a).getFlag() != null) {
					returnFlag(((Player) a).getFlag());
				}
				((Player) a).getControl().warp(posB);
			} else if (a instanceof Flube) {
				getWorldController().detachFlube((Flube) a);
				((Flube) a).getControl().setPhysicsLocation(
						getWorldController().rasterize(posB));
				getWorldController().attachFlube((Flube) a);
			}
	
			if (b instanceof Player) {
				if (((Player) b).getFlag() != null) {
					returnFlag(((Player) b).getFlag());
				}
				((Player) b).getControl().warp(posA);
			} else if (b instanceof Flube) {
				getWorldController().detachFlube((Flube) b);
				((Flube) b).getControl().setPhysicsLocation(
						getWorldController().rasterize(posA));
				getWorldController().attachFlube((Flube) b);
			}
		}

		unhighlight(a);
		unhighlight(b);
	}

	protected void highlight(WorldObject o) {
		// Spatial model = o.getModel();
		// AmbientLight highLight = new AmbientLight();
		// highLight.setColor(ColorRGBA.White.mult(0.5f));
		// highLight.setName("HighLight");
		// model.addLight(highLight);
		if (o instanceof Flube) {
			((Flube) o).setHighlighted(true);
		}
	}

	protected void unhighlight(WorldObject o) {
		// for(Light l : o.getModel().getLocalLightList()) {
		// if(l.getName().equals("HighLight")) {
		// o.getModel().removeLight(l);
		// }
		// }
		if (o instanceof Flube) {
			((Flube) o).setHighlighted(false);
		}
	}

	public long getRemainingTime() {
		long passedTime = System.currentTimeMillis() - roundStartTime;
		return getGameConfig().getLong("timelimit") * 1000 - passedTime;
	}

	public int getWinTeam() {
		return winTeam;
	}

	public String getScores() {
		StringBuilder sb_team1 = new StringBuilder();
		StringBuilder sb_team2 = new StringBuilder();
		for (Player p : getPlayerController().getAllPlayers()) {
			if (p.getTeam() == 1) {
				sb_team1.append("\n" + p.getName() + "\t\t" + p.getScores());
			} else if (p.getTeam() == 2) {
				sb_team2.append("\n" + p.getName() + "\t\t" + p.getScores());
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Team 1 \t\t Scores");
		sb.append(sb_team1);
		sb.append("\n\n\n\n");
		sb.append("Team 2 \t\t Scores");
		sb.append(sb_team2);
		return sb.toString();
	}

	public void scoreboard(boolean show) {
		if (show) {
			menuController.showScoreboard();
		} else {
			menuController.hideScoreboard();
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
		if (showFps) {
			fpsText.setCullHint(CullHint.Inherit);
		} else {
			fpsText.setCullHint(CullHint.Always);
		}
		guiNode.attachChild(fpsText);
	}

	/**
	 * Attaches Statistics View to guiNode and displays it on the screen above
	 * FPS statistics line.
	 */
	public void loadStatsView() {
		statsView = new StatsView("Statistics View", assetManager,
				renderer.getStatistics());
		// move it up so it appears above fps text
		statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		guiNode.attachChild(statsView);
	}

	/**
	 * Sets the displayFPS property.
	 * 
	 * @param show
	 *            if true fps are painted
	 */
	public void setDisplayFps(boolean show) {
		showFps = show;
		fpsText.setCullHint(show ? CullHint.Never : CullHint.Always);
	}

	/**
	 * Sets the displayStats property.
	 * 
	 * @param show
	 *            if true stats are painted
	 */
	public void setDisplayStatView() {
		boolean show = !statsView.isEnabled();
		statsView.setEnabled(show);
		statsView.setCullHint(show ? CullHint.Never : CullHint.Always);
	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.playerNameInput = screen.findNiftyControl("playername",
				TextField.class);
		this.teamInput = screen.findNiftyControl("team", DropDown.class);

		this.teamInput.addItem("Team 1");
		this.teamInput.addItem("Team 2");
	}

	@Override
	public void onEndScreen() {
	}

	@Override
	public void onStartScreen() {
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

	public ClientEquipmentFactory getEquipmentFactory() {
		return this.equipmentFactory;
	}

	/**
	 * Retrieves nifty.
	 * 
	 * @return nifty object
	 */
	public Nifty getNifty() {
		return nifty;
	}

	/**
	 * Retrieves guiNode
	 * 
	 * @return guiNode Node object
	 */
	public Node getGuiNode() {
		return guiNode;
	}

	public void sendViewDir() {
		ViewDirMessage msg = new ViewDirMessage();
		msg.setPlayerid(getPlayer().getId());
		msg.setViewDir(getPlayer().getViewDir());

		connector.sendMessage(msg);
	}

	private void switchGameMode(String mode) {
		getGameConfig().putString("gamemode", mode);
		if ("editor".equalsIgnoreCase(mode)) {
			for (Player p : getPlayerController().getAllPlayers()) {
				getPlayerController().setDefaultEquipment(p);
				p.getControl().setGravity(0);
			}
			for (SpawnPoint sp : getWorldController().getAllSpawnPoints()) {
				sp.getNode().setCullHint(CullHint.Inherit);
			}
			getWorldController().setAmbientBrightness(0.5f);
		} else if ("ctf".equalsIgnoreCase(mode)) {
			for (Player p : getPlayerController().getAllPlayers()) {
				getPlayerController().setDefaultEquipment(p);
				p.getControl().setGravity(25);
			}
			for (SpawnPoint sp : getWorldController().getAllSpawnPoints()) {
				sp.getNode().setCullHint(CullHint.Always);
			}
			getWorldController().setAmbientBrightness(0.25f);
		}
	}

	public GameState getGamestate() {
		return gamestate;
	}

	/**
	 * @param gamestate
	 *            the gamestate to set
	 */
	public void setGamestate(GameState gamestate) {
		this.gamestate = gamestate;
	}

	public ClientState getClientstate() {
		return clientState;
	}

	public void setClientstate(ClientState clientState) {
		this.clientState = clientState;
	}

	public int getTeam1score() {
		return team1score;
	}

	public int getTeam2score() {
		return team2score;
	}

	public long getGameOverTime() {
		return gameOverTime;
	}
	
	public void switchFPS() {
		showFps = !showFps;
		if (showFps) {
			fpsText.setCullHint(CullHint.Inherit);
		} else {
			fpsText.setCullHint(CullHint.Always);
		}
	}

	/**
	 * @return the gameConfig
	 */
	public GameConfig getGameConfig() {
		return gameConfig;
	}

	/**
	 * @return the bulletAppState
	 */
	public BulletAppState getBulletAppState() {
		return bulletAppState;
	}

	/**
	 * @return the worldController
	 */
	public WorldController getWorldController() {
		return worldController;
	}

	/**
	 * @return the playerController
	 */
	public PlayerController getPlayerController() {
		return playerController;
	}

	/**
	 * @return the eventMachine
	 */
	public EventMachine getEventMachine() {
		return eventMachine;
	}

	public ServerConnector getConnector() {
		return connector;
	}
	
	public boolean isNetwork() {
		return this.network;
	}

	protected void returnFlag(Flag flag) {
		getWorldController().returnFlag(flag);
	}

	protected void takeFlag(Player p, Flag flag) {
		flag.setInBase(false);
		Node parent = flag.getModel().getParent();
		if (parent != null) {
			parent.detachChild(flag.getModel());
		}
		flag.getModel().setLocalTranslation(0, 1, 0);
		// flag.getModel().setLocalScale(Vector3f.UNIT_XYZ.divide(p.getModel().getLocalScale()));
		p.getNode().attachChild(flag.getModel());
		p.setFlag(flag);
		flag.setPlayer(p);
		System.out.println("takenflag");
	}

	protected void phase(Player attacker, Player victim, float damage) {
		getPlayerController().setHealthpoints(victim,
				victim.getHealthpoints() - damage);
		if (victim.getHealthpoints() <= 0) {
			beam(attacker, victim);
		}
	}

	protected void push(Player attacker, Player victim, Vector3f force) {
		victim.getControl().applyCentralForce(force);
	}

	private void handleEvents() {
		Event e = null;
		while ((e = eventQueue.poll()) != null) {
			this.handleEvent(e);
		}
	}

	@Override
	public void newEvent(Event e) {
		eventQueue.offer(e);
	}

	
}
