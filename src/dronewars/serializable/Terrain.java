package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import dronewars.main.ImageFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class Terrain {

    private String name = "Default";
    private Vector3f scale = Vector3f.UNIT_XYZ;
    private int smoothing = 3;
    private boolean wet = true;
    
    private String redTexture = "Cracked";
    private int redScale = 128;
    private String greenTexture = "Sand";
    private int greenScale = 128;
    private String blueTexture = "Grass";
    private int blueScale = 128;
    private String alphaTexture = "Sand";
    private int alphaScale = 128;
    
    // not recommended for change
    private int patchSize = 128;
    private int shininess = 128;
    private boolean wire = false;
    
    private Vegetation vegetation;
    private Gradient alphaGradient;
    private Gradient spawnGradient;
    
    private transient TerrainQuad terrain;
    private transient BufferedImage spawnImage;
    private transient Node parent;
    private transient ColorRGBA sunColor;
    private transient BulletAppState bullet;
    private transient AssetManager assetManager;
    
    public Terrain() {}
        
    public void create(Node parent, BulletAppState bullet, 
            ColorRGBA sunColor, AssetManager assetManager) {
        this.parent = parent;
        this.bullet = bullet;
        this.sunColor = sunColor;
        this.assetManager = assetManager;
        
        Texture heightTex = assetManager.loadTexture(getRelativePath("height.png"));
        BufferedImage heightImage = ImageFactory.toBufferedImage(heightTex.getImage());
        AbstractHeightMap heightMap = new ImageBasedHeightMap(heightTex.getImage());
        heightMap.load();
        heightMap.smooth(1, smoothing);
        
        String alphaPath = getAbsolutePath("alpha.png");
        try {
            ImageFactory.load(alphaPath);
        } catch (IOException ex) {
            alphaGradient = new Gradient();
            Image img = ImageFactory.getAlphaMap(heightImage, alphaGradient);
            BufferedImage buffImg = ImageFactory.toBufferedImage(img);
            try {
                ImageFactory.save(buffImg, alphaPath);
            } catch (IOException ex1) {
                Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        setTerrain(new TerrainQuad("Terrain", patchSize + 1, heightMap.getSize() + 1, 
                heightMap.getScaledHeightMap()));
        terrain.setMaterial(getTerrainMaterial(sunColor, assetManager));
        terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        terrain.setLocalScale(scale);
        
        // avoids flickering when overlapping the horizon floor
        terrain.move(0, -0.1f, 0);
        parent.attachChild(terrain);
        
        CollisionShape terrainBox =  CollisionShapeFactory.createMeshShape(terrain);
        RigidBodyControl terrainControl = new RigidBodyControl(terrainBox, 0);
        getTerrainQuad().addControl(terrainControl);
        
        if (bullet != null)
            bullet.getPhysicsSpace().add(terrain);
        
        String spawnPath = getAbsolutePath("spawn.png");        
        try {
            spawnImage = ImageFactory.load(spawnPath);
        } catch (IOException ex) {
            spawnGradient = new Gradient();
            Image img = ImageFactory.getAlphaMap(heightImage, spawnGradient);
            spawnImage = ImageFactory.toBufferedImage(img);
            try {
                ImageFactory.save(spawnImage, spawnPath);
            } catch (IOException ex1) {
                Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        if (vegetation == null)
            vegetation = new Vegetation();
        vegetation.create(spawnImage, terrain, bullet, parent, assetManager);
    }
    
    private String getAbsolutePath(String file) {
        return System.getProperty("user.dir") + "/assets/Maps/" + name + "/" + file;
    }
    
    private String getRelativePath(String file) {
        return "Maps/" + name + "/" + file;
    }
    
    public Material getTerrainMaterial(ColorRGBA sunColor, AssetManager assetManager) {
        
        Material material = new Material(assetManager,
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
   
        material.setTexture("AlphaMap", assetManager.loadTexture(getRelativePath("alpha.png")));
        
        Texture redDiffuse = assetManager.loadTexture(
                "Textures/" + redTexture + "/diffuse.jpg");
        redDiffuse.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap", redDiffuse);
        material.setFloat("DiffuseMap_0_scale", redScale);
        
        Texture redNormal = assetManager.loadTexture(
                "Textures/" + redTexture + "/normal.jpg");
        redNormal.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("NormalMap", redNormal);

        Texture greenDiffuse = assetManager.loadTexture(
                "Textures/" + greenTexture + "/diffuse.jpg");
        greenDiffuse.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap_1", greenDiffuse);
        material.setFloat("DiffuseMap_1_scale", greenScale);
        
        Texture greenNormal = assetManager.loadTexture(
                "Textures/" + greenTexture + "/normal.jpg");
        greenNormal.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("NormalMap_1", greenNormal);

        Texture blueDiffuse = assetManager.loadTexture(
                "Textures/" + blueTexture + "/diffuse.jpg");
        blueDiffuse.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap_2", blueDiffuse);
        material.setFloat("DiffuseMap_2_scale", blueScale);
        
        Texture blueNormal = assetManager.loadTexture(
                "Textures/" + blueTexture + "/normal.jpg");
        blueNormal.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("NormalMap_2", blueNormal);
        
        Texture alphaDiffuse = assetManager.loadTexture(
                "Textures/" + alphaTexture + "/diffuse.jpg");
        alphaDiffuse.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap_3", alphaDiffuse);
        material.setFloat("DiffuseMap_3_scale", alphaScale);
        
        Texture alphaNormal = assetManager.loadTexture(
                "Textures/" + alphaTexture + "/normal.jpg");
        alphaNormal.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("NormalMap_3", alphaNormal);
        
        if (wet) {
            material.setBoolean("WardIso", true);
            material.setFloat("Shininess", shininess);
        }
        
        if (wire)
            material.getAdditionalRenderState().setWireframe(true);
        
        return material;
    }
    
    public void remove() {
        vegetation.remove();
        terrain.removeFromParent();
    }
    
    public void reload() {
        remove();
        create(parent, bullet, sunColor, assetManager);
    }
    
    private void reloadVegetation() {
        vegetation.remove();
        if (vegetation == null)
            vegetation = new Vegetation();
        vegetation.create(spawnImage, terrain, bullet, parent, assetManager);
    }

    public Vector3f getScaleVector() {
        return scale;
    }

    public void setScaleVector(Vector3f scale) {
        this.scale = scale;
        terrain.setLocalScale(scale);
        reloadVegetation();
    }
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Gradient getAlphaGradient() {
        return alphaGradient;
    }
    
    public Gradient getSpawnGradient() {
        return alphaGradient;
    }

    public int getSize() {
        return terrain.getTerrainSize();
    }
    
    public String getRedTexture() {
        return redTexture;
    }
    
    public void setRedTexture(String redTexture) {
        this.redTexture = redTexture;
    }

    public int getRedScale() {
        return redScale;
    }

    public void setRedScale(int redScale) {
        this.redScale = redScale;
    }
    
    public String getGreenTexture() {
        return greenTexture;
    }
    
    public void setGreenTexture(String greenTexture) {
        this.greenTexture = greenTexture;
    }
    
    public int getGreenScale() {
        return greenScale;
    }
    
    public void setGreenScale(int greenScale) {
        this.greenScale = greenScale;
    }
    
    public String getBlueTexture() {
        return blueTexture;
    }
    
    public void setBlueTexture(String blueTexture) {
        this.blueTexture = blueTexture;
    }
    
    public int getBlueScale() {
        return blueScale;
    }
    
    public void setBlueScale(int blueScale) {
        this.blueScale = blueScale;
    }
    
    public String getAlphaTexture() {
        return alphaTexture;
    }
    
    public void setAlphaTexture(String alphaTexture) {
        this.alphaTexture = alphaTexture;
    }
    
    public int getAlphaScale() {
        return alphaScale;
    }
    
    public void setAlphaScale(int alphaScale) {
        this.alphaScale = alphaScale;
    }
    
    public TerrainQuad getTerrainQuad() {
        return terrain;
    }
    
    public void setTerrain(TerrainQuad terrain) {
        this.terrain = terrain;
    }
}
