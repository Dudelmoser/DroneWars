package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerState extends GameState {
   
    private static final float camElevation = 10; // in degrees
    private static final float camDistance = 10;
    private static final float camSensitivity = 2;
    
    private static boolean trail = false;
    
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
        
        if (warzone != null && warzone.getPlayer() != null) {
            if (!trail) {
                Spatial player = warzone.getPlayer().getSpatial();            
                Vector3f upwards = player.getLocalRotation().mult(Vector3f.UNIT_Y).normalize().mult(2);
                Vector3f backwards = player.getLocalRotation().mult(Vector3f.UNIT_Z).normalize().mult(10);
                app.getCamera().setLocation(player.getLocalTranslation().add(backwards).add(upwards));
                app.getCamera().lookAt(player.getLocalTranslation(), upwards);
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
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setTrailingSensitivity(camSensitivity);
        
        chaseCam.setEnabled(trail);
    }
    
    public WarplaneControl getCombatControl() {
        if (warzone != null && warzone.getPlayer() != null)
            return warzone.getPlayer().getControl();
        return null;
    }
    
    public void toggleTrail() {
        trail = !trail;
        chaseCam.setEnabled(trail);
    }
}