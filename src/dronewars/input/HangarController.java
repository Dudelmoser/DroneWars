package dronewars.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import static dronewars.input.DefaultController.logger;
import dronewars.main.HangarState;
import dronewars.io.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Warplane;
import java.lang.reflect.Method;

/**
 *
 * @author Jan David Kleiß
 */
public class HangarController extends DefaultController {    
    private String[] planeNames;
    private Warplane plane;
    private HangarState state;
    private NiftyJmeDisplay display;
    private ImageSelect planeSelect;
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                switch (name) {
                    case "BACK":
                        nifty.gotoScreen("MainMenu");
                        break;
                    case "START":
                        System.exit(0);
                        break;
                }
            }
        }
    };
    
    public HangarController(StereoApplication app, NiftyJmeDisplay display) {
        super(app);
        this.display = display;
    }

    @Override
    public void onStartScreen() {
        inputManager.addListener(actionListener, "BACK", "START");
        plane = JsonFactory.load(Warplane.class);
        
        state = new HangarState(plane);
        stateManager.attach(state);
        
        setColorSlider("Color_", plane.getColor());
        setColorSlider("LaserColor_", plane.getLaserColor());
        
        planeSelect = nifty.getCurrentScreen().findNiftyControl("planeSelect", ImageSelect.class);
        planeNames = fillImageSelector(planeSelect, 
                plane.getClass().getSimpleName() + "s", "preview.jpg", display);
        planeSelect.setSelectedImageIndex(Integer.parseInt(plane.getName()));
    }

    @Override
    public void onEndScreen() {
        JsonFactory.save(plane);
        stateManager.detach(state);
    }
    
    @NiftyEventSubscriber(pattern = ".*_Slider")
    public void onSliderChanged(String id, SliderChangedEvent event) {
        String[] parts = id.split("_");
        try {
            if(event.getSlider().hasFocus()){
                Method getter = plane.getClass().getMethod("get" + parts[0]);
                ColorRGBA color = (ColorRGBA) getter.invoke(plane);
                color.getClass().getDeclaredField(parts[1].toLowerCase()).setFloat(color, event.getValue() / 255f);
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Slider reflection exception!", ex);
        }
    }
    
    public void nextPlane() {
        planeSelect.setSelectedImageIndex(getNextImageIndex(planeSelect));
    }
    
    @NiftyEventSubscriber(id = "planeSelect")
    public void onChange(final String id, ImageSelectSelectionChangedEvent event) {
        String name = planeNames[event.getSelectedIndex()];
        state.setRenderedObject(name);
    }
}
