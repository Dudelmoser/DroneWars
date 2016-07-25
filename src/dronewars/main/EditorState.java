package dronewars.main;

import com.jme3.input.FlyByCamera;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorState extends LevelState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    private float rotationSpeed = 1;
    private float moveSpeed = 100;
    private float zoomSpeed = 30;
    
    private FlyByCamera flyCam;
    
    @Override
    protected void init() {
        flyCam = app.getFlyByCamera();
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        flyCam.setZoomSpeed(zoomSpeed);
        flyCam.setMoveSpeed(moveSpeed);
        flyCam.setRotationSpeed(rotationSpeed);
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
        flyCam.setEnabled(false);
    }
    
    
    public void setRenderedObject(String name) {
        // setName, remove, createStatic 
    }
    
    public Level getRenderedObject() {
        return level;
    }
}