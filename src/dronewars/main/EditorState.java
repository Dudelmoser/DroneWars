package dronewars.main;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import dronewars.serializable.Settings;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorState extends AbstractAppState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
           
    private Settings settings;
    
    private Level level;
    private SimpleApplication app;
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        this.app = (SimpleApplication) application;
        
        level = new Level();
        level.create(app, null);
        
        settings = new Settings();
        settings.setProfile(2);
        settings.apply(app.getAssetManager(), app.getViewPort(), app.getCamera(),
                       level.getTerrain().getTerrainQuad(), level.getSky().getSun(), 
                       level.getWater().getWaterFilter(), app.getAudioRenderer());
        
        app.getFlyByCamera().setEnabled(true);
        app.getCamera().setLocation(specPosition);
        app.getCamera().setRotation(new Quaternion().fromAngles(specAngles));
    }
    
    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            if (level.getWater() != null)
                level.getWater().update(tpf);
        }
    }
    
    @Override
    public void cleanup() {
        app.getRootNode().detachAllChildren();
        
        LightList lights = app.getRootNode().getWorldLightList();
        for (Light light : lights) {
            app.getRootNode().removeLight(light);
        }
        
        app.getViewPort().removeProcessor(app.getViewPort().getProcessors().get(0));
        
        level.getWater().getAudioNode().stop();
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