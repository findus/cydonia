/**
 * 
 */
package de.encala.cydonia.level;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

import de.encala.cydonia.game.GameController;

/**
 * @author encala
 * 
 */
public class FlagFactory {

	private static FlagFactory instance;

	public static void init(AssetManager assetManager) {
		instance = new FlagFactory(assetManager);
	}

	public static FlagFactory getInstance() {
		return instance;
	}

	private AssetManager assetManager;

	/**
	 * 
	 */
	private FlagFactory(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public Flag createFlag(int id, Vector3f origin, int team) {
		Flag f = new Flag();
		f.setId(id);
		f.setOrigin(origin);
		f.setTeam(team);
		f.setInBase(true);

		ColorRGBA color = null;

		if (team == 1) {
			color = ColorRGBA.Blue;
		} else if (team == 2) {
			color = ColorRGBA.Red;
		}

		Node flagNode = new Node("Flag_" + id);
		flagNode.setUserData("id", id);
		flagNode.setLocalTranslation(0, 0.5f, 0);

		Texture tex_box = assetManager.loadTexture(GameController.TEXTURES_PATH
				+ "Box_white.png");
		Material mat_lit = new Material(assetManager,
				"Common/MatDefs/Light/Lighting.j3md");
		mat_lit.setTexture("DiffuseMap", tex_box);
		mat_lit.setBoolean("UseMaterialColors", true);
		mat_lit.setColor("Specular", ColorRGBA.White);
		mat_lit.setColor("Diffuse", color);
		mat_lit.setColor("Ambient", color);
		mat_lit.setFloat("Shininess", 2f);

		Mesh mesh = new Box(0.1f, 0.1f, 0.1f);
		Geometry model = new Geometry("FlagModel", mesh);
		model.setUserData("id", id);
		model.setMaterial(mat_lit);
		model.setShadowMode(ShadowMode.CastAndReceive);
		TangentBinormalGenerator.generate(model);

		AnimationFactory af = new AnimationFactory(4, "Rotate", 1);
		af.addKeyFrameRotationAngles(0, 1f, 0f, 1f);
		af.addKeyFrameRotationAngles(1, 1, FastMath.HALF_PI, 1);
		af.addKeyFrameRotationAngles(2, 1, FastMath.PI, 1);
		af.addKeyFrameRotationAngles(3, 1, FastMath.PI + FastMath.HALF_PI, 1);
		af.addKeyFrameRotationAngles(4, 1, FastMath.TWO_PI, 1);
		AnimControl ac = new AnimControl();
		ac.addAnim(af.buildAnimation());
		model.addControl(ac);
		AnimChannel chan = ac.createChannel();
		chan.setLoopMode(LoopMode.Loop);
		chan.setAnim("Rotate");

		flagNode.attachChild(model);
		f.setModel(flagNode);

		Node nodeBase = new Node("Flag_" + id);
		nodeBase.setUserData("id", id);
		nodeBase.setUserData("FlagBase", true);
		nodeBase.setUserData("team", team);

		ParticleEmitter glitterBase = new ParticleEmitter("Glitter",
				Type.Triangle, 30);
		Material mat_red = new Material(assetManager,
				"Common/MatDefs/Misc/Particle.j3md");
		mat_red.setTexture("Texture",
				assetManager.loadTexture("Effects/Explosion/flame.png"));
		glitterBase.setMaterial(mat_red);
		glitterBase.setImagesX(2);
		glitterBase.setImagesY(2); // 2x2 texture animation
		glitterBase.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.5f));
		glitterBase.setStartColor(color);
		glitterBase.setStartSize(0.03f);
		glitterBase.setEndSize(0.001f);
		glitterBase.setGravity(0, 0, 0);
		glitterBase.setNumParticles(400);
		glitterBase.setShape(new EmitterBoxShape(
				new Vector3f(-0.5f, -1f, -0.5f), new Vector3f(0.5f, 1f, 0.5f)));
		glitterBase.getParticleInfluencer().setInitialVelocity(
				new Vector3f(0.01f, 0.01f, 0.01f));
		glitterBase.getParticleInfluencer().setVelocityVariation(1f);
		glitterBase.setRandomAngle(true);
		glitterBase.setParticlesPerSec(100f);
		glitterBase.setLowLife(1f);
		glitterBase.setHighLife(3f);
		glitterBase.setEnabled(true);
		nodeBase.attachChild(glitterBase);

		SpotLight sptop = new SpotLight();
		sptop.setColor(ColorRGBA.White.mult(0.7f));
		sptop.setDirection(new Vector3f(0, -1, 0));
		sptop.setName("Flaglight");
		sptop.setPosition(origin.add(0, 0.9f, 0));
		sptop.setSpotInnerAngle(0);
		sptop.setSpotOuterAngle(FastMath.QUARTER_PI);
		sptop.setSpotRange(2f);
		nodeBase.addLight(sptop);

		SpotLight spbot = new SpotLight();
		spbot.setColor(ColorRGBA.White.mult(0.7f));
		spbot.setDirection(new Vector3f(0, 1, 0));
		spbot.setName("Flaglight");
		spbot.setPosition(origin.add(0, -0.9f, 0));
		spbot.setSpotInnerAngle(0);
		spbot.setSpotOuterAngle(FastMath.QUARTER_PI);
		spbot.setSpotRange(2f);
		nodeBase.addLight(spbot);

		Mesh m = new Quad(1f, 1f);
		Geometry floor = new Geometry("FlagFloor", m);
		floor.setUserData("id", id);
		floor.setMaterial(mat_lit);
		floor.setShadowMode(ShadowMode.Receive);
		TangentBinormalGenerator.generate(floor);
		floor.setLocalRotation(new Quaternion(0, 1f, 1f, 0));
		floor.setLocalTranslation(0.5f, -0.99f, -0.5f);
		nodeBase.attachChild(floor);

		CollisionShape collisionShape = new BoxCollisionShape(new Vector3f(
				0.5f, 1f, 0.5f));
		GhostControl baseControl = new GhostControl(collisionShape);
		baseControl.setCollisionGroup(GhostControl.COLLISION_GROUP_02);
		baseControl.setCollideWithGroups(GhostControl.COLLISION_GROUP_02);
		nodeBase.addControl(baseControl);

		nodeBase.setLocalTranslation(origin);

		f.setBaseControl(baseControl);
		f.setBaseModel(nodeBase);

		return f;
	}

}
