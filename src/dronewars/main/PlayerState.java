package dronewars.main;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import dronewars.serializable.Settings;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerState extends AbstractAppState {
   
    private static final Vector3f gravity = new Vector3f(0, -30, 0);
    private static final float camSensitivity = 5;
    private Settings settings;
    
    private Warzone warzone;
    private Level level;
    private ChaseCamera chaseCam;
    private SimpleApplication app;
    private BulletAppState bullet;
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        this.app = (SimpleApplication) application;
        
        bullet = new BulletAppState();
        app.getStateManager().attach(bullet);
        bullet.getPhysicsSpace().setGravity(gravity);
        
        app.setDisplayFps(true);
        app.setDisplayStatView(true);
        
        level = new Level();
        level.create(app, bullet);
        
        settings = new Settings();
        settings.setProfile(2);
        settings.apply(app.getAssetManager(), app.getViewPort(), app.getCamera(),
                       level.getTerrain().getTerrainQuad(), level.getSky().getSun(), 
                       level.getWater().getWaterFilter(), app.getAudioRenderer());
        
        warzone = new Warzone(app.getRootNode(), app.getTimer(), bullet,
                level, app.getAssetManager());
        warzone.addPlayer();
        
        bullet.getPhysicsSpace().clearForces();
        
        initCamera();
    }
    
    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            if (level.getWater() != null)
                level.getWater().update(tpf);
            if (warzone != null)
                warzone.update(tpf);
        }
    }
     
    public void initCamera() {        
        app.getFlyByCamera().setEnabled(false);
        chaseCam = new ChaseCamera(app.getCamera(), 
                warzone.getPlayer().getSpatial(), app.getInputManager());
        chaseCam.setTrailingSensitivity(camSensitivity);
        chaseCam.setDefaultHorizontalRotation(0);
        chaseCam.setDefaultDistance(10);
        chaseCam.setDefaultVerticalRotation((float)Math.toRadians(10));
        chaseCam.setEnabled(true);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
    }
    
    public WarplaneControl getCombatControl() {
        return warzone.getControl();
    }
    
    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }
    
    public InputManager getInputManager() {
        return app.getInputManager();
    }
    
    public Node getRootNode() {
        return app.getRootNode();
    }
    
    public Camera getCamera() {
        return app.getCamera();
    }
    
    public AudioRenderer getAudioRenderer() {
        return app.getAudioRenderer();
    }
    
    public ViewPort getViewPort() {
        return app.getViewPort();
    }
    
    public Node getGuiNode() {
        return app.getGuiNode();
    }
}