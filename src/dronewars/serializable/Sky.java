package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Jan David Kleiß
 */
public class Sky {
    private String name = "1";
    
    private ColorRGBA sunColor = ColorRGBA.White;
    private ColorRGBA ambientColor = ColorRGBA.White;
    private Vector3f sunDirection = new Vector3f(-0.7f, -0.2f, 0.7f);
    
    private transient Spatial sky;
    private transient AmbientLight ambient;
    private transient DirectionalLight sun;
    
    public Sky() {}
    
    public void create(Node node, AssetManager assetManager) {
        createSkybox(node, assetManager);
        createAmbient(node);
        createSun(node);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void update(AssetManager assetManager) {
        Node parent = sky.getParent();
        parent.detachChild(sky);
        createSkybox(parent, assetManager);
    }
    
    public void createSkybox(Node node, AssetManager assetManager) {
        Texture up = assetManager.loadTexture("Skies/" + name + "/up.png");
        Texture down = assetManager.loadTexture("Skies/" + name + "/down.png");
        Texture east = assetManager.loadTexture("Skies/" + name + "/east.png");
        Texture west = assetManager.loadTexture("Skies/" + name + "/west.png");
        Texture north = assetManager.loadTexture("Skies/" + name + "/north.png");
        Texture south = assetManager.loadTexture("Skies/" + name + "/south.png");
        sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        node.attachChild(sky);
    }
    
    public void createSun(Node node) {
        sun = new DirectionalLight();
        sun.setDirection(sunDirection);
        sun.setColor(sunColor.mult(sunColor.a));
        node.addLight(sun);
    }
    
    public void createAmbient(Node node) {
        ambient = new AmbientLight();
        ambient.setColor(ambientColor.mult(ambientColor.a));
        node.addLight(ambient);
    }
    
    public DirectionalLight getSunLight() {
        return sun;
    }
    
    public AmbientLight getAmbientLight() {
        return ambient;
    }
    
    public Spatial getSpatial() {
        return sky;
    }
    
    public ColorRGBA getSunColor() {
        return sunColor;
    }
    
    public void setSunColor(ColorRGBA color) {
        sunColor.set(color);
    }
    
    public ColorRGBA getAmbientColor() {
        return ambientColor;
    }
        
    public void setAmbientColor(ColorRGBA color) {
        ambientColor = color;
        ambient.setColor(color);
    }
    
    public Vector3f getSunDirection() {
        return sunDirection;
    }
    
    public void setSunDirection(Vector3f vec) {
        sunDirection = vec;
        sun.setDirection(vec);
    }
}