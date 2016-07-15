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
import com.jme3.texture.Texture;
import dronewars.main.ImageFactory;
import dronewars.main.MapFactory;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jan David Kleiß
 */
public class Terrain {

    private String map = "Default";
    private float height = 0.3f;
    private int smoothing = 3;
    private int scale = 1;
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
    
    private transient TerrainQuad terrain;
    private transient BufferedImage heightImage;
    private transient BufferedImage spawnImage;
    
    public Terrain() {}
        
    public void create(Node node, BulletAppState bullet, 
            ColorRGBA sunColor, AssetManager assetManager) {
        
        Texture heightTex = assetManager.loadTexture(
                "Maps/" + map + "/height.png");
        heightImage = MapFactory.fromImage(heightTex.getImage());
        AbstractHeightMap heightMap = new ImageBasedHeightMap(heightTex.getImage());
        heightMap.load();
        heightMap.smooth(1, smoothing);
        
        setTerrain(new TerrainQuad("Terrain", patchSize + 1, heightMap.getSize() + 1, 
                heightMap.getScaledHeightMap()));
        terrain.setMaterial(getTerrainMaterial(sunColor, assetManager));
        terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        terrain.setLocalScale(new Vector3f(scale, height, scale));
        
        // avoids flickering when overlapping the horizon floor
        terrain.move(0, -0.1f, 0);
        node.attachChild(terrain);
        
        CollisionShape terrainBox =  CollisionShapeFactory.createMeshShape(terrain);
        RigidBodyControl terrainControl = new RigidBodyControl(terrainBox, 0);
        getTerrainQuad().addControl(terrainControl);
        
        bullet.getPhysicsSpace().add(terrain);
                
        vegetation = new Vegetation();
        Texture spawnTex = assetManager.loadTexture(
                "Maps/" + map + "/spawn.png");
        spawnImage = ImageFactory.toBufferedImage(spawnTex.getImage());
        vegetation.create(spawnImage, terrain, bullet, node, assetManager);
    }
    
    public Material getTerrainMaterial(ColorRGBA sunColor, AssetManager assetManager) {
        Material material = new Material(assetManager,
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
   
        material.setTexture("AlphaMap", assetManager.loadTexture(
            "Maps/" + map + "/alpha.png"));
        
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
    
    public BufferedImage getHeightmap() {
        return heightImage;
    }
    
    /**
     * @return the map
     */
    public String getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * @return the heightScale
     */
    public float getHeightScale() {
        return height;
    }

    /**
     * @param heightScale the heightScale to set
     */
    public void setHeightScale(float heightScale) {
        this.height = heightScale;
    }

    /**
     * @return the smoothing
     */
    public int getSmoothing() {
        return smoothing;
    }

    /**
     * @param smoothing the smoothing to set
     */
    public void setSmoothing(int smoothing) {
        this.smoothing = smoothing;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return terrain.getTerrainSize();
    }

    /**
     * @return the patchSize
     */
    public int getPatchSize() {
        return patchSize;
    }

    /**
     * @param patchSize the patchSize to set
     */
    public void setPatchSize(int patchSize) {
        this.patchSize = patchSize;
    }

    /**
     * @return the wet
     */
    public boolean isWet() {
        return wet;
    }

    /**
     * @param wet the wet to set
     */
    public void setWet(boolean wet) {
        this.wet = wet;
    }

    /**
     * @return the shininess
     */
    public int getShininess() {
        return shininess;
    }

    /**
     * @param shininess the shininess to set
     */
    public void setShininess(int shininess) {
        this.shininess = shininess;
    }

    /**
     * @return the redTexture
     */
    public String getRedTexture() {
        return redTexture;
    }

    /**
     * @param redTexture the redTexture to set
     */
    public void setRedTexture(String redTexture) {
        this.redTexture = redTexture;
    }

    /**
     * @return the redScale
     */
    public int getRedScale() {
        return redScale;
    }

    /**
     * @param redScale the redScale to set
     */
    public void setRedScale(int redScale) {
        this.redScale = redScale;
    }

    /**
     * @return the greenTexture
     */
    public String getGreenTexture() {
        return greenTexture;
    }

    /**
     * @param greenTexture the greenTexture to set
     */
    public void setGreenTexture(String greenTexture) {
        this.greenTexture = greenTexture;
    }

    /**
     * @return the greenScale
     */
    public int getGreenScale() {
        return greenScale;
    }

    /**
     * @param greenScale the greenScale to set
     */
    public void setGreenScale(int greenScale) {
        this.greenScale = greenScale;
    }

    /**
     * @return the blueTexture
     */
    public String getBlueTexture() {
        return blueTexture;
    }

    /**
     * @param blueTexture the blueTexture to set
     */
    public void setBlueTexture(String blueTexture) {
        this.blueTexture = blueTexture;
    }

    /**
     * @return the blueScale
     */
    public int getBlueScale() {
        return blueScale;
    }

    /**
     * @param blueScale the blueScale to set
     */
    public void setBlueScale(int blueScale) {
        this.blueScale = blueScale;
    }

    /**
     * @return the alphaTexture
     */
    public String getAlphaTexture() {
        return alphaTexture;
    }

    /**
     * @param alphaTexture the alphaTexture to set
     */
    public void setAlphaTexture(String alphaTexture) {
        this.alphaTexture = alphaTexture;
    }

    /**
     * @return the alphaScale
     */
    public int getAlphaScale() {
        return alphaScale;
    }

    /**
     * @param alphaScale the alphaScale to set
     */
    public void setAlphaScale(int alphaScale) {
        this.alphaScale = alphaScale;
    }

    /**
     * @return the terrain
     */
    public TerrainQuad getTerrainQuad() {
        return terrain;
    }

    /**
     * @param terrain the terrain to set
     */
    public void setTerrain(TerrainQuad terrain) {
        this.terrain = terrain;
    }
}
