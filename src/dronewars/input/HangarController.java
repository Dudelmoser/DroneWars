package dronewars.input;

import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import dronewars.main.HangarState;
import dronewars.main.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Airplane;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Jan David Kleiß
 */
public class HangarController extends DefaultController {

    private static final String fileName = "airplane.json";
    private Airplane airplane;
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
    
    public HangarController(StereoApplication app) {
        super(app);
    }

    @Override
    public void onStartScreen() {
        inputManager.addListener(actionListener, "BACK");
        
        airplane = JsonFactory.load(fileName, Airplane.class);
        state = new HangarState(airplane);
        stateManager.attach(state);
        state.setEnabled(true);
        
        initSliders();
    }

    @Override
    public void onEndScreen() {
        JsonFactory.save(fileName, airplane);
        state.setEnabled(false);
        stateManager.detach(state);
    }
    
    @NiftyEventSubscriber(pattern = ".*")
    public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
        switch(id) {
            case "laserR":
                airplane.getLaserColor().r = event.getValue() / 255;
                break;
            case "laserG":
                airplane.getLaserColor().g = event.getValue() / 255;
                break;
            case "laserB":
                airplane.getLaserColor().b = event.getValue() / 255;
                break;
            case "rotorR":
                airplane.getSecondaryColor().r = event.getValue() / 255;
                break;
            case "rotorG":
                airplane.getSecondaryColor().g = event.getValue() / 255;
                break;
            case "rotorB":
                airplane.getSecondaryColor().b = event.getValue() / 255;
                break;
            case "shellR":
                airplane.getPrimaryColor().r = event.getValue() / 255;
                break;
            case "shellG":
                airplane.getPrimaryColor().g = event.getValue() / 255;
                break;
            case "shellB":
                airplane.getPrimaryColor().b = event.getValue() / 255;
                break;
        }
    }

    private void initSliders() {
        HashMap<String, ColorRGBA> parts = new HashMap();
        parts.put("laser", airplane.getLaserColor());
        parts.put("shell", airplane.getPrimaryColor());
        parts.put("rotor", airplane.getSecondaryColor());
        
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
