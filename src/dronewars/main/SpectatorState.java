package dronewars.main;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jan David Kleiß
 */
public class SpectatorState extends LevelState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    
    private Warzone warzone;
    
    @Override
    protected void init() {
        
        warzone = new Warzone(app.getRootNode(), app.getTimer(), bullet,
                level, app.getAssetManager());
        
        app.getFlyByCamera().setEnabled(true);
        app.getFlyByCamera().setMoveSpeed(100);
        app.getCamera().setLocation(specPosition);
        app.getCamera().setRotation(new Quaternion().fromAngles(specAngles));
    }
    
    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            if (level.getWater() != null)
                level.getWater().update(tpf);
            if (warzone != null) {
                warzone.update(tpf);
            }
        }
    }
    
    @Override
    protected void remove() {
        
    }
}