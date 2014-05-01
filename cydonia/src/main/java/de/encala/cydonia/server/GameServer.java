/**
 * 
 */
package de.encala.cydonia.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.JDOMException;
import org.xml.sax.InputSource;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.network.message.CompressedMessage;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import de.encala.cydonia.server.equipment.ServerEquipment;
import de.encala.cydonia.server.equipment.ServerEquipmentFactory;
import de.encala.cydonia.server.equipment.ServerPicker;
import de.encala.cydonia.server.equipment.ServerSwapper;
import de.encala.cydonia.server.player.ServerPlayer;
import de.encala.cydonia.server.player.ServerPlayerController;
import de.encala.cydonia.server.world.MapXMLParser;
import de.encala.cydonia.server.world.ServerFlag;
import de.encala.cydonia.server.world.ServerFlagFactory;
import de.encala.cydonia.server.world.ServerFlube;
import de.encala.cydonia.server.world.ServerMap;
import de.encala.cydonia.server.world.ServerSpawnPoint;
import de.encala.cydonia.server.world.ServerWorldController;
import de.encala.cydonia.server.world.ServerWorldObject;
import de.encala.cydonia.share.GameConfig;
import de.encala.cydonia.share.events.AddEvent;
import de.encala.cydonia.share.events.BeamEvent;
import de.encala.cydonia.share.events.ChooseTeamEvent;
import de.encala.cydonia.share.events.ConfigEvent;
import de.encala.cydonia.share.events.ConnectionAddedEvent;
import de.encala.cydonia.share.events.ConnectionRemovedEvent;
import de.encala.cydonia.share.events.Event;
import de.encala.cydonia.share.events.EventListener;
import de.encala.cydonia.share.events.EventMachine;
import de.encala.cydonia.share.events.FlagEvent;
import de.encala.cydonia.share.events.InputEvent;
import de.encala.cydonia.share.events.KillEvent;
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
import de.encala.cydonia.share.messages.ConnectionInitMessage;
import de.encala.cydonia.share.messages.FlagInfo;
import de.encala.cydonia.share.messages.FlubeStatePartMessage;
import de.encala.cydonia.share.messages.InitialStateMessage;
import de.encala.cydonia.share.messages.LocationUpdatedMessage;
import de.encala.cydonia.share.messages.MoveableInfo;
import de.encala.cydonia.share.messages.PlayerInfo;
import de.encala.cydonia.share.messages.SpawnPointInfo;
import de.encala.cydonia.share.messages.WorldState;
import de.encala.cydonia.share.player.InputCommand;
import de.encala.cydonia.share.player.PlayerInputState;

/**
 * @author encala
 * 
 */
public class GameServer extends Application implements
PhysicsCollisionListener, EventListener {

	public static final String APPTITLE = "Cydonia Server";
	
	public static float PLAYER_SPEED = 5f;
	public static float PHYSICS_ACCURACY = (1f / 192);

	public static Transform ROTATE90LEFT = new Transform(
			new Quaternion().fromRotationMatrix(new Matrix3f(1, 0,
					FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));

	public static final int RELOAD_TIME = 500;

	public static final float MAX_PICK_RANGE = 20;

	public static final float MAX_PLACE_RANGE = 20;

	public static final boolean FREE_PLACING = false;

	public static final String MAPEXTENSION = ".mfx";

	private static ConsoleWriter CWRITER = ConsoleWriter.getWriter();

	public static void main(String[] args) {
		boolean window = false;

		for (String arg : args) {
			if ("--window".equalsIgnoreCase(arg)) {
				window = true;
			}
		}

		GameServer gameServer = new GameServer(window);
		gameServer.start();
	}

	private String mapsDir;

	private ServerConfigFrame configFrame;

	private Thread locationSenderLoop;

	private Thread consoleListener;

	private GameplayController gameplayController;
	
	/**
	 * Used for moving players. Allocated only once and reused for performance
	 * reasons.
	 */
	private Vector3f walkDirection = new Vector3f();

	private NetworkController networkController;

	private ServerEquipmentFactory equipmentFactory;

	private Collection<ServerStateListener> stateListeners;

	private GameConfig gameConfig;

	private ServerWorldController worldController;

	private ServerPlayerController playerController;

	private BulletAppState bulletAppState;

	private EventMachine eventMachine;

	private ConcurrentLinkedQueue<Event> eventQueue;

	public GameServer(boolean window) {
		super();
		
		gameConfig = new GameConfig(true);

		this.stateListeners = new LinkedList<ServerStateListener>();

		if (window) {
			configFrame = new ServerConfigFrame(this);
			configFrame.pack();
			CWRITER.addConsole(configFrame);
		} else {
			ConsoleWriter.getWriter().addConsole(new Console() {
				@Override
				public void writeLine(String line) {
					System.out.println(line);
				}
			});

			consoleListener = new Thread() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						BufferedReader console = new BufferedReader(
								new InputStreamReader(System.in));
						String zeile = null;
						try {
							zeile = console.readLine();
							handleCommand(zeile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			consoleListener.start();
		}
	}

	public void setConfigFrameVisible(boolean visible) {
		configFrame.setVisible(visible);
	}

	@Override
	public void start() {
		if (settings == null) {
			settings = new AppSettings(true);
			settings.setTitle(APPTITLE);
		}
		super.start(JmeContext.Type.Headless);
	}

	@Override
	public void stop(boolean waitfor) {
		CWRITER.writeLine("shutting down ...");
		cleanup();
		super.stop(waitfor);
		// System.exit(0);
	}

	protected void cleanup() {
		bulletAppState.setEnabled(false);
		eventMachine.stop();
		
		networkController.stop();
		locationSenderLoop.interrupt();
		gameplayController.dispose();
		if (consoleListener != null && consoleListener.isAlive()) {
			consoleListener.interrupt();
		}
		if (configFrame != null) {
			configFrame.setVisible(true);
		}
		// configFrame.dispose();
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

		ServerFlagFactory.init(assetManager);

		worldController = new ServerWorldController(assetManager,
				bulletAppState.getPhysicsSpace());
		eventMachine.registerListener(this);

		playerController = new ServerPlayerController(assetManager, this);

		this.equipmentFactory = new ServerEquipmentFactory(this);

		// loadMap(getGameConfig().getString("map"));

		networkController = new NetworkController(this, getEventMachine());

		getBulletAppState().setEnabled(true);
		locationSenderLoop = new Thread(new LocationSenderLoop());
		locationSenderLoop.start();

		gameplayController = new GameplayController(getEventMachine(),
				getGameConfig());
		gameplayController.restartRound();
	}

	@Override
	public void update() {
		super.update(); // makes sure to execute AppTasks
		
		handleEvents();
		
		if (speed == 0 || paused) {
			return;
		}

		float tpf = timer.getTimePerFrame() * speed;

		// update states
		stateManager.update(tpf);

		// update game specific things
		movePlayers(tpf);

		// update world and gui
		getWorldController().updateLogicalState(tpf);
		getWorldController().updateGeometricState();

		stateManager.render(renderManager);
		renderManager.render(tpf, context.isRenderable());
		stateManager.postRender();
	}

	protected void handleEvent(Event e) {
		if (e instanceof ConnectionAddedEvent) {
			connectionAdded(((ConnectionAddedEvent) e).getClientid());
		} else if (e instanceof ConnectionRemovedEvent) {
			connectionRemoved(((ConnectionRemovedEvent) e).getClientid());
		} else if (e instanceof RestartRoundEvent) {
			for (ServerPlayer p : getPlayerController().getAllPlayers()) {
				if (p.isAlive()) {
					killPlayer(p);
				}
				getPlayerController().reset(p);
			}
			getWorldController().resetWorld();
			// for (ServerPlayer p : getPlayerController().getAllPlayers()) {
			// respawn(p);
			// }
			getBulletAppState().setEnabled(true);
		} else if (e instanceof RoundEndedEvent) {
			getBulletAppState().setEnabled(false);
			RoundEndedEvent roundEnded = (RoundEndedEvent) e;
			for (ServerPlayer p : getPlayerController().getAllPlayers()) {
				p.setInputState(new PlayerInputState());
				if (p.getId() == roundEnded.getWinteam()) {
					p.setScores(p.getScores() + 1);
				}
			}
		} else if (e instanceof FlagEvent) {
			FlagEvent flagev = (FlagEvent) e;
			if (flagev.getType() == FlagEvent.TAKE) {
				ServerFlag f = getWorldController().getFlag(flagev.getFlagid());
				ServerPlayer p = getPlayerController()
						.getPlayer(flagev.getPlayerid());
				takeFlag(p, f);
			} else if (flagev.getType() == FlagEvent.SCORE) {
				ServerFlag f = getWorldController().getFlag(flagev.getFlagid());
				ServerPlayer p = getPlayerController()
						.getPlayer(flagev.getPlayerid());
				scoreFlag(p, f);
			} else if (flagev.getType() == FlagEvent.RETURN) {
				ServerFlag f = getWorldController().getFlag(flagev.getFlagid());
				returnFlag(f);
			}
		} else if (e instanceof KillEvent) {
			KillEvent kill = (KillEvent) e;
			ServerPlayer p = getPlayerController().getPlayer(kill.getPlayerid());
			killPlayer(p);
		} else if (e instanceof PhaseEvent) {
			PhaseEvent phase = (PhaseEvent) e;
			ServerPlayer attacker = getPlayerController().getPlayer(
					phase.getAttackerId());
			ServerPlayer victim = getPlayerController()
					.getPlayer(phase.getVictimId());
			phase(attacker, victim, phase.getDamage());
		} else if (e instanceof PushEvent) {
			PushEvent push = (PushEvent) e;
			ServerPlayer attacker = getPlayerController().getPlayer(
					push.getAttackerId());
			ServerPlayer victim = getPlayerController().getPlayer(push.getVictimId());
			push(attacker, victim, push.getForce());
		} else if (e instanceof BeamEvent) {
			BeamEvent beam = (BeamEvent) e;
			ServerPlayer p = getPlayerController().getPlayer(beam.getSourceid());
			ServerPlayer victim = getPlayerController().getPlayer(beam.getTargetid());
			beam(p, victim);
		} else if (e instanceof PickupEvent) {
			PickupEvent pickup = (PickupEvent) e;
			ServerPlayer p = getPlayerController().getPlayer(pickup.getPlayerid());
			ServerFlube f = getWorldController().getFlube(pickup.getMoveableid());
			pickup(p, f);
		} else if (e instanceof PlaceEvent) {
			PlaceEvent place = (PlaceEvent) e;
			ServerPlayer p = getPlayerController().getPlayer(place.getPlayerid());
			ServerFlube f = getWorldController().getFlube(place.getMoveableid());
			Vector3f loc = place.getLocation();
			place(p, f, loc);
		} else if (e instanceof RemoveEvent) {
			RemoveEvent remove = (RemoveEvent) e;
			if ("flube".equalsIgnoreCase(remove.getObjectType())) {
				ServerFlube f = getWorldController().getFlube(remove.getObjectid());
				getWorldController().removeFlube(f);
			} else if ("flag".equalsIgnoreCase(remove.getObjectType())) {
				ServerFlag f = getWorldController().getFlag(
						(int) remove.getObjectid());
				getWorldController().removeFlag(f);
			} else if ("spawnpoint".equalsIgnoreCase(remove.getObjectType())) {
				ServerSpawnPoint sp = getWorldController().getSpawnPoint(
						(int) remove.getObjectid());
				getWorldController().removeSpawnPoint(sp);
			}
		} else if (e instanceof AddEvent) {
			AddEvent add = (AddEvent) e;
			if ("flube".equalsIgnoreCase(add.getObjectType())) {
				ServerFlube f = getWorldController().addNewFlube(add.getObjectid(),
						add.getLocation(), add.getObjectSpec());
				getWorldController().attachFlube(f);
			} else if ("flag".equalsIgnoreCase(add.getObjectType())) {
				ServerFlag f = getWorldController().addNewFlag(
						(int) add.getObjectid(), add.getLocation(),
						add.getObjectSpec());
			} else if ("spawnpoint".equalsIgnoreCase(add.getObjectType())) {
				ServerSpawnPoint sp = getWorldController().addNewSpawnPoint(
						(int) add.getObjectid(), add.getLocation(),
						add.getObjectSpec());
			}
		} else if (e instanceof SwapEvent) {
			SwapEvent swap = (SwapEvent) e;
			ServerWorldObject a = null;
			if (swap.getPlayerA() >= 0) {
				a = getPlayerController().getPlayer(swap.getPlayerA());
			} else if (swap.getFlubeA() > 0) {
				a = getWorldController().getFlube(swap.getFlubeA());
			}
			ServerWorldObject b = null;
			if (swap.getPlayerB() >= 0) {
				b = getPlayerController().getPlayer(swap.getPlayerB());
			} else if (swap.getFlubeB() > 0) {
				b = getWorldController().getFlube(swap.getFlubeB());
			}
			swap(a, b);
		} else if (e instanceof PlayerQuitEvent) {
			PlayerQuitEvent quit = (PlayerQuitEvent) e;
			ServerPlayer p = getPlayerController().getPlayer(quit.getPlayerId());
			quitPlayer(p);
		} else if (e instanceof ConfigEvent) {
			ConfigEvent conf = (ConfigEvent) e;
			String key = conf.getKey();
			if("map".equalsIgnoreCase(key)) {
				loadMap(getGameConfig().getString(key));
			}else if("gamemode".equalsIgnoreCase(key)) {
				switchGameMode(getGameConfig().getString(key));
			}
		}
	}

	private void movePlayers(float tpf) {
		if (gameplayController.getGameState() != ServerGameState.RUNNING) {
			return;
		}
		for (ServerPlayer p : this.getPlayerController().getAllPlayers()) {
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

				walkDirection.normalizeLocal().multLocal(
						PHYSICS_ACCURACY * PLAYER_SPEED);
				if ("editor".equalsIgnoreCase(getGameConfig().getString(
						"gamemode"))) {
					walkDirection.multLocal(1.5f);
				}

				p.getControl().setWalkDirection(walkDirection);

				if (getWorldController().isBelowBottomOfPlayground(p)
						&& "ctf".equalsIgnoreCase(getGameConfig().getString(
								"gamemode"))) {
					KillEvent ev = new KillEvent(p.getId(), true);
					getEventMachine().fireEvent(ev);
				}
			}
		}
	}

	@Override
	public void collision(PhysicsCollisionEvent e) {
		// collisionen müssen nur im spielmodus "ctf" berechnet werden
		if (!"ctf".equalsIgnoreCase(getGameConfig().getString("gamemode")))
			return;

		Spatial other = null;
		Spatial target = null;

		if (e.getNodeA() != null) {

			if (e.getNodeA().getUserData("FlagBase") != null
					&& ((Boolean) e.getNodeA().getUserData("FlagBase"))
							.booleanValue() == true) {
				target = e.getNodeA();
				other = e.getNodeB();
			}
		}
		if (e.getNodeB() != null) {

			if (e.getNodeB().getUserData("FlagBase") != null
					&& ((Boolean) e.getNodeB().getUserData("FlagBase"))
							.booleanValue() == true) {
				target = e.getNodeB();
				other = e.getNodeA();
			}
		}

		if (target != null && other != null) {
			if (other.getName().startsWith("player")) {
				ServerPlayer p = getPlayerController().getPlayer(
						Integer.parseInt(other.getName().substring(6)));
				if (p != null) {
					if (p.getTeam() == ((Integer) target.getUserData("team"))
							.intValue()) { // own target
						if (p.getFlag() != null) {
							int stolenflagid = p.getFlag().getId();
							ServerFlag f = getWorldController().getFlag(
									((Integer) target.getUserData("id"))
											.intValue());
							if (f != null && f.isInBase()) {
								p.setFlag(null);
								System.out.println("Team " + p.getTeam()
										+ " scored");
								FlagEvent event = new FlagEvent(
										FlagEvent.SCORE, p.getId(),
										stolenflagid, true);
								getEventMachine().fireEvent(event);
							}
						}
					} else { // opponents target
						if (p.getFlag() == null) {
							ServerFlag f = getWorldController().getFlag(
									((Integer) target.getUserData("id"))
											.intValue());
							if (f != null && f.isInBase()) {
								f.setInBase(false);
								System.out.println("Team " + p.getTeam()
										+ " took flag");
								FlagEvent event = new FlagEvent(FlagEvent.TAKE,
										p.getId(), f.getId(), true);
								getEventMachine().fireEvent(event);
							}
						}
					}
				}
			}
		}
	}

	protected void joinPlayer(int playerid, String playername) {
		ServerPlayer p = playerController.createNew(playerid);
		p.setName(playername);
	
		getPlayerController().setDefaultEquipment(p);

		CWRITER.writeLine(playername + " joined");

		PlayerJoinEvent join = new PlayerJoinEvent(playerid, playername, true);
		getEventMachine().fireEvent(join);
	}

	protected void quitPlayer(ServerPlayer p) {
		if (p == null)
			return;
		if (p.getFlag() != null) {
			returnFlag(p.getFlag());
		}
		worldController.detachPlayer(p);
		playerController.removePlayer(p.getId());
		if (p != null) {
			CWRITER.writeLine(p.getName() + " quit");
		}
	}

	protected boolean respawn(ServerPlayer p) {
		if (p == null)
			return false;
	
		if ("ctf".equalsIgnoreCase(getGameConfig().getString("gamemode"))) {
			ServerSpawnPoint sp = worldController.getSpawnPointForTeam(p.getTeam());
			if (sp != null) {
				playerController.setHealthpoints(p, 100);
				playerController.resetEquips(p);
				p.setAlive(true);
				p.getControl().zeroForce();
				p.getControl().setPhysicsLocation(sp.getPosition());
				worldController.attachPlayer(p);
				CWRITER.writeLine(p.getName() + " respawned");

				RespawnEvent respawn = new RespawnEvent(p.getId(), true);
				getEventMachine().fireEvent(respawn);
				return true;
			}
		} else if ("editor".equalsIgnoreCase(getGameConfig().getString(
				"gamemode"))) {
			playerController.setHealthpoints(p, 100);
			p.setAlive(true);
			p.getControl().zeroForce();
			p.getControl().setPhysicsLocation(Vector3f.UNIT_Y);
			worldController.attachPlayer(p);
			CWRITER.writeLine(p.getName() + " respawned");

			RespawnEvent respawn = new RespawnEvent(p.getId(), true);
			getEventMachine().fireEvent(respawn);
			return true;
		}
	
		return false;
	}

	protected void beam(ServerPlayer p, ServerPlayer victim) {
		p.setScores(p.getScores() + 1);
		killPlayer(victim);
		CWRITER.writeLine(p.getName() + " beamed " + victim.getName());
	}

	protected void swap(ServerWorldObject a, ServerWorldObject b) {
		for (ServerPlayer p : a.getAllMarks()) {
			for (ServerEquipment e : p.getEquips()) {
				if (e instanceof ServerSwapper) {
					((ServerSwapper) e).resetMark(a);
					((ServerSwapper) e).resetMark(b);
				}
			}
		}

		a.removeAllMarks();
		b.removeAllMarks();
	
		Vector3f posA = null;
		if (a instanceof ServerPlayer) {
			posA = ((ServerPlayer) a).getControl().getPhysicsLocation();
		} else if (a instanceof ServerFlube) {
			posA = ((ServerFlube) a).getControl().getPhysicsLocation();
		}
	
		Vector3f posB = null;
		if (b instanceof ServerPlayer) {
			posB = ((ServerPlayer) b).getControl().getPhysicsLocation();
		} else if (b instanceof ServerFlube) {
			posB = ((ServerFlube) b).getControl().getPhysicsLocation();
		}
	
		if (posA != null && posB != null) {
			if (a instanceof ServerPlayer) {
				if (((ServerPlayer) a).getFlag() != null) {
					returnFlag(((ServerPlayer) a).getFlag());
				}
				((ServerPlayer) a).getControl().warp(posB);
			} else if (a instanceof ServerFlube) {
				getWorldController().detachFlube((ServerFlube) a);
				((ServerFlube) a).getControl().setPhysicsLocation(
						getWorldController().rasterize(posB));
				getWorldController().attachFlube((ServerFlube) a);
			}
	
			if (b instanceof ServerPlayer) {
				if (((ServerPlayer) b).getFlag() != null) {
					returnFlag(((ServerPlayer) b).getFlag());
				}
				((ServerPlayer) b).getControl().warp(posA);
			} else if (b instanceof ServerFlube) {
				getWorldController().detachFlube((ServerFlube) b);
				((ServerFlube) b).getControl().setPhysicsLocation(
						getWorldController().rasterize(posA));
				getWorldController().attachFlube((ServerFlube) b);
			}
		}
	}

	protected void scoreFlag(ServerPlayer p, ServerFlag flag) {
		p.setFlag(null);
		flag.setPlayer(null);
		p.setScores(p.getScores() + 3);
		returnFlag(flag);
		// TODO: score team
		System.out.println("scoredflag");
		if (p != null) {
			gameplayController.playerScored(p);
			CWRITER.writeLine(p.getName() + " scored the flag");
		}
	}

	protected void returnFlag(ServerFlag flag) {
		getWorldController().returnFlag(flag);

		String teamname = "";
		if (flag.getTeam() == 1) {
			teamname = "Blue";
		} else if (flag.getTeam() == 2) {
			teamname = "Red";
		}
		CWRITER.writeLine(teamname + " flag returned");
	}

	public void handlePlayerInput(int playerid, InputCommand command,
			boolean value) {
		ServerPlayer p = getPlayerController().getPlayer(playerid);
		switch (command) {
		case USEPRIMARY:
			if (gameplayController.getGameState() == ServerGameState.RUNNING) {
				if (p.isAlive()) {
					getPlayerController().handleInput(p, command, value);

					// InputEvent event = new InputEvent(p.getId(), command,
					// value, true);
					// getEventMachine().fireEvent(event);
				} else {
					if (value && p.getTeam() > 0) {
						long timeToRespawn = p.getGameOverTime()
								+ (getGameConfig().getLong("respawntime") * 1000)
								- System.currentTimeMillis();
						if (timeToRespawn < 0) {
							respawn(p);
						}
					}
				}
			}
			break;
		case USESECONDARY:
			if (gameplayController.getGameState() == ServerGameState.RUNNING) {
				if (p.isAlive()) {
					getPlayerController().handleInput(p, command, value);

					// InputEvent event = new InputEvent(p.getId(), command,
					// value, true);
					// getEventMachine().fireEvent(event);
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
			PlayerQuitEvent quit = new PlayerQuitEvent(p.getId(), true);
			getEventMachine().fireEvent(quit);
			break;

		default:
			if (gameplayController.getGameState() == ServerGameState.RUNNING) {
				getPlayerController().handleInput(p, command, value);
				InputEvent event = new InputEvent(p.getId(), command, value,
						true);
				getEventMachine().fireEvent(event);
			}
			break;
		}
	}

	protected void chooseTeam(ServerPlayer p, int team) {
		if (p == null)
			return;
		playerController.setTeam(p, team);

		ChooseTeamEvent event = new ChooseTeamEvent(p.getId(), team, true);
		getEventMachine().fireEvent(event);

		if (getPlayerController().getPlayerCount() == 1) {
			gameplayController.restartRound();
		}
	}

	public WorldState getWorldState() {
		PlayerInfo[] playerinfos = new PlayerInfo[getPlayerController()
				.getPlayerCount()];
		int i = 0;
		for (ServerPlayer p : getPlayerController().getAllPlayers()) {
			playerinfos[i] = new PlayerInfo(p);
			i++;
		}

		Collection<ServerFlube> flubes = getWorldController().getAllFlubes();
		MoveableInfo[] flubeinfos = new MoveableInfo[flubes.size()];
		int j = 0;
		for (ServerFlube m : flubes) {
			flubeinfos[j] = new MoveableInfo(m);
			j++;
		}

		Collection<ServerFlag> flags = getWorldController().getAllFlags();
		FlagInfo[] flaginfos = new FlagInfo[flags.size()];
		int k = 0;
		for (ServerFlag f : flags) {
			flaginfos[k] = new FlagInfo(f);
			k++;
		}

		Collection<ServerSpawnPoint> spawnPoints = getWorldController()
				.getAllSpawnPoints();
		SpawnPointInfo[] spinfos = new SpawnPointInfo[spawnPoints.size()];
		int l = 0;
		for (ServerSpawnPoint sp : spawnPoints) {
			spinfos[l] = new SpawnPointInfo(sp);
			l++;
		}

		long passedTime = System.currentTimeMillis()
				- gameplayController.getRoundStartTime();

		WorldState state = new WorldState();
		state.setPassedRoundTime(passedTime);
		state.setTeam1score(gameplayController.getTeam1score());
		state.setTeam2score(gameplayController.getTeam2score());
		state.setPlayers(playerinfos);
		state.setFlubes(flubeinfos);
		state.setFlags(flaginfos);
		state.setSpawnPoints(spinfos);
		state.setconfig(getGameConfig());

		return state;
	}

	public void sendInitialState(int playerid) {
		WorldState state = getWorldState();
		MoveableInfo[] infos = state.getFlubes();
		int partsize = 300;
		LinkedList<MoveableInfo[]> list = new LinkedList<MoveableInfo[]>();
		MoveableInfo[] part = new MoveableInfo[partsize];
		int j = 0;
		for (int i = 0; i < infos.length; i++) {
			part[j] = infos[i];
			j++;
			if (j >= partsize) {
				list.add(part);
				part = new MoveableInfo[partsize];
				j = 0;
			}
		}
		list.add(part);

		state.setFlubes(new MoveableInfo[0]);
		InitialStateMessage msg = new InitialStateMessage();
		msg.setPartcount(list.size());
		msg.setWorldState(state);
		networkController.sendMessage(new CompressedMessage(msg), playerid);

		int k = 1;
		for (MoveableInfo[] p : list) {
			FlubeStatePartMessage partmsg = new FlubeStatePartMessage();
			partmsg.setFlubes(p);
			partmsg.setNumber(k++);
			networkController.sendMessage(new CompressedMessage(partmsg),
					playerid);
		}
	}

	public void broadcastInitialState() {
		WorldState state = getWorldState();
		MoveableInfo[] infos = state.getFlubes();
		int partsize = 300;
		LinkedList<MoveableInfo[]> list = new LinkedList<MoveableInfo[]>();
		MoveableInfo[] part = new MoveableInfo[partsize];
		int j = 0;
		for (int i = 0; i < infos.length; i++) {
			part[j] = infos[i];
			j++;
			if (j >= partsize) {
				list.add(part);
				part = new MoveableInfo[partsize];
				j = 0;
			}
		}
		list.add(part);

		state.setFlubes(new MoveableInfo[0]);
		InitialStateMessage msg = new InitialStateMessage();
		msg.setPartcount(list.size());
		msg.setWorldState(state);
		networkController.broadcast(new CompressedMessage(msg));

		int k = 1;
		for (MoveableInfo[] p : list) {
			FlubeStatePartMessage partmsg = new FlubeStatePartMessage();
			partmsg.setFlubes(p);
			partmsg.setNumber(k++);
			networkController.broadcast(new CompressedMessage(partmsg));
		}
	}

	public void setViewDir(int playerid, Vector3f dir) {
		ServerPlayer p = getPlayerController().getPlayer(playerid);
		if (p == null || dir == null)
			return;
		p.setViewDir(dir);
	}

	public void connectionAdded(int clientid) {
		ConnectionInitMessage init = new ConnectionInitMessage();
		init.setConnectionAccepted(true);
		init.setText("Welcome");
		// try {
		// String xml = new
		// MapXMLParser(assetManager).writeMap(worldController.getMap());
		// init.setLevel(xml);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		init.setLevel(getGameConfig().getString("mapFileName"));
		networkController.sendMessage(init, clientid);
	}

	public void connectionRemoved(int clientid) {
		ServerPlayer p = getPlayerController().getPlayer(clientid);

		if (p != null) {
			PlayerQuitEvent quit = new PlayerQuitEvent(p.getId(), true);
			getEventMachine().fireEvent(quit);
		}
	}

	public void handleCommand(String command) {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
		Matcher regexMatcher = regex.matcher(command);
		while (regexMatcher.find()) {
		    matchList.add(stripParameterQuotes(regexMatcher.group()));
		} 
		String[] com = matchList.toArray(new String[matchList.size()]);

		if ("restartround".equalsIgnoreCase(com[0])) {
			gameplayController.endRound(-1, true);
		} else if("set".equalsIgnoreCase(com[0])) {
			if(com.length < 3) {
				CWRITER.writeLine("Syntax error! Usage: set <key> <value>");
			}else if(getGameConfig().getObject(com[1]) == null) {
				CWRITER.writeLine("no such key: " + com[1]);
			}else {
				changeConfig(com[1], com[2]);
			}
		} else if ("mapsdir".equalsIgnoreCase(com[0])) {
			if (com.length < 2) {
				CWRITER.writeLine("mapsdir is " + this.mapsDir);
			} else {
				this.mapsDir = com[1];
			}
		} else if ("shutdown".equalsIgnoreCase(com[0])) {
			gameplayController.endRound(-1, false);
			stop();
		}
	}
	
	private String stripParameterQuotes(String param) {
		if(param.startsWith("\"") && param.endsWith("\"")) {
			return param.substring(1, param.length()-1);
		}
		return param;
	}

	private void loadMap(final String mapname) {
		gameplayController.endRound(-1, false);
		enqueue(new Callable<String>() {
			@Override
			public String call() throws Exception {
				getWorldController().unloadCurrentWorld();

				for (ServerPlayer p : getPlayerController().getAllPlayers()) {
					getPlayerController().setDefaultEquipment(p);
				}

				try {
					String filename = GameServer.this.mapsDir
							+ System.getProperty("file.separator") + mapname
							+ MAPEXTENSION;
					File mapFile = new File(filename);
					if (!mapFile.exists()) {
						CWRITER.writeLine("ServerMap file not found: " + filename);
					} else {
						InputSource is = new InputSource(
								new FileReader(mapFile));

						MapXMLParser mapXMLParser = new MapXMLParser(
								assetManager);
						ServerMap map = mapXMLParser.loadMap(is);
						getWorldController().loadWorld(map);
						broadcastInitialState();
						changeConfig("map", mapname);
						gameplayController.restartRound();
						informStateListeners();
						CWRITER.writeLine("loaded map: " + mapname);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				}

				return null;
			}
		});
	}

	public void saveCurrentMap(String name, Writer w) {
		MapXMLParser parser = new MapXMLParser(assetManager);

		try {
			getWorldController().getMap().setName(name);
			String mapstring = parser.writeMap(getWorldController().getMap());
			w.write(mapstring);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void switchGameMode(String mode) {
		changeConfig("gamemode", mode);
		if ("editor".equalsIgnoreCase(mode)) {
			gameplayController.endRound(-1, true);
			for (ServerPlayer p : getPlayerController().getAllPlayers()) {
				getPlayerController().setDefaultEquipment(p);
				p.getControl().setGravity(0);
			}
			CWRITER.writeLine("switched gamemode to 'editor'");
		} else if ("ctf".equalsIgnoreCase(mode)) {
			gameplayController.endRound(-1, true);
			for (ServerPlayer p : getPlayerController().getAllPlayers()) {
				getPlayerController().setDefaultEquipment(p);
				p.getControl().setGravity(25);
			}
			CWRITER.writeLine("switched gamemode to 'ctf'");
		}
	}

	private void changeConfig(String key, Object value) {
		getGameConfig().putObject(key, value);

		ConfigEvent event = new ConfigEvent(key, value, true);
		getEventMachine().fireEvent(event);
	}

	public ServerEquipmentFactory getEquipmentFactory() {
		return this.equipmentFactory;
	}

	public void registerStateListener(ServerStateListener listener) {
		if (!stateListeners.contains(listener)) {
			stateListeners.add(listener);
		}
	}

	public void unregisterStateListener(ServerStateListener listener) {
		if (stateListeners.contains(listener)) {
			stateListeners.remove(listener);
		}
	}

	public void informStateListeners() {
		for (ServerStateListener sl : stateListeners) {
			sl.stateChanged();
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
	public ServerWorldController getWorldController() {
		return worldController;
	}

	/**
	 * @return the playerController
	 */
	public ServerPlayerController getPlayerController() {
		return playerController;
	}

	/**
	 * @return the eventMachine
	 */
	public EventMachine getEventMachine() {
		return eventMachine;
	}

	protected void takeFlag(ServerPlayer p, ServerFlag flag) {
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

	protected void killPlayer(ServerPlayer p) {
		if (p == null)
			return;
		if (p.getFlag() != null) {
			returnFlag(p.getFlag());
		}
		p.setGameOverTime(System.currentTimeMillis());
		worldController.detachPlayer(p);
		p.setAlive(false);
	}

	protected void phase(ServerPlayer attacker, ServerPlayer victim, float damage) {
		getPlayerController().setHealthpoints(victim,
				victim.getHealthpoints() - damage);
		if (victim.getHealthpoints() <= 0) {
			beam(attacker, victim);
		}
	}

	protected void push(ServerPlayer attacker, ServerPlayer victim, Vector3f force) {
		victim.getControl().applyCentralForce(force);
	}

	protected void pickup(ServerPlayer p, ServerFlube flube) {
		if (flube != null) {
			getWorldController().detachFlube(flube);
			if (p != null) {
				if (p.getCurrentEquipment() instanceof ServerPicker) {
					ServerPicker picker = (ServerPicker) p.getCurrentEquipment();
					picker.getRepository().add(flube);
				}
			}
		}
	}

	protected void place(ServerPlayer p, ServerFlube f, Vector3f loc) {
		f.getControl().setPhysicsLocation(loc);
		getWorldController().attachFlube(f);
		if (p != null) {
			if (p.getCurrentEquipment() instanceof ServerPicker) {
				ServerPicker picker = (ServerPicker) p.getCurrentEquipment();
				picker.getRepository().remove(f);
			}
		}
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

	/**
	 * This class is used to send the current state of the virtual world to all
	 * clients in constant intervals.
	 * 
	 * @author encala
	 * 
	 */
	private class LocationSenderLoop implements Runnable {
		@Override
		public void run() {
			while (!Thread.interrupted()) {
				LocationUpdatedMessage worldstate = LocationUpdatedMessage
						.getUpdate(getPlayerController().getAllPlayers());
				networkController.broadcast(worldstate);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	public interface ServerStateListener {

		public void stateChanged();
	}
}
