package dronewars.serializable;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Jan David Kleiß
 */
public class Level {
    private Vector3f gravity = new Vector3f(0, -32, 0);
    
    private Sky sky;
    private Water water;
    private Horizon horizon;
    private Terrain terrain;
    private Precipitation precipitation;
    
    private transient Node scene;
    private transient BulletAppState bullet;
    private transient SimpleApplication app;
        
    public void create(SimpleApplication app, BulletAppState bullet) {
        this.app = app;
        
        scene = new Node("Scene");
        app.getRootNode().attachChild(scene);
        
        app.getStateManager().attach(bullet);
        bullet.getPhysicsSpace().setGravity(gravity);
        
        if (sky == null)
            sky =  new Sky();
        sky.createSkybox(scene, app.getAssetManager());
        sky.createSun(app.getRootNode());
        sky.createAmbient(app.getRootNode());
                
        if (horizon == null)
            horizon = new Horizon();
        horizon.create(app.getRootNode(), app.getAssetManager());
        
        if (terrain == null)
            terrain = new Terrain();
        terrain.create(scene, bullet, sky.getSunColor(), app.getAssetManager());
        
        if (water == null)
            water = new Water();
        water.create(scene, sky.getSunDirection(), app.getCamera(), 
                app.getAudioRenderer(), app.getAssetManager());
        
        if (precipitation == null)
            precipitation = new Precipitation();
        precipitation.create(app.getRootNode(), terrain.getSize(), app.getAssetManager());
    }
    
    public Sky getSky() {
        return sky;
    }
    
    public Water getWater() {
        return water;
    }
    
    public Horizon getHorizon() {
        return horizon;
    }
    
    public Terrain getTerrain() {
        return terrain;
    }
    
    public Precipitation getPrecipitation() {
        return precipitation;
    }
    
    public Node getNode() {
        return scene;
    }
    
    public BulletAppState getBullet() {
        return bullet;
    }
    
    public SimpleApplication getApp(){
        return this.app;
    }
}
