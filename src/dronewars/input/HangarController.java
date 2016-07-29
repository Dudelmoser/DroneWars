package dronewars.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import dronewars.main.HangarState;
import dronewars.main.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Warplane;

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
        
        setColorSlider("color", plane.getColor());
        setColorSlider("laser", plane.getLaserColor());
        
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
    
    @NiftyEventSubscriber(pattern = ".*")
    public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
        switch(id) {
            case "colorR":
                plane.getColor().r = event.getValue() / 255;
                break;
            case "colorG":
                plane.getColor().g = event.getValue() / 255;
                break;
            case "colorB":
                plane.getColor().b = event.getValue() / 255;
                break;
            case "laserR":
                plane.getLaserColor().r = event.getValue() / 255;
                break;
            case "laserG":
                plane.getLaserColor().g = event.getValue() / 255;
                break;
            case "laserB":
                plane.getLaserColor().b = event.getValue() / 255;
                break;
        }
    }
    
    public void nextPlane() {
        planeSelect.setSelectedImageIndex(getNextImageIndex(planeSelect));
    }
    
    @NiftyEventSubscriber(id = "planeSelect")
    public void onChange(final String id, ImageSelectSelectionChangedEvent event) {
        System.out.println(event.getSelectedIndex());
        String name = planeNames[event.getSelectedIndex()];
        state.setRenderedObject(name);
    }
}
