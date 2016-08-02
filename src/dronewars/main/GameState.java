/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import dronewars.io.JsonFactory;
import com.google.gson.GsonBuilder;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import dronewars.io.UdpBroadcastHandler;
import dronewars.io.UdpBroadcastSocket;
import dronewars.serializable.Level;
import dronewars.serializable.Settings;

/**
 *
 * @author Jan David Kleiß
 */
public abstract class GameState extends AbstractAppState implements UdpBroadcastHandler {
    
    protected static final int PORT = 54320;
    protected static final int LEVEL_SEND_INTERVAL = 1;
    protected static final Vector3f GRAVITY = new Vector3f(0, -30, 0);
    
    protected StereoApplication app;
    protected BulletAppState bullet;
    protected Level level;
    protected Settings settings;
    protected UdpBroadcastSocket udp;
    protected Warzone warzone;
    
    protected float queueTime = LEVEL_SEND_INTERVAL * 5;
    protected float sendTime = 0;
    protected String levelJson = null;
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        app = (StereoApplication) application;
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        initBullet();
        udp = new UdpBroadcastSocket(this, PORT, 2048);
        
        onInitialize();
    }
    
    @Override
    public void update(float tpf) {
        udp.send("");
        if (isEnabled()) {            
            if (warzone != null) {
                warzone.update(tpf);
                
                if (warzone.getPlayer() != null) {
                    app.getListener().setLocation(warzone.getPlayer().getSpatial().getLocalTranslation());
                    app.getListener().setRotation(warzone.getPlayer().getSpatial().getLocalRotation());
                }
            }
            
            if (level != null && level.getWater() != null && level.getWater().getWaterFilter() != null) {
                level.getWater().update(tpf);
            }
        }

        onUpdate(tpf);
    }
    
    @Override
    public void cleanup() {
        level.remove();
        app.getRootNode().detachAllChildren();
        
        LightList lights = app.getRootNode().getWorldLightList();
        for (Light light : lights) {
            app.getRootNode().removeLight(light);
        }
        
        app.getViewPort().removeProcessor(app.getViewPort().getProcessors().get(0));
        
        app.getStateManager().detach(bullet);
        udp.close();
        onCleanup();
    }
    
    protected void initLevel() {
        level.create(app, bullet);
        levelJson = new GsonBuilder().create().toJson(level);
        
        warzone = new Warzone(app.getRootNode(), app.getTimer(), bullet,
            level, app.getAssetManager());
        applySettings();
    }
    
    protected void initBullet() {
        bullet = new BulletAppState();
        app.getStateManager().attach(bullet);
        bullet.getPhysicsSpace().setGravity(GRAVITY);
    }
    
    protected void applySettings() {
        settings = JsonFactory.load(Settings.class);
        settings.setProfile(2);
        settings.apply(app.getAssetManager(), app.getViewPort(), app.getCamera(),
                       level.getTerrain().getTerrainQuad(), level.getSky().getSunLight(), 
                       level.getWater().getWaterFilter(), app.getAudioRenderer());
    }
       
    protected abstract void onInitialize();
    protected abstract void onUpdate(float tpf);
    protected abstract void onCleanup();
           
    public Level getLevel() {
        return level;
    }
    
    public void setLevel(Level level) {
        this.level = level;
    }
    
    public Warzone getWarzone() {
        return warzone;
    }
    
    public Settings getSettings() {
        return settings;
    }
    
    public Node getGuiNode() {
        return app.getGuiNode();
    }
    
    public Node getRootNode() {
        return app.getRootNode();
    }
    
    public Camera getCamera() {
        return app.getCamera();
    }
    
    public ViewPort getViewPort() {
        return app.getViewPort();
    }
    
    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }
    
    public InputManager getInputManager() {
        return app.getInputManager();
    }
    
    public AudioRenderer getAudioRenderer() {
        return app.getAudioRenderer();
    }
    
    public StereoApplication getApp(){
        return this.app;
    }
    
    public BulletAppState getBullet(){
        return this.bullet;
    }
}
