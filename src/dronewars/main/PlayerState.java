package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.input.ChaseCamera;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerState extends GameState {
   
    private static final float camElevation = 10; // in degrees
    private static final float camDistance = 10;
    private static final float camSensitivity = 2;
    
    private ChaseCamera chaseCam;
    
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
        warzone.addPlayer();
        initCamera();
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
    
    public WarplaneControl getCombatControl() {
        if (warzone != null && warzone.getPlayer() != null)
            return warzone.getPlayer().getControl();
        return null;
    }
}