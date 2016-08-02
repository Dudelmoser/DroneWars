package dronewars.main;

import dronewars.io.JsonFactory;
import com.jme3.input.FlyByCamera;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorState extends GameState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    private float rotationSpeed = 1;
    private float moveSpeed = 100;
    private float zoomSpeed = 30;
    
    private FlyByCamera flyCam;
    
    @Override
    protected void onInitialize() {
        level = JsonFactory.load(Level.class);
        level.create(app, null);
        
        applySettings();
        initCamera();
    }    
    
    private void initCamera() {
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
    public void onUpdate(float tpf) {}
    
    @Override
    protected void onCleanup() {
        flyCam.setEnabled(false);
    }

    @Override
    public void onMessage(String host, int port, String line) {}

    public void setWarzone(Warzone warzone) {
        this.warzone = warzone;
    }
}