/**
 * 
 */
package de.findus.cydonia.main;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.EventListener;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.level.Flag;
import de.findus.cydonia.level.FlagFactory;
import de.findus.cydonia.level.Flube;
import de.findus.cydonia.level.WorldController;
import de.findus.cydonia.player.Beamer;
import de.findus.cydonia.player.Picker;
import de.findus.cydonia.player.Player;
import de.findus.cydonia.player.PlayerController;

/**
 * @author Findus
 *
 */
public abstract class MainController extends Application implements PhysicsCollisionListener, EventListener {
		
	    public static float PLAYER_SPEED = 5f;
	    public static float PHYSICS_ACCURACY = (1f / 192);
	    
	    public static Transform ROTATE90LEFT = new Transform(new Quaternion().fromRotationMatrix(new Matrix3f(1, 0, FastMath.HALF_PI, 0, 1, 0, -FastMath.HALF_PI, 0, 1)));
	    
	    private GameState gamestate;
	    
	    private GameConfig gameConfig;
	    
	    private WorldController worldController;
	    
	    private PlayerController playerController;
	    
	    private BulletAppState bulletAppState;
	    
	    private EventMachine eventMachine;
	    
	    private ConcurrentLinkedQueue<Event> eventQueue;
	    

	    @Override
	    public void initialize() {
	        super.initialize();
	        
	        this.gamestate = GameState.LOADING;
	        
	        gameConfig = new GameConfig(true);
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
	        
	        worldController = new WorldController(assetManager, bulletAppState.getPhysicsSpace());
	        eventMachine.registerListener(this);
			
			playerController = new PlayerController(assetManager, this);
	    }
	    
	    protected void cleanup() {
	    	bulletAppState.setEnabled(false);
	    	eventMachine.stop();
	    }
	    
	    @Override
	    public void update() {
	        super.update(); // makes sure to execute AppTasks
	        handleEvents();
	    }

	    protected abstract void handleEvent(Event e);

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
		
		protected void returnFlag(Flag flag) {
			getWorldController().returnFlag(flag);
		}
		
		protected void takeFlag(Player p, Flag flag) {
			flag.setInBase(false);
			Node parent = flag.getModel().getParent();
			if(parent != null) {
				parent.detachChild(flag.getModel());
			}
			flag.getModel().setLocalTranslation(0, 1, 0);
//			flag.getModel().setLocalScale(Vector3f.UNIT_XYZ.divide(p.getModel().getLocalScale()));
			p.getNode().attachChild(flag.getModel());
			p.setFlag(flag);
			flag.setPlayer(p);
			System.out.println("takenflag");
		}
		
		protected void scoreFlag(Player p, Flag flag) {
			p.setFlag(null);
			flag.setPlayer(null);
			p.setScores(p.getScores() + 3);
			returnFlag(flag);
			// TODO: score team
			System.out.println("scoredflag");
		}
		
		protected void killPlayer(Player p) {
			if(p == null) return;
			if(p.getFlag() != null) {
				returnFlag(p.getFlag());
			}
			worldController.detachPlayer(p);
			p.setAlive(false);
		}
		
		protected void joinPlayer(int playerid, String playername) {
			Player p = playerController.createNew(playerid);
			p.getEquips().add(new Picker("defaultPicker1", 15, 1, p, this));
			p.getEquips().add(new Picker("defaultPicker3", 5, 3, p, this));
			p.getEquips().add(new Beamer("beamer", 20, p, this));
			p.setName(playername);
		}
		
		protected void quitPlayer(Player p) {
			if(p == null) return;
			if(p.getFlag() != null) {
				returnFlag(p.getFlag());
			}
			worldController.detachPlayer(p);
			playerController.removePlayer(p.getId());
		}
		
		protected void respawn(final Player p) {
			if(p == null) return;
			playerController.setHealthpoints(p, 100);
			p.setAlive(true);

			p.getControl().setPhysicsLocation(worldController.getSpawnPoint(p.getTeam()).getPosition());
			worldController.attachPlayer(p);
		}
		
		protected void chooseTeam(Player p, int team) {
			if(p == null) return;
			playerController.setTeam(p, team);
		}
		
		protected void pickup(Player p, Flube flube) {
			if(flube != null) {
				getWorldController().detachFlube(flube);
				if(p != null) {
					if(p.getCurrentEquipment() instanceof Picker) {
						Picker picker = (Picker) p.getCurrentEquipment();
						picker.getRepository().add(flube);
					}
				}
			}
		}
		
		protected void place(Player p, Flube f, Vector3f loc) {
			f.getControl().setPhysicsLocation(loc);
			getWorldController().attachFlube(f);
			if(p != null) {
				if(p.getCurrentEquipment() instanceof Picker) {
					Picker picker = (Picker) p.getCurrentEquipment();
					picker.getRepository().remove(f);
				}
			}
			System.out.println("Place");
		}
		
		public GameState getGamestate() {
			return gamestate;
		}

	    /**
		 * @param gamestate the gamestate to set
		 */
		public void setGamestate(GameState gamestate) {
			this.gamestate = gamestate;
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
}
