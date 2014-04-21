/**
 * 
 */
package de.encala.cydonia.server.player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import de.encala.cydonia.server.GameServer;
import de.encala.cydonia.server.equipment.ServerEditor;
import de.encala.cydonia.server.equipment.ServerEquipment;
import de.encala.cydonia.server.equipment.ServerPicker;
import de.encala.cydonia.server.equipment.ServerSwapper;
import de.encala.cydonia.share.player.InputCommand;

/**
 * @author encala
 * 
 */
public class ServerPlayerController {

	private AssetManager assetManager;

	private GameServer gameServer;

	private ConcurrentHashMap<Integer, ServerPlayer> players;

	public ServerPlayerController(AssetManager assetManager,
			GameServer gameServer) {
		this.assetManager = assetManager;
		this.gameServer = gameServer;

		players = new ConcurrentHashMap<Integer, ServerPlayer>();
	}

	public ServerPlayer createNew(int id) {
		ServerPlayer p = new ServerPlayer(id);
		if ("ctf".equalsIgnoreCase(gameServer.getGameConfig().getString(
				"mp_gamemode"))) {
			p.getControl().setGravity(25);
		} else if ("editor".equalsIgnoreCase(gameServer.getGameConfig()
				.getString("mp_gamemode"))) {
			p.getControl().setGravity(0);
		}

		players.put(p.getId(), p);

		return p;
	}

	public ServerPlayer getPlayer(int id) {
		return players.get(id);
	}

	public Collection<ServerPlayer> getAllPlayers() {
		return players.values();
	}

	public void removePlayer(int id) {
		players.remove(id);
	}

	public void removeAllPlayers() {
		players.clear();
	}

	public void updateModel(ServerPlayer p) {
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

	public void setTransparency(ServerPlayer p, float transparency) {
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

	public void setHealthpoints(ServerPlayer p, double health) {
		if (p == null)
			return;
		p.setHealthpoints(health);
		setTransparency(p, (float) p.getHealthpoints() * 0.008f + 0.2f);
	}

	public void reset(ServerPlayer p) {
		p.setScores(0);

		resetEquips(p);
	}

	public void resetEquips(ServerPlayer p) {
		for (ServerEquipment equip : p.getEquips()) {
			equip.reset();
		}
	}

	public void setDefaultEquipment(ServerPlayer p) {
		ServerEquipment cur = p.getCurrentEquipment();
		if (cur != null && cur.getGeometry() != null) {
			p.getNode().detachChild(cur.getGeometry());
		}
		p.setCurrEquip(0);
		p.getEquips().clear();

		String gameMode = gameServer.getGameConfig().getString(
				"mp_gamemode");
		if ("ctf".equals(gameMode)) {
			ServerPicker picker1 = (ServerPicker) gameServer.getEquipmentFactory()
					.create("Picker");
			picker1.setName("LongRangePicker");
			picker1.setRange(15);
			picker1.setCapacity(1);
			picker1.setServerPlayer(p);
			p.getEquips().add(picker1);

			ServerPicker picker2 = (ServerPicker) gameServer.getEquipmentFactory()
					.create("Picker");
			picker2.setName("ShortRangePicker");
			picker2.setRange(5);
			picker2.setCapacity(3);
			picker2.setServerPlayer(p);
			p.getEquips().add(picker2);

			ServerSwapper swapper = (ServerSwapper) gameServer.getEquipmentFactory()
					.create("Swapper");
			swapper.setName("Swapper");
			swapper.setRange(100);
			swapper.setServerPlayer(p);
			p.getEquips().add(swapper);
		} else if ("editor".equals(gameMode)) {
			ServerEditor editor1 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor1.setName("EditorDarkGray");
			editor1.setRange(50);
			editor1.setObjectType("flube");
			editor1.setObjectSpec(-2);
			editor1.setServerPlayer(p);
			p.getEquips().add(editor1);

			ServerEditor editor2 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor2.setName("EditorLightGray");
			editor2.setRange(50);
			editor2.setObjectType("flube");
			editor2.setObjectSpec(-1);
			editor2.setServerPlayer(p);
			p.getEquips().add(editor2);

			ServerEditor editor3 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor3.setName("EditorWhite");
			editor3.setRange(50);
			editor3.setObjectType("flube");
			editor3.setObjectSpec(0);
			editor3.setServerPlayer(p);
			p.getEquips().add(editor3);

			ServerEditor editor4 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor4.setName("EditorBlue");
			editor4.setRange(50);
			editor4.setObjectType("flube");
			editor4.setObjectSpec(1);
			editor4.setServerPlayer(p);
			p.getEquips().add(editor4);

			ServerEditor editor5 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor5.setName("EditorRed");
			editor5.setRange(50);
			editor5.setObjectType("flube");
			editor5.setObjectSpec(2);
			editor5.setServerPlayer(p);
			p.getEquips().add(editor5);

			ServerEditor editor6 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor6.setName("EditorFlagBlue");
			editor6.setRange(50);
			editor6.setObjectType("flag");
			editor6.setObjectSpec(1);
			editor6.setServerPlayer(p);
			p.getEquips().add(editor6);

			ServerEditor editor7 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor7.setName("EditorFlagRed");
			editor7.setRange(50);
			editor7.setObjectType("flag");
			editor7.setObjectSpec(2);
			editor7.setServerPlayer(p);
			p.getEquips().add(editor7);

			ServerEditor editor8 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor8.setName("EditorSpawnBlue");
			editor8.setRange(50);
			editor8.setObjectType("spawnpoint");
			editor8.setObjectSpec(1);
			editor8.setServerPlayer(p);
			p.getEquips().add(editor8);

			ServerEditor editor9 = (ServerEditor) gameServer.getEquipmentFactory()
					.create("Editor");
			editor9.setName("EditorSpawnRed");
			editor9.setRange(50);
			editor9.setObjectType("spawnpoint");
			editor9.setObjectSpec(2);
			editor9.setServerPlayer(p);
			p.getEquips().add(editor9);
		}
	}

	public void setTeam(ServerPlayer p, int team) {
		if (p == null)
			return;

		p.setTeam(team);
		updateModel(p);
	}

	public int getPlayerCount() {
		return players.size();
	}

	public void handleInput(ServerPlayer p, InputCommand command, boolean value) {
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
				if ("ctf".equalsIgnoreCase(gameServer.getGameConfig()
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
}
