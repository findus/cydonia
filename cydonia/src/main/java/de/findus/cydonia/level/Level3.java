/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import de.findus.cydonia.main.GameController;

/**
 * @author Findus
 *
 */
public class Level3 implements Level {

	@Override
	public Node getScene(AssetManager assetManager) {
		Node levelRoot = new Node("levelRoot");
		
		Texture tex_pflaster = assetManager.loadTexture(GameController.TEXTURES_PATH + "pflaster1.jpg");
		Material mat_pflaster = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat_pflaster.setTexture("DiffuseMap", tex_pflaster);
	    mat_pflaster.setBoolean("UseMaterialColors",true);    
	    mat_pflaster.setColor("Specular",ColorRGBA.White);
	    mat_pflaster.setColor("Diffuse",ColorRGBA.White);
	    mat_pflaster.setColor("Ambient",ColorRGBA.White);
	    mat_pflaster.setFloat("Shininess", 1f);
	    
	    Material mat_rock = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    Texture pond = assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg");
	    pond.setWrap(WrapMode.Repeat);
	    mat_rock.setTexture("DiffuseMap", pond);
	    Texture pond_normal = assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png");
	    pond_normal.setWrap(WrapMode.Repeat);
//	    mat_lit.setTexture("NormalMap", pond_normal);
	    mat_rock.setBoolean("UseMaterialColors",true);    
	    mat_rock.setColor("Specular",ColorRGBA.White);
	    mat_rock.setColor("Diffuse",ColorRGBA.White);
	    mat_rock.setColor("Ambient",ColorRGBA.White);
	    mat_rock.setFloat("Shininess", 1f); 

		Quad quad1 = new Quad(100, 100);
		quad1.scaleTextureCoordinates(new Vector2f(4f, 4f));
		Geometry floor1 = new Geometry("Floor", quad1);
		floor1.setMaterial(mat_rock);
		floor1.rotate((float) (-Math.PI/2), 0, 0);
		floor1.move(-50, 0, 50);
		floor1.setShadowMode(ShadowMode.Receive);
		floor1.setUserData("PlaceableSurface", true);
		levelRoot.attachChild(floor1);
		
		Material mat_wall = assetManager.loadMaterial("Textures/BumpMapTest/Tangent.j3m");
//		Material mat_wall = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//	    mat_wall.setFloat("Shininess", 2f);
//	    mat_wall.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
//	    mat_wall.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg"));
//	    mat_wall.setTexture("ParallaxMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_height.jpg"));
		
//		Box box1 = new Box(25, 1.5f, 0.25f);
//		box1.scaleTextureCoordinates(new Vector2f(6.25f, 0.375f));
//		Geometry wall1 = new Geometry("Wall1", box1);
//		wall1.setMaterial(mat_rock);
//		wall1.rotate(0, (float) (Math.PI/4), 0);
//		wall1.setLocalTranslation(25, 1.5f, 25);
//		wall1.setShadowMode(ShadowMode.CastAndReceive);
//		wall1.setUserData("PlaceableSurface", true);
//		levelRoot.attachChild(wall1);
//		
//		Geometry wall2 = new Geometry("Wall2", box1);
//		wall2.setMaterial(mat_rock);
//		wall2.rotate(0, (float) (-Math.PI/4), 0);
//		wall2.setLocalTranslation(-25, 1.5f, 25);
//		wall2.setShadowMode(ShadowMode.CastAndReceive);
//		wall2.setUserData("PlaceableSurface", true);
//		levelRoot.attachChild(wall2);
//		
//		Geometry wall3 = new Geometry("Wall3", box1);
//		wall3.setMaterial(mat_rock);
//		wall3.rotate(0, (float) (Math.PI/4), 0);
//		wall3.setLocalTranslation(-25, 1.5f, -25);
//		wall3.setShadowMode(ShadowMode.CastAndReceive);
//		wall3.setUserData("PlaceableSurface", true);
//		levelRoot.attachChild(wall3);
//		
//		Geometry wall4 = new Geometry("Wall4", box1);
//		wall4.setMaterial(mat_rock);
//		wall4.rotate(0, (float) (-Math.PI/4), 0);
//		wall4.setLocalTranslation(25, 1.5f, -25);
//		wall4.setShadowMode(ShadowMode.CastAndReceive);
//		wall4.setUserData("PlaceableSurface", true);
//		levelRoot.attachChild(wall4);
		
		return levelRoot;
	}

	@Override
	public SpawnPoint[] getSpawnPoints() {
		SpawnPoint sp1 = new SpawnPoint();
		sp1.setPosition(new Vector3f(10, 1, 10));
		sp1.setTeam(1);
		
		SpawnPoint sp2 = new SpawnPoint();
		sp2.setPosition(new Vector3f(-10, 1, -10));
		sp2.setTeam(2);
		
		return new SpawnPoint[]{sp1, sp2};
	}

	@Override
	public SpawnPoint getSpawnPoint(int team) {
		SpawnPoint sp = new SpawnPoint();
		if(team == 1) {
			sp.setPosition(new Vector3f(10, 1, 10));
			sp.setTeam(1);
		}else if(team == 2) {
			sp.setPosition(new Vector3f(-10, 1, -10));
			sp.setTeam(2);
		}
		return sp;
	}

}
