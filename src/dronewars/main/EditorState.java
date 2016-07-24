package dronewars.main;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Level;
import dronewars.serializable.Warplane;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorState extends LevelState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    
    @Override
    protected void init() {
        app.getFlyByCamera().setEnabled(false);
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
    protected void remove() {
        
    }
    
    
    public void setRenderedObject(String name) {
        // setName, remove, createStatic 
    }
    
    public Level getRenderedObject() {
        return level;
    }
    
}