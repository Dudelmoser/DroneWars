package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class SpectatorState extends GameState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    
    private boolean failed;
    
    @Override
    protected void onInitialize() {
        app.getFlyByCamera().setEnabled(true);
        app.getFlyByCamera().setMoveSpeed(100);
        app.getCamera().setLocation(specPosition);
        app.getCamera().setRotation(new Quaternion().fromAngles(specAngles));
    }

    @Override
    protected void onUpdate(float tpf) {
        if (warzone == null) {
            queueTime -= tpf;
            if (queueTime < 0) {
                failed = true;
            }
            if (levelJson != null) {
                level = new GsonBuilder().create().fromJson(levelJson, Level.class);
                warzone = new Warzone(app.getRootNode(), app.getTimer(), bullet,
                    level, app.getAssetManager());
            }
        }
    }

    @Override
    protected void onCleanup() {}

    @Override
    public void onMessage(String host, int port, String line) {
        if (line.charAt(0) == '{') {
            levelJson = line;
        }
    }
    
    public boolean hasFailed() {
        return failed;
    }
}