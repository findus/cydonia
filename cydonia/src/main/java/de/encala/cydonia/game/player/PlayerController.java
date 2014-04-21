/**
 * 
 */
package de.encala.cydonia.game.player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import de.encala.cydonia.share.MainController;

/**
 * @author encala
 * 
 */
public class PlayerController {

	private AssetManager assetManager;

	private MainController mainController;

	private ConcurrentHashMap<Integer, Player> players;

	private ParticleEmitter dieEmit;

	public PlayerController(AssetManager assetManager,
			MainController mainController) {
		this.assetManager = assetManager;
		this.mainController = mainController;

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
		this.mainController.getWorldController().getRootNode()
				.attachChild(dieEmit);
	}

	public Player createNew(int id) {
		Player p = new Player(id);
		if ("ctf".equalsIgnoreCase(mainController.getGameConfig().getString(
				"mp_gamemode"))) {
			p.getControl().setGravity(25);
		} else if ("editor".equalsIgnoreCase(mainController.getGameConfig()
				.getString("mp_gamemode"))) {
			p.getControl().setGravity(0);
		}

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

	public void updateModel(Player p) {
		if (p == null)
			return;

		Node model;
		if (p.getTeam() == 1) {
			model = (Node) assetManager
					.loadModel("de/encala/cydonia/models/blue/Sinbad.mesh.xml");
		} else if (p.getTeam() == 2) {
			model = (Node) assetManager
					.loadModel("de/encala/cydonia/models/red/Sinbad.mesh.xml");
		} else {
			model = (Node) assetManager
					.loadModel("de/encala/cydonia/models/green/Sinbad.mesh.xml");
		}
		model.setName("player" + p.getId());
		model.setLocalScale(0.2f);
		model.setShadowMode(ShadowMode.Cast);
		model.setQueueBucket(Bucket.Transparent);

		p.setModel(model);
	}

	public void setTransparency(Player p, float transparency) {
		Node n = (Node) p.getModel();
		if (n == null)
			return;

		transparency = Math.max(0f, Math.min(1f, transparency));

		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		int cw = Math.round(255 * transparency);
		Color c = new Color(cw, cw, cw);
		img.setRGB(0, 0, c.getRGB());

		AWTLoader loader = new AWTLoader();
		Image imageJME = loader.load(img, true);
		Texture t = new Texture2D(imageJME);

		ColorRGBA glowcolor = new ColorRGBA(0, 0, 0, cw);

		for (Spatial s : n.getChildren()) {
			if (s instanceof Geometry) {
				Material m = ((Geometry) s).getMaterial();
				m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				m.setTexture("AlphaMap", t);
				m.setColor("GlowColor", glowcolor);
			}
		}
	}

	public void setHealthpoints(Player p, double health) {
		if (p == null)
			return;
		p.setHealthpoints(health);
		setTransparency(p, (float) p.getHealthpoints() * 0.008f + 0.2f);
	}

	public void reset(Player p) {
		p.setScores(0);

		resetEquips(p);
	}

	public void resetEquips(Player p) {
		for (Equipment equip : p.getEquips()) {
			equip.reset();
		}
	}

	public void setDefaultEquipment(Player p) {
		Equipment cur = p.getCurrentEquipment();
		if (cur != null && cur.getGeometry() != null) {
			p.getNode().detachChild(cur.getGeometry());
		}
		p.setCurrEquip(0);
		p.getEquips().clear();

		String gameMode = mainController.getGameConfig().getString(
				"mp_gamemode");
		if ("ctf".equals(gameMode)) {
			Picker picker1 = (Picker) mainController.getEquipmentFactory()
					.create("Picker");
			picker1.setName("LongRangePicker");
			picker1.setRange(15);
			picker1.setCapacity(1);
			picker1.setPlayer(p);
			p.getEquips().add(picker1);

			Picker picker2 = (Picker) mainController.getEquipmentFactory()
					.create("Picker");
			picker2.setName("ShortRangePicker");
			picker2.setRange(5);
			picker2.setCapacity(3);
			picker2.setPlayer(p);
			p.getEquips().add(picker2);

			// Beamer beamer = (Beamer)
			// mainController.getEquipmentFactory().create("Beamer");
			// beamer.setName("Beamer");
			// beamer.setRange(20);
			// beamer.setPlayer(p);
			// p.getEquips().add(beamer);
			//
			// Phaser phaser = (Phaser)
			// mainController.getEquipmentFactory().create("Phaser");
			// phaser.setName("Phaser");
			// phaser.setDamage(100);
			// phaser.setInterval(1000);
			// phaser.setPlayer(p);
			// p.getEquips().add(phaser);
			//
			// Pusher pusher = (Pusher)
			// mainController.getEquipmentFactory().create("Pusher");
			// pusher.setName("Phaser");
			// pusher.setForce(15);
			// pusher.setInterval(1000);
			// pusher.setPlayer(p);
			// p.getEquips().add(pusher);

			Swapper swapper = (Swapper) mainController.getEquipmentFactory()
					.create("Swapper");
			swapper.setName("Swapper");
			swapper.setRange(100);
			swapper.setPlayer(p);
			p.getEquips().add(swapper);
		} else if ("editor".equals(gameMode)) {
			Editor editor1 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor1.setName("EditorDarkGray");
			editor1.setRange(50);
			editor1.setObjectType("flube");
			editor1.setObjectSpec(-2);
			editor1.setPlayer(p);
			p.getEquips().add(editor1);

			Editor editor2 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor2.setName("EditorLightGray");
			editor2.setRange(50);
			editor2.setObjectType("flube");
			editor2.setObjectSpec(-1);
			editor2.setPlayer(p);
			p.getEquips().add(editor2);

			Editor editor3 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor3.setName("EditorWhite");
			editor3.setRange(50);
			editor3.setObjectType("flube");
			editor3.setObjectSpec(0);
			editor3.setPlayer(p);
			p.getEquips().add(editor3);

			Editor editor4 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor4.setName("EditorBlue");
			editor4.setRange(50);
			editor4.setObjectType("flube");
			editor4.setObjectSpec(1);
			editor4.setPlayer(p);
			p.getEquips().add(editor4);

			Editor editor5 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor5.setName("EditorRed");
			editor5.setRange(50);
			editor5.setObjectType("flube");
			editor5.setObjectSpec(2);
			editor5.setPlayer(p);
			p.getEquips().add(editor5);

			Editor editor6 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor6.setName("EditorFlagBlue");
			editor6.setRange(50);
			editor6.setObjectType("flag");
			editor6.setObjectSpec(1);
			editor6.setPlayer(p);
			p.getEquips().add(editor6);

			Editor editor7 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor7.setName("EditorFlagRed");
			editor7.setRange(50);
			editor7.setObjectType("flag");
			editor7.setObjectSpec(2);
			editor7.setPlayer(p);
			p.getEquips().add(editor7);

			Editor editor8 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor8.setName("EditorSpawnBlue");
			editor8.setRange(50);
			editor8.setObjectType("spawnpoint");
			editor8.setObjectSpec(1);
			editor8.setPlayer(p);
			p.getEquips().add(editor8);

			Editor editor9 = (Editor) mainController.getEquipmentFactory()
					.create("Editor");
			editor9.setName("EditorSpawnRed");
			editor9.setRange(50);
			editor9.setObjectType("spawnpoint");
			editor9.setObjectSpec(2);
			editor9.setPlayer(p);
			p.getEquips().add(editor9);
		}
	}

	public void setTeam(Player p, int team) {
		if (p == null)
			return;

		p.setTeam(team);
		updateModel(p);
	}

	public int getPlayerCount() {
		return players.size();
	}

	public void handleInput(Player p, InputCommand command, boolean value) {
		switch (command) {
		case MOVEFRONT:
			p.inputs.setForward(value);
			break;
		case MOVEBACK:
			p.inputs.setBack(value);
			break;
		case STRAFELEFT:
			p.inputs.setLeft(value);
			break;
		case STRAFERIGHT:
			p.inputs.setRight(value);
			break;
		case JUMP:
			if (value) {
				if ("ctf".equalsIgnoreCase(mainController.getGameConfig()
						.getString("mp_gamemode"))) {
					p.jump();
				}
			}
			break;
		case USEPRIMARY:
			p.getCurrentEquipment().usePrimary(value);
			break;
		case USESECONDARY:
			p.getCurrentEquipment().useSecondary(value);
			break;
		case SWITCHEQUIP:
			p.switchEquipment(value);
			break;
		default:
			break;
		}

		p.updateAnimationState();
	}

	public void playDieAnim(Player p) {
		dieEmit.setLocalTranslation(p.getControl().getPhysicsLocation());
		dieEmit.emitAllParticles();
	}
}
