package dronewars.archive;

import dronewars.main.Audio;
import dronewars.serializable.Player;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.Camera;
import de.lessvoid.nifty.Nifty;
import dronewars.ui.Crosshair;
import dronewars.ui.HealthWidget;
import dronewars.ui.HeightWidget;
import dronewars.ui.Minimap;
import dronewars.ui.TimeWidget;
import dronewars.input.PlayerController;
import dronewars.archive.PlayerClient;
import dronewars.input.KeyboardController;
import dronewars.ui.CameraWidget;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;
import tonegod.gui.core.Screen;

    
/**
 *
 * @author Jan David Kleiß
 */
public class PlayerMode extends AbstractAppState {
    private final float ROTOR_RANGE = 100;
    private final float ROTOR_DIM = 5;
    
    private SimpleApplication app;
    private PlayerSettings cfg;
    private Audio sound;
    private Game scene;
    
    // model
    private Drone drone;
    private Player me;
    private Player enemy;
    private PlayerClient client;
    
    // widgets
    private Minimap minimap;
    private Crosshair crosshair;
    private CameraWidget posWidget;
    private HealthWidget healthWidget;
    private HeightWidget heightWidget;
    private TimeWidget timeWidget;
    private BatteryWidget batteryWidget;
    
    private static final Logger logger = Logger.getLogger(PlayerMode.class.getName());
    
    public PlayerMode(SimpleApplication app, Nifty nifty) {
        this.app = app;
        cfg = Settings.getPlayerSettings();
        
        addDrone();
        addClient();
        addController();
        
        me = client.getMyStatus();
        enemy = client.getEnemyStatus();

        scene = new Game(app);        
        addWidgets();
        addAudio(app);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
    }
    @Override
    public void update(float tpf) {
        // avoid nullpointer when origin is not yet dertermined by the server
        GPS origin = client.getOrigin();
        if (origin == null) return;
        
        // updates virtual drone and gets sensor data
        drone.update(tpf);
        me.position = drone.getPosition().getVector(origin);
        me.orientation = drone.getOrientation();

        // replaces pattern recognition
        if (isTargeting()) {
            client.setAim(true);
        } else {
            client.setAim(false);
        }
        
        // updates scene and fpv camera
        scene.update(me, enemy);
        scene.setCamera(me.position, me.orientation);
        
        updateWidgets();
        
        playAudioEvents();
        setRotorVolumes();
    }
    
    private String getMinutes(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        return new SimpleDateFormat("mm:ss").format(cal.getTime());
    }
        
    private boolean isTargeting() {
        BoundingVolume bv = scene.getEnemySpatial().getWorldBound();
        int planeState = app.getCamera().getPlaneState();
        app.getCamera().setPlaneState(0);
        Camera.FrustumIntersect result = app.getCamera().contains(bv);
        app.getCamera().setPlaneState(planeState);
        
        if (result == Camera.FrustumIntersect.Inside) {
            return true;
        } else {
            return false;
        }
    }

    private void playAudioEvents() {
        String audio = client.getAudio();
        switch (audio) {
            case "":
                return;
            case "lockoff":
                sound.stop("lockon");
                break;
            default:
                sound.play(audio);
        }
    }
    
    private void setRotorVolumes() {
        sound.setVolume("rotor", drone.getThrottle() / ROTOR_DIM);
        float distance = enemy.position.distance(me.position);
        float volume;
        if (ROTOR_RANGE < distance) {
            volume = 0;
        } else {
            volume = (ROTOR_RANGE - distance) / ROTOR_RANGE;
        }
        sound.setVolume("rotor2", volume / ROTOR_DIM);
    }
 
    private void updateWidgets() {
        minimap.updatePlayer(me.position.x, me.position.z);
        minimap.updateEnemy(enemy.position.x, enemy.position.z);
        posWidget.update(me, enemy);
        healthWidget.setOwnHealth(client.getMyHP());
        healthWidget.setEnemyHealth(client.getEnemyHP());
        heightWidget.setHeightArrow(me.position.y);
        String minutes = getMinutes(client.getTimeLeft());
        timeWidget.updateTime(minutes);
        minutes = getMinutes(client.getDrone().getBattery());
        batteryWidget.updateBattery(minutes);
    }
    
    private void addWidgets() {
        Screen screen = new Screen(app);
        app.getGuiNode().addControl(screen);
        
        float width = app.getContext().getSettings().getWidth();
        float height = app.getContext().getSettings().getHeight();
        
        minimap = new Minimap(screen, width, height);
        crosshair = new Crosshair(screen, width, height);
        posWidget = new CameraWidget(app);
        healthWidget = new HealthWidget(screen);
        heightWidget = new HeightWidget(screen, width, height);
        timeWidget = new TimeWidget(screen, width, height);
        batteryWidget = new BatteryWidget(screen, width, height);
    }

    private void addController() {
        switch (cfg.inputType) {
            case "KEYBOARD":
                KeyboardController kb = new KeyboardController(
                        app.getInputManager(), client);
                break;
            case "RANDOM":
                RandomController ai = new RandomController(client);
                ai.start();
                break;
            case "XBOX":
                PlayerController js = new PlayerController(
                        app.getInputManager(), client);
                break;
        }
    }

    private void addDrone() {
                drone = new Drone(new GPS(cfg.vdLongitude, 
                        cfg.vdLatitude, cfg.vdAltitude), cfg.vdBatteryLife);
            
    }

    private void addClient() {
        client = new PlayerClient(drone);
        Thread th = new Thread(client);
        th.start();
    }

    private void addAudio(SimpleApplication app) {
        sound = new Audio(app);
        sound.play("ambient");
        
        sound.play("rotor");
        sound.setLooping("rotor");
        sound.play("rotor2");
        sound.setLooping("rotor2");
    }
}