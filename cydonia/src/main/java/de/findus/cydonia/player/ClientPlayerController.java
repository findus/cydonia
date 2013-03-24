/**
 * 
 */
package de.findus.cydonia.player;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import de.findus.cydonia.main.MainController;

/**
 * @author Findus
 *
 */
public class ClientPlayerController extends PlayerController {


	public ClientPlayerController(MainController mainController) {
		super(mainController);
	}
	
	public void setTransparency(Player p, float transparency) {
		Node n = (Node) p.getModel();
		if(n == null) return;
		
		transparency = Math.max(0f, Math.min(1f, transparency));
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		int cw = Math.round(255*transparency);
		Color c = new Color(cw, cw, cw);
		img.setRGB(0, 0, c.getRGB());
		
		AWTLoader loader =new AWTLoader();
		Image imageJME = loader.load(img, true);
		Texture t = new Texture2D(imageJME);
		
		ColorRGBA glowcolor = new ColorRGBA(0, 0, 0, cw);
		
		for(Spatial s : n.getChildren()) {
			if(s instanceof Geometry) {
				Material m = ((Geometry) s).getMaterial();
				m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				m.setTexture("AlphaMap", t);
				m.setColor("GlowColor", glowcolor);
			}
		}
	}
	
	public void setHealthpoints(Player p, double health) {
		super.setHealthpoints(p, health);
		
		if(p == null) return;
		setTransparency(p, (float)p.getHealthpoints() * 0.008f + 0.2f);
	}

	@Override
	protected String getType() {
		return "Client";
	}

}
