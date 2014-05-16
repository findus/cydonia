/**
 * 
 */
package de.encala.cydonia.game.player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import de.encala.cydonia.game.GameController;
import de.encala.cydonia.game.equipment.ClientEditor;
import de.encala.cydonia.game.equipment.ClientEquipment;
import de.encala.cydonia.game.equipment.ClientPicker;
import de.encala.cydonia.game.equipment.ClientSwapper;
import de.encala.cydonia.share.messages.EquipmentInfo;
import de.encala.cydonia.share.messages.PlayerInfo;
import de.encala.cydonia.share.player.InputCommand;

/**
 * @author encala
 * 
 */
public class PlayerController {

	private GameController gameController;

	private ConcurrentHashMap<Integer, Player> players;

	private ParticleEmitter dieEmit;

	public PlayerController(AssetManager assetManager,
			GameController mainController) {
		this.gameController = mainController;

		players = new ConcurrentHashMap<Integer, Player>();

		dieEmit = new ParticleEmitter("dieEmit", Type.Point, 200);
		Material mat_red = new Material(assetManager,
				"Common/MatDefs/Misc/Particle.j3md");
		mat_red.setTexture("Texture",
				assetManager.loadTexture("Effects/Explosion/flame.png"));

		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Particle.j3md");
		mat.setBoolean("PointSprite", true);
		mat.setTexture("Texture",
				assetManager.loadTexture("Effects/Explosion/flash.png"));

		dieEmit.setMaterial(mat);
		dieEmit.setImagesX(2);
		dieEmit.setImagesY(2);
		dieEmit.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.0f));
		dieEmit.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
		dieEmit.setStartSize(0.03f);
		dieEmit.setEndSize(0.01f);
		dieEmit.setGravity(0, 0, 0);
		dieEmit.setNumParticles(100);
		dieEmit.setShape(new EmitterBoxShape(new Vector3f(-0.5f, -0.8f, -0.5f),
				new Vector3f(0.5f, 0.8f, 0.5f)));
		dieEmit.getParticleInfluencer().setInitialVelocity(
				new Vector3f(0f, 0.1f, 0f));
		dieEmit.getParticleInfluencer().setVelocityVariation(0.1f);
		dieEmit.setRandomAngle(true);
		dieEmit.setParticlesPerSec(0f);
		dieEmit.setLowLife(0.2f);
		dieEmit.setHighLife(0.5f);
		dieEmit.setEnabled(true);
		this.gameController.getWorldController().getRootNode()
				.attachChild(dieEmit);
	}

	public Player createNew(int id) {
		Player p = new Player(id);
		players.put(p.getId(), p);

		return p;
	}

	public Player getPlayer(int id) {
		return players.get(id);
	}

	public Collection<Player> getAllPlayers() {
		return players.values();
	}

	public void removePlayer(int id) {
		players.remove(id);
	}

	public void removeAllPlayers() {
		players.clear();
	}

	

//	public void setTransparency(Player p, float transparency) {
//		Node n = (Node) p.getModel();
//		if (n == null)
//			return;
//
//		transparency = Math.max(0f, Math.min(1f, transparency));
//
//		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
//		int cw = Math.round(255 * transparency);
//		Color c = new Color(cw, cw, cw);
//		img.setRGB(0, 0, c.getRGB());
//
//		AWTLoader loader = new AWTLoader();
//		Image imageJME = loader.load(img, true);
//		Texture t = new Texture2D(imageJME);
//
//		ColorRGBA glowcolor = new ColorRGBA(0, 0, 0, cw);
//
//		for (Spatial s : n.getChildren()) {
//			if (s instanceof Geometry) {
//				Material m = ((Geometry) s).getMaterial();
//				m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//				m.setTexture("AlphaMap", t);
//				m.setColor("GlowColor", glowcolor);
//			}
//		}
//	}

	public void reset(int playerid) {
		Player p = players.get(playerid);
		p.setScores(0);

		resetEquips(playerid);
	}

	public void resetEquips(int playerid) {
		Player p = players.get(playerid);
		for (ClientEquipment equip : p.getEquips()) {
			equip.reset();
		}
	}

	public void setDefaultEquipment(int playerid) {
		Player p = players.get(playerid);
		p.setCurrEquip(0);
		p.getEquips().clear();

		String gameMode = gameController.getGameConfig().getString(
				"gamemode");
		if ("ctf".equals(gameMode)) {
			ClientPicker picker1 = (ClientPicker) gameController.getEquipmentFactory()
					.create("Picker");
			picker1.setName("LongRangePicker");
			picker1.setRange(15);
			picker1.setCapacity(1);
			picker1.setPlayer(p);
			p.getEquips().add(picker1);

			ClientPicker picker2 = (ClientPicker) gameController.getEquipmentFactory()
					.create("Picker");
			picker2.setName("ShortRangePicker");
			picker2.setRange(5);
			picker2.setCapacity(3);
			picker2.setPlayer(p);
			p.getEquips().add(picker2);

			ClientSwapper swapper = (ClientSwapper) gameController.getEquipmentFactory()
					.create("Swapper");
			swapper.setName("Swapper");
			swapper.setRange(100);
			swapper.setPlayer(p);
			p.getEquips().add(swapper);
		} else if ("editor".equals(gameMode)) {
			ClientEditor editor1 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor1.setName("EditorDarkGray");
			editor1.setRange(50);
			editor1.setObjectType("flube");
			editor1.setObjectSpec(-2);
			editor1.setPlayer(p);
			p.getEquips().add(editor1);

			ClientEditor editor2 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor2.setName("EditorLightGray");
			editor2.setRange(50);
			editor2.setObjectType("flube");
			editor2.setObjectSpec(-1);
			editor2.setPlayer(p);
			p.getEquips().add(editor2);

			ClientEditor editor3 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor3.setName("EditorWhite");
			editor3.setRange(50);
			editor3.setObjectType("flube");
			editor3.setObjectSpec(0);
			editor3.setPlayer(p);
			p.getEquips().add(editor3);

			ClientEditor editor4 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor4.setName("EditorBlue");
			editor4.setRange(50);
			editor4.setObjectType("flube");
			editor4.setObjectSpec(1);
			editor4.setPlayer(p);
			p.getEquips().add(editor4);

			ClientEditor editor5 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor5.setName("EditorRed");
			editor5.setRange(50);
			editor5.setObjectType("flube");
			editor5.setObjectSpec(2);
			editor5.setPlayer(p);
			p.getEquips().add(editor5);

			ClientEditor editor6 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor6.setName("EditorFlagBlue");
			editor6.setRange(50);
			editor6.setObjectType("flag");
			editor6.setObjectSpec(1);
			editor6.setPlayer(p);
			p.getEquips().add(editor6);

			ClientEditor editor7 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor7.setName("EditorFlagRed");
			editor7.setRange(50);
			editor7.setObjectType("flag");
			editor7.setObjectSpec(2);
			editor7.setPlayer(p);
			p.getEquips().add(editor7);

			ClientEditor editor8 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor8.setName("EditorSpawnBlue");
			editor8.setRange(50);
			editor8.setObjectType("spawnpoint");
			editor8.setObjectSpec(1);
			editor8.setPlayer(p);
			p.getEquips().add(editor8);

			ClientEditor editor9 = (ClientEditor) gameController.getEquipmentFactory()
					.create("Editor");
			editor9.setName("EditorSpawnRed");
			editor9.setRange(50);
			editor9.setObjectType("spawnpoint");
			editor9.setObjectSpec(2);
			editor9.setPlayer(p);
			p.getEquips().add(editor9);
		}
	}

	public void handleInput(int playerid, InputCommand command, boolean value) {
		Player p = players.get(playerid);
		switch (command) {
		case MOVEFRONT:
			p.setForward(value);
			break;
		case MOVEBACK:
			p.setBack(value);
			break;
		case STRAFELEFT:
			p.setLeft(value);
			break;
		case STRAFERIGHT:
			p.setRight(value);
			break;
		case JUMP:
			if("ctf".equalsIgnoreCase(gameController.getGameConfig()
					.getString("gamemode"))) {
				p.setJump(value);
			}
			break;
		case USEPRIMARY:
			p.getCurrentEquipment().usePrimary(value);
			break;
		case USESECONDARY:
			p.getCurrentEquipment().useSecondary(value);
			break;
		case SWITCHEQUIP:
			p.setCurrEquip(p.getCurrEquip() + (value? 1 : -1));
			break;
		default:
			break;
		}
	}
	
	public void respawn(int playerid) {
		Player p = players.get(playerid);
		resetEquips(playerid);
		p.setAlive(true);
	}

	public void kill(int playerid) {
		Player p = players.get(playerid);
		p.setAlive(false);
		p.setGameOverTime(System.currentTimeMillis());
	}
	
	public void loadInfo(PlayerInfo info) {
		Player p = players.get(info.getPlayerid());
		
		p.setName(info.getName());
		p.setTeam(info.getTeam());
		p.setAlive(info.isAlive());
		p.setScores(info.getScores());

		LinkedList<ClientEquipment> equips = new LinkedList<ClientEquipment>();
		for (EquipmentInfo ei : info.getEquipInfos()) {
			ClientEquipment equip = gameController.getEquipmentFactory()
					.create(ei.getTypeName());
			if (equip != null) {
				equip.setPlayer(p);
				equip.loadInfo(ei);
				equips.add(equip);
			}
		}
		p.setEquips(equips);
		p.setCurrEquip(info.getCurrEquip());
	}

//	public void playDieAnim(Player p) {
//		dieEmit.setLocalTranslation(p..getControl().getPhysicsLocation());
//		dieEmit.emitAllParticles();
//	}
}
