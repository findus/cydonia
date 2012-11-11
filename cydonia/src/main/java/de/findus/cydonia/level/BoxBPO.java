/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * @author Findus
 *
 */
public class BoxBPO {

	private Material mat_red;
	private Material mat_blue;
	private Material mat_grey;
	
	/**
	 * 
	 */
	public BoxBPO(AssetManager assetManager) {
		mat_blue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    mat_blue.setColor("Color",ColorRGBA.Blue);
		
		mat_red =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_red.setColor("Color",ColorRGBA.Red);
		
		mat_grey =  new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_grey.setColor("Color",ColorRGBA.Gray);
	}
	
	public Spatial createBox(String color, boolean moveable) {
		Box box = new Box( new Vector3f(0,0.5f,0), 0.5f, 0.5f, 0.5f);
        Geometry spatial = new Geometry("Box", box);
        spatial.setUserData("movable", moveable);
        
        if("red".equals(color)) {
        	spatial.setMaterial(mat_red);
        }else if ("blue".equals(color)) {
			spatial.setMaterial(mat_blue);
		}else {
			spatial.setMaterial(mat_grey);
		}
        
        return spatial;
	}

}
