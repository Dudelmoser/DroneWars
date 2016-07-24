/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

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
import dronewars.serializable.Level;
import dronewars.serializable.Settings;

/**
 *
 * @author Jan David Kleiß
 */
public abstract class LevelState extends AbstractAppState {
    
    private static final Vector3f gravity = new Vector3f(0, -30, 0);
    
    protected StereoApplication app;
    protected BulletAppState bullet;
    protected Level level;
    protected Settings settings;
            
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        app = (StereoApplication) application;
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        
        bullet = new BulletAppState();
        app.getStateManager().attach(bullet);
        bullet.getPhysicsSpace().setGravity(gravity);
        
        level = new Level();
        level.create(app, bullet);
        
        settings = new Settings();
        settings.setProfile(2);
        settings.apply(app.getAssetManager(), app.getViewPort(), app.getCamera(),
                       level.getTerrain().getTerrainQuad(), level.getSky().getSun(), 
                       level.getWater().getWaterFilter(), app.getAudioRenderer());
        init();
    }
        
    protected abstract void init();
    
    @Override
    public void cleanup() {
        app.getRootNode().detachAllChildren();
        
        LightList lights = app.getRootNode().getWorldLightList();
        for (Light light : lights) {
            app.getRootNode().removeLight(light);
        }
        
        app.getViewPort().removeProcessor(app.getViewPort().getProcessors().get(0));
        
        level.getWater().getAudioNode().stop();
        app.getStateManager().detach(bullet);
    }
    
    protected abstract void remove();
    
    public Level getLevel() {
        return level;
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
}
