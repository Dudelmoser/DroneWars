package dronewars.input;

import com.google.gson.GsonBuilder;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import dronewars.main.DroneState;
import dronewars.serializable.Drone;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Jan David Kleiß
 */
public class DroneController extends DefaultController {

    private Drone drone;
    private AppState state;
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                switch (name) {
                    case "BACK":
                        nifty.gotoScreen("MainMenu");
                }
            }
        }
    };
    
    public DroneController(SimpleApplication app) {
        super(app);
    }

    @Override
    public void onStartScreen() {
        inputManager.addListener(actionListener, "BACK");
        
        Path path = Paths.get(System.getProperty("user.dir") + "/drone.json");
        try {
            String json = new String(Files.readAllBytes(path));
            drone = new GsonBuilder().create().fromJson(json, Drone.class);
        } catch (IOException ex) {
            drone = new Drone();
        }
        state = new DroneState(drone);
        stateManager.attach(state);
        state.setEnabled(true);
        
        initSliders();
    }

    @Override
    public void onEndScreen() {
        String path = System.getProperty("user.dir") + "/drone.json";
        String json = new GsonBuilder().create().toJson(drone);
        
        try (PrintWriter out = new PrintWriter(path)) {
            out.println(json);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        state.setEnabled(false);
        stateManager.detach(state);
    }
    
    @NiftyEventSubscriber(pattern = ".*")
    public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
        switch(id) {
            case "laserR":
                drone.getLaserColor().r = event.getValue() / 255;
                break;
            case "laserG":
                drone.getLaserColor().g = event.getValue() / 255;
                break;
            case "laserB":
                drone.getLaserColor().b = event.getValue() / 255;
                break;
            case "rotorR":
                drone.getRotorColor().r = event.getValue() / 255;
                break;
            case "rotorG":
                drone.getRotorColor().g = event.getValue() / 255;
                break;
            case "rotorB":
                drone.getRotorColor().b = event.getValue() / 255;
                break;
            case "shellR":
                drone.getShellColor().r = event.getValue() / 255;
                break;
            case "shellG":
                drone.getShellColor().g = event.getValue() / 255;
                break;
            case "shellB":
                drone.getShellColor().b = event.getValue() / 255;
                break;
        }
    }

    private void initSliders() {
        HashMap<String, ColorRGBA> parts = new HashMap();
        parts.put("laser", drone.getLaserColor());
        parts.put("shell", drone.getShellColor());
        parts.put("rotor", drone.getRotorColor());
        
        for (Entry<String, ColorRGBA> part : parts.entrySet()) {
            setRgbSlider(part.getKey(), part.getValue());
        }
    }
    
    private void setRgbSlider(String id, ColorRGBA color) {
        Screen screen = nifty.getCurrentScreen();
        
        Slider r = screen.findNiftyControl(id + "R", Slider.class);
        r.setValue(color.r * 255);
        Slider g = screen.findNiftyControl(id + "G", Slider.class);
        g.setValue(color.g * 255);
        Slider b = screen.findNiftyControl(id + "B", Slider.class);
        b.setValue(color.b * 255);
    }
}
