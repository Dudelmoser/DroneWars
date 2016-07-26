package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import static dronewars.main.GameState.LEVEL_SEND_INTERVAL;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class SpectatorState extends GameState {
    
    private Vector3f specPosition = new Vector3f(-100, 50, 140);
    private float[] specAngles = new float[]{0, 2.56f, 0};
    
    @Override
    public void onInitialize() {}
    
    @Override
    public void onUpdate(float tpf) {
        if (warzone == null) {
            if (queueTime > 0) {
                queueTime -= tpf;
            } else {
                level = JsonFactory.load(Level.class);
                levelJson = new GsonBuilder().create().toJson(level);
            }
            if (levelJson != null)
                startGame();
        } else {
            if (sendTime <= 0) {
                udp.send(levelJson);
                sendTime = LEVEL_SEND_INTERVAL;
            } else {
                sendTime -= tpf;
            }
        }
    }

    @Override
    protected void onCleanup() {}

    @Override
    public void onMessage(String host, int port, String line) {
        if (line.charAt(0) == '{' && levelJson == null) {
            System.out.println(line);
            level = new GsonBuilder().create().fromJson(line, Level.class);
            levelJson = line;
        }
    }
    
    public void startGame() {
        initLevel();
        initCamera();
    }
     
    public void initCamera() {
        app.getFlyByCamera().setEnabled(true);
        app.getFlyByCamera().setMoveSpeed(100);
        app.getCamera().setLocation(specPosition);
        app.getCamera().setRotation(new Quaternion().fromAngles(specAngles));
    }
}