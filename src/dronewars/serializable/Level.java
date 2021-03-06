package dronewars.serializable;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;

/**
 *
 * @author Jan David Kleiß
 */
public class Level {
    
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
        this.bullet = bullet;
        
        scene = new Node("Scene");
        app.getRootNode().attachChild(scene);
        
        if (sky == null)
            sky =  new Sky();
        sky.createSkybox(scene, app.getAssetManager());
        sky.createSun(app.getRootNode());
        sky.createAmbient(app.getRootNode());
                
        if (horizon == null)
            horizon = new Horizon();
        horizon.create(app.getRootNode(), bullet, app.getAssetManager());
        
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
    
    public void remove() {
        sky.remove();
        horizon.remove();
        terrain.remove();
        water.remove();
        precipitation.remove();
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
