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
    private Vector3f sunVector = new Vector3f(-0.7f, -0.2f, 0.7f);
    
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
        sun.setDirection(sunVector);
        sun.setColor(sunColor.mult(sunColor.a));
        node.addLight(sun);
    }
    
    public void createAmbient(Node node) {
        ambient = new AmbientLight();
        ambient.setColor(ambientColor.mult(ambientColor.a));
        node.addLight(ambient);
    }
    
    public DirectionalLight getSun() {
        return sun;
    }
    
    public AmbientLight getAmbient() {
        return ambient;
    }
    
    public ColorRGBA getSunColor() {
        return sunColor;
    }
    
    public ColorRGBA getFogColor() {
        return ambientColor;
    }
    
    public Vector3f getSunVector() {
        return sunVector;
    }
    
    // CONSISTENT Color/ColorRGBA usage
    // plus update/set behaviour
    
    public void setSunColor(ColorRGBA color) {
        sun.getColor().r = color.r * color.a;
        sun.getColor().g = color.g * color.a;
        sun.getColor().b = color.b * color.a;
    }
    
    public void setFogColor(ColorRGBA color) {
        ambient.getColor().r = color.r * color.a;
        ambient.getColor().g = color.g * color.a;
        ambient.getColor().b = color.b * color.a;
    }
    
    public Spatial getSpatial() {
        return sky;
    }
}