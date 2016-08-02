package dronewars.main;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Node;
import dronewars.serializable.Warplane;

/**
 *
 * @author Jan David Kleiß
 */
public class HangarState extends AbstractAppState {
    
    private static final Vector3f camPos = new Vector3f(0, 1, 4);
    private static final float rotSpeed = FastMath.HALF_PI / 4;
    
    private Node node;
    private Warplane plane;
    private AmbientLight fog;
    private DirectionalLight sun;
    private StereoApplication app;
    private FilterPostProcessor filters;
    
    public HangarState(Warplane plane) {
        this.plane = plane;
    }
    
    @Override
    public void cleanup() {
        node.removeFromParent();
        app.getCamera().setFrustumNear(1);
        app.getViewPort().removeProcessor(filters);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        app = (StereoApplication) application;
        
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        
        node = new Node();
        app.getRootNode().attachChild(node);
        
        plane.createStatic(node, app.getAssetManager());
        
        addLights();
        applyFilters();
        setCamera();
    }
    
    private void addLights() {
        sun = new DirectionalLight();
        sun.setDirection(camPos.negate());
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

    private void setCamera() {
        app.getCamera().setFrustumNear(0.5f);
        app.getCamera().setLocation(camPos);
        app.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    @Override
    public void update(float tpf) {
        plane.getSpatial().rotate(0, tpf * rotSpeed, 0);
        plane.updateLaser();
        plane.updateRotors(1, 1);
    }
    
    public void setRenderedObject(String name) {
        plane.setName(name);
        plane.remove();
        if (app != null)
            plane.createStatic(node, app.getAssetManager());
    }
    
    public Warplane getRenderedObject() {
        return plane;
    }
}