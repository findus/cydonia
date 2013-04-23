/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.collision.CollisionResult;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.findus.cydonia.events.PhaseEvent;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PhaserInfo;

/**
 * @author Findus
 *
 */
public class Phaser extends AbstractEquipment {

	private static final String TYPENAME = "Phaser"; 
	
	private static BufferedImage hudImg;
	
	private String name;
	
	private float damage;
	
	private float interval;
	
	private float lastShotTime = 0;
	
	private Node geom = new Node("Phaser");
	
	protected ParticleEmitter beam;
	
	/**
	 * 
	 */
	public Phaser() {
		initHUDImgs();
	}

	/**
	 * @param mainController
	 */
	public Phaser(String name, float damage, float interval, Player player, MainController mainController) {
		super(mainController);
		this.name = name;
		this.damage = damage;
		this.interval = interval;
		this.player = player;
		
		initHUDImgs();
		
		initGeometry();
	}

	/* (non-Javadoc)
	 * @see de.findus.cydonia.player.Equipment#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return TYPENAME;
	}

	@Override
	public void initGeometry() {
		beam = new ParticleEmitter("Beam", Type.Point, 30);
        Material mat_red = new Material(getMainController().getAssetManager(), 
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", getMainController().getAssetManager().loadTexture(
                "Effects/Explosion/flame.png"));
        beam.setMaterial(mat_red);
        beam.setImagesX(2); 
        beam.setImagesY(2); // 2x2 texture animation
        beam.setEndColor(new ColorRGBA(6f, 6f, 1f, 0.5f));
        beam.setStartColor(new ColorRGBA(3f, 3f, 1f, 1f));
        beam.setStartSize(0.01f);
        beam.setEndSize(0.005f);
        beam.setGravity(0, 0, 0);
        beam.setNumParticles(200);
	    geom.setLocalTranslation(0, 0.3f, 0.2f);
		beam.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0.1f));
		beam.getParticleInfluencer().setVelocityVariation(1f);
		beam.setParticlesPerSec(0);
		beam.setShape(new EmitterBoxShape(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0)));
		beam.setLowLife(0.5f);
		beam.setHighLife(1f);
		beam.setEnabled(true);
	    
	    this.geom.attachChild(beam);
	}

	private void initHUDImgs() {
		try {
			if(hudImg == null) {
				hudImg = ImageIO.read(this.getClass().getResourceAsStream("/de/findus/cydonia/gui/hud/phaser.png"));
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void usePrimary(boolean activate) {
		if(!activate) return;

		if(this.getLastShotTime() + this.getInterval() < System.currentTimeMillis()) {
			CollisionResult result = getMainController().getWorldController().pickWorld(this.player.getEyePosition(), this.player.getViewDir());
			float distance = 0;
			if(result != null) {
				distance = result.getDistance();
			}else {
				distance = 30;
			}
			beam.setLocalTranslation(distance/2, 0, 0);
			beam.emitAllParticles();
		}
	}

	@Override
	public void useSecondary(boolean activate) {

	}

	@Override
	public void reset() {
		lastShotTime = 0;
	}

	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		gr.drawImage(hudImg, 0, 0, new Color(0, 0, 0, 0), null);

		return tmpimg;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new PhaserInfo(this);
	}

	@Override
	public void loadInfo(EquipmentInfo info) {
		if(info instanceof PhaserInfo) {
			PhaserInfo i = (PhaserInfo) info;
			this.name = i.getName();
			this.damage = i.getDamage();
			this.interval = i.getInterval();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getInterval() {
		return interval;
	}
	
	public void setInterval(float interval) {
		this.interval = interval;
	}

	protected float getLastShotTime() {
		return lastShotTime;
	}

	@Override
	public Node getGeometry() {
		return geom;
	}

	@Override
	public void setActive(boolean active) {

	}
}
