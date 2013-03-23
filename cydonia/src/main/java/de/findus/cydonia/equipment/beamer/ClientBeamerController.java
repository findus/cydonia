/**
 * 
 */
package de.findus.cydonia.equipment.beamer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidParameterException;

import javax.imageio.ImageIO;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial.CullHint;

import de.findus.cydonia.equipment.ClientEquipmentController;
import de.findus.cydonia.equipment.EquipmentModel;
import de.findus.cydonia.main.MainController;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.EquipmentInfo;
import de.findus.cydonia.messages.PickerInfo;


/**
 * @author Findus
 *
 */
public class ClientBeamerController implements ClientEquipmentController {

	private static Image hudImg;
	private MainController maincontroller;
	
	/**
	 * 
	 */
	public ClientBeamerController() {
		
	}

	@Override
	public void usePrimary(EquipmentModel e, boolean activate) {
		if(!(e instanceof BeamerModel)) return;
		
		BeamerModel beamer = (BeamerModel) e;
		beamer.setBeaming(activate);
	}

	@Override
	public void useSecondary(EquipmentModel e, boolean activate) {
		
	}

	@Override
	public BufferedImage getHUDImage(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		if(hudImg == null) {
			initHUDImgs();
		}
		
		BufferedImage tmpimg = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D) tmpimg.getGraphics();
		
		gr.drawImage(hudImg, 0, 0, new Color(0, 0, 0, 0), null);

		return tmpimg;
	}
	
	private void initHUDImgs() {
		try {
			hudImg = ImageIO.read(ClassLoader.getSystemResourceAsStream("de/findus/cydonia/gui/hud/beamer.png"));
		} catch (IOException e) {
		}
	}
	
	public void initGeometry(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		
		BeamerModel beamer = (BeamerModel) e;
		
		beamer.beam = new ParticleEmitter("Beam", Type.Triangle, 30);
        Material mat_red = new Material(maincontroller.getAssetManager(), 
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", maincontroller.getAssetManager().loadTexture(
                "Effects/Explosion/flame.png"));
        beamer.beam.setMaterial(mat_red);
        beamer.beam.setImagesX(2); 
        beamer.beam.setImagesY(2); // 2x2 texture animation
        beamer.beam.setEndColor(  new ColorRGBA(6f, 6f, 1f, 0.5f));
        beamer.beam.setStartColor(new ColorRGBA(3f, 3f, 1f, 1f));
        beamer.beam.setStartSize(0.01f);
        beamer.beam.setEndSize(0.005f);
        beamer.beam.setGravity(0, 0, 0);
        beamer.beam.setNumParticles(2000);
        beamer.beam.getParticleInfluencer().setVelocityVariation(1f);
        beamer.beam.setEnabled(false);
	    update(beamer);
	    
	    beamer.geom.attachChild(beamer.beam);
	}
	
	@Override
	public void setActive(EquipmentModel e, boolean active) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		
		BeamerModel beamer = (BeamerModel) e;
		beamer.beam.setEnabled(active);
		if(active) {
			beamer.geom.setLocalTranslation(0, 0.3f, 0.5f);
			beamer.geom.setCullHint(CullHint.Dynamic);
		}
	}

	@Override
	public void reset(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		
		BeamerModel beamer = (BeamerModel) e;
		beamer.beaming = false;
	}
	
	public void update(EquipmentModel e) {
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}
		
		BeamerModel beamer = (BeamerModel) e;
		if(beamer.beaming) {
			beamer.geom.setLocalTranslation(0, 0.3f, 0.2f);
			float angle = beamer.getPlayer().getViewDir().normalize().angleBetween(beamer.getPlayer().getControl().getViewDirection());
			if(beamer.getPlayer().getViewDir().normalize().getY() > 0) angle = -angle;
			beamer.beam.setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_X));
			beamer.beam.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 80f));
			beamer.beam.getParticleInfluencer().setVelocityVariation(0.01f);
			beamer.beam.setParticlesPerSec(2000);
			beamer.beam.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.01f));
			beamer.beam.setLowLife(0.06f);
			beamer.beam.setHighLife(0.1f);
		}else {
			beamer.geom.setLocalTranslation(0, 0.3f, 0.5f);
			beamer.beam.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0.1f));
			beamer.beam.getParticleInfluencer().setVelocityVariation(1f);
			beamer.beam.setParticlesPerSec(200);
			beamer.beam.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.05f));
			beamer.beam.setLowLife(0.05f);
			beamer.beam.setHighLife(0.13f);
		}
	}


	@Override
	public void loadInfo(EquipmentModel e, EquipmentInfo info) {
		if(!(info instanceof BeamerInfo)) {
			throw new InvalidParameterException("info must be of type " + BeamerInfo.class.getName());
		}
		if(!(e instanceof BeamerModel)) {
			throw new InvalidParameterException("e must be of type " + BeamerModel.class.getName());
		}

		BeamerModel beamer = (BeamerModel) e;
		PickerInfo i = (PickerInfo) info;
		beamer.setName(i.getName());
		beamer.setRange(i.getRange());
	}

	@Override
	public void setMainController(MainController mc) {
		this.maincontroller = mc;
	}
}
