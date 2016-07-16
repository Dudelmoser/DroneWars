package dronewars.main;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Node;
import dronewars.serializable.Drone;

/**
 *
 * @author Jan David Kleiß
 */
public class DroneState extends AbstractAppState {
        
    private Node node;
    private Drone drone;
    private AmbientLight fog;
    private DirectionalLight sun;
    private ChaseCamera chaseCam;
    private SimpleApplication app;
    private FilterPostProcessor filters;
    
    public DroneState(Drone drone) {
        this.drone = drone;
    }
    
    @Override
    public void cleanup() {
        app.getRootNode().detachChild(node);
        app.getViewPort().removeProcessor(filters);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        this.app = (SimpleApplication) application;
        
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        
        node = new Node();
        app.getRootNode().attachChild(node);
        
        drone.create(node, null, app.getAssetManager());
        
        addLights();
        applyFilters();
        
        app.getCamera().setFrustumNear(0.5f);
        chaseCam = new ChaseCamera(app.getCamera(), drone.getSpatial(), app.getInputManager());
        chaseCam.setMinDistance(1.5f);
        chaseCam.setMaxDistance(5);
        chaseCam.setDefaultDistance(2);
        chaseCam.setZoomSensitivity(0.05f);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 8);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI / -1.6f);
    }
    
    private void addLights() {
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-1,1));
        sun.setColor(ColorRGBA.White.mult(0.5f));
        node.addLight(sun);
        
        fog = new AmbientLight();
        fog.setColor(ColorRGBA.Blue);
        node.addLight(sun);
    }
    
    private void applyFilters() {
        filters = new FilterPostProcessor(app.getAssetManager());
                
        FXAAFilter fxaa = new FXAAFilter();
        filters.addFilter(fxaa);
        
        SSAOFilter ssao = new SSAOFilter();
        filters.addFilter(ssao);
        
        app.getViewPort().addProcessor(filters);
    }
    
    public Drone getDrone() {
        return drone;
    }
    
    @Override
    public void update(float tpf) {}
}