/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

import de.findus.cydonia.main.GameController;

/**
 * This is the first test level.
 * @author Findus
 */
public class Level1 implements Level{
    
	@Override
    public Node getScene(AssetManager assetManager) {
        Material mat_pflaster = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex_pflaster = assetManager.loadTexture(GameController.TEXTURES_PATH + "pflaster1.jpg");
        mat_pflaster.setTexture("ColorMap", tex_pflaster);
        
        
        Material mat_wand = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex_wand = assetManager.loadTexture(GameController.TEXTURES_PATH + "wand1.jpg");
        mat_wand.setTexture("ColorMap", tex_wand);
        
        
        
        Box box1 = new Box( new Vector3f(0,-1,0), 20,1,20);
        Geometry floor1 = new Geometry("Box", box1);
        floor1.setMaterial(mat_pflaster);
 
        Box box2 = new Box( new Vector3f(0,1,0), 1,1,1);
        Geometry block = new Geometry("Box", box2);
        block.setMaterial(mat_wand);
        
        Node stair = makeStair("Treppe", 4, 2, 2);
        stair.setMaterial(mat_pflaster);
        stair.setLocalTranslation(-5, 0, 0);
        
        Node scene = new Node("Level1");
        scene.attachChild(floor1);
        scene.attachChild(block);
        scene.attachChild(stair);
        
        return scene;
    }
    
	/**
	 * Makes a stair. Step height is <code>GameController.MAX_STEP_HEIGHT</code>.
	 * @param name name of the stairs root node
	 * @param depth total depth of the stair (X)
	 * @param height total height of the stair (Y)
	 * @param width total width of the stair (Z)
	 * @return root node object of the stair
	 */
    private static Node makeStair(String name, float depth, float height, float width) {
        Node stair = new Node(name);
        
        int stepCount = (int) Math.floor(height / GameController.MAX_STEP_HEIGHT);
        float stepDepth = depth / stepCount;
        for (int i = 0; i < stepCount; i++) {
            //Box step = new Box(new Vector3f(stepDepth * i,Main.MAX_STEP_HEIGHT * i, 0), stepDepth/2, Main.MAX_STEP_HEIGHT/2, width/2);
            Box step = new Box(new Vector3f(stepDepth*i, GameController.MAX_STEP_HEIGHT*i, -width/2), new Vector3f(stepDepth*(i+1), GameController.MAX_STEP_HEIGHT*(i+1), width/2));
            stair.attachChild(new Geometry("Step" + i, step));
        }
        
        return stair;
    }
}
