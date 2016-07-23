package dronewars.main;

import com.jme3.input.ChaseCamera;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerState extends LevelState {
   
    private static final float camElevation = 10; // in degrees
    private static final float camDistance = 10;
    private static final float camSensitivity = 2;
    
    private Warzone warzone;
    private ChaseCamera chaseCam;
    
    private boolean stereo = true;
    
    @Override
    public void init() {
        warzone = new Warzone(app.getRootNode(), app.getTimer(), bullet,
                level, app.getAssetManager());
        warzone.addPlayer();
        
        initCamera();
    }
    
    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            if (level.getWater() != null)
                level.getWater().update(tpf);
            if (warzone != null)
                warzone.update(tpf);
            app.getListener().setLocation(warzone.getPlayer().getSpatial().getLocalTranslation());
            app.getListener().setRotation(warzone.getPlayer().getSpatial().getLocalRotation());
        }
    }
     
    public void initCamera() {        
        app.getFlyByCamera().setEnabled(false);
        chaseCam = new ChaseCamera(app.getCamera(), 
                warzone.getPlayer().getSpatial(), app.getInputManager());
        chaseCam.setDefaultVerticalRotation((float)Math.toRadians(camElevation));
        chaseCam.setDefaultHorizontalRotation(0);
        chaseCam.setDefaultDistance(camDistance);
        chaseCam.setEnabled(true);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setTrailingSensitivity(camSensitivity);
    }
    
    @Override
    protected void remove() {
        app.getRootNode().detachAllChildren();
    }
    
    public WarplaneControl getCombatControl() {
        return warzone.getPlayer().getControl();
    }
}