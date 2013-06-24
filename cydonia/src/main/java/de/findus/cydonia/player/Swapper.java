/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.SwapperInfo;

/**
 * @author Findus
 *
 */
public class Swapper extends AbstractEquipment {

	private static final String TYPENAME = "Phaser"; 
	
	private static BufferedImage hudImg;
	
	private String name;
	
	private float range;
	
	private Node geom = new Node("Swapper");
	
	protected ParticleEmitter beam;
	
	protected AudioNode fireSound;
	
	protected Object markerA;
	
	protected Object markerB;
	
	/**
	 * 
	 */
	public Swapper() {
		initHUDImgs();
	}

	/**
	 * @param mainController
	 */
	public Swapper(MainController mainController) {
		super(mainController);
		initHUDImgs();
	}
	
	/**
	 * @param mainController
	 */
	public Swapper(String name, Player player, MainController mainController) {
		super(mainController);
		this.name = name;
		this.player = player;
		
		initHUDImgs();
	}
	
	@Override
	public String getTypeName() {
		return TYPENAME;
	}
	
	@Override
	public void initGeometry() {
		beam = new ParticleEmitter("Beam", Type.Triangle, 30);
        Material mat_red = new Material(getMainController().getAssetManager(), 
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", getMainController().getAssetManager().loadTexture(
                "Effects/Explosion/flame.png"));
        beam.setMaterial(mat_red);
        beam.setImagesX(2); 
        beam.setImagesY(2); // 2x2 texture animation
        beam.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.5f));
        beam.setStartColor(new ColorRGBA(1f, 0.5f, 0.5f, 1f));
        beam.setStartSize(0.02f);
        beam.setEndSize(0.005f);
        beam.setGravity(0, 0, 0);
        beam.setNumParticles(300);
		beam.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0.05f));
		beam.getParticleInfluencer().setVelocityVariation(1f);
		beam.setParticlesPerSec(0);
		beam.setShape(new EmitterBoxShape(Vector3f.ZERO, new Vector3f(0.1f, 0.1f, 0.1f)));
		beam.setLowLife(0.5f);
		beam.setHighLife(2f);
		beam.setEnabled(true);
	    
	    this.geom.attachChild(beam);
	    geom.setLocalTranslation(0, 0.3f, 0.5f);
	    
	    initSound();
	}

	private void initSound() {
		fireSound = new AudioNode(getMainController().getAssetManager(), "de/findus/cydonia/sounds/pew01_mono.wav", false);
		fireSound.setLooping(false);
		fireSound.setPositional(true);
		fireSound.setLocalTranslation(Vector3f.ZERO);
		fireSound.setVolume(1);
		this.geom.attachChild(fireSound);
	}

	private void initHUDImgs() {
		try {
			if(hudImg == null) {
				hudImg = ImageIO.read(this.getClass().getResourceAsStream("/de/findus/cydonia/gui/hud/swapper.png"));
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void usePrimary(boolean activate) {
		
	}

	@Override
	public void useSecondary(boolean activate) {
		
	}

	@Override
	public void reset() {
		markerA = null;
		markerB = null;
	}

	@Override
	public BufferedImage getHUDImage() {
		BufferedImage tmpimg = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		gr.drawImage(hudImg, 0, 0, new Color(0, 0, 0, 0), null);

		return tmpimg;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public EquipmentInfo getInfo() {
		return new SwapperInfo(this);
	}

	@Override
	public void loadInfo(EquipmentInfo info) {
		if(info instanceof SwapperInfo) {
			SwapperInfo i = (SwapperInfo) info;
			this.name = i.getName();
		}
	}

	@Override
	public Node getGeometry() {
		return this.geom;
	}

	@Override
	public void setActive(boolean active) {
		
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

}
