package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import dronewars.serializable.Settings;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import dronewars.serializable.Drone;
import dronewars.serializable.Level;
import dronewars.ui.CameraWidget;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerState extends AbstractAppState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
        
   
    private Settings settings;
    
    private Drone drone;
    private Level level;
    private ChaseCamera chaseCam;
    
    private CameraWidget camWidget;
    private SimpleApplication app;    
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        this.app = (SimpleApplication) application;
        
        app.setDisplayFps(true);
        app.setDisplayStatView(true);
        
        level = new Level();
        level.create(app);
        
        settings = new Settings();
        settings.setProfile(2);
        settings.apply(app.getAssetManager(), app.getViewPort(), app.getCamera(),
                       level.getTerrain().getTerrainQuad(), level.getSky().getSun(), 
                       level.getWater().getWaterFilter(), app.getAudioRenderer());
        
        Path path = Paths.get(System.getProperty("user.dir") + "/drone.json");
        try {
            String json = new String(Files.readAllBytes(path));
            drone = new GsonBuilder().create().fromJson(json, Drone.class);
        } catch (IOException ex) {
            drone = new Drone();
        }
        drone.create(level.getNode(), level.getBullet(), app.getAssetManager());
        drone.getControl().setPhysicsLocation(new Vector3f(0, 100, 0));
        drone.setThrottle(1);
        
        setCameraMode(2);
    }
    
    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            if (level.getWater() != null)
                level.getWater().update(tpf);
            if (drone != null)
                drone.update(tpf);
            if (camWidget != null)
                camWidget.update(app.getCamera());
        }
        
    }
     
    public void setCameraMode(int mode) {
        if (chaseCam == null) {
            chaseCam = new ChaseCamera(app.getCamera(), drone.getSpatial(), app.getInputManager());
            chaseCam.setDefaultHorizontalRotation(0);
            chaseCam.setDefaultDistance(10);
            chaseCam.setDefaultVerticalRotation(FastMath.PI / 8);
            
            app.getFlyByCamera().setMoveSpeed(20);
        }
        if (mode == 0) {
            chaseCam.setEnabled(false);
            app.getFlyByCamera().setEnabled(true);
            app.getCamera().setLocation(specPosition);
            app.getCamera().setRotation(new Quaternion().fromAngles(specAngles));
        } else {
            app.getFlyByCamera().setEnabled(false);
            chaseCam.setEnabled(true);
            if (mode == 2) {
                chaseCam.setSmoothMotion(true);
                chaseCam.setTrailingEnabled(true);
            }
        }
    }
    
    public Drone getDrone() {
        return drone;
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