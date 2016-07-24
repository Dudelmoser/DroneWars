package dronewars.input;

import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import static dronewars.input.DefaultController.logger;
import dronewars.main.EditorState;
import dronewars.main.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Level;
import dronewars.serializable.Precipitation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorController extends DefaultController {
    private Level level;
    private String[] mapNames;
    private ImageSelect mapSelect;
    private EditorState state;
    private NiftyJmeDisplay display;
    private AssetManager assetManager;
    
    public EditorController(StereoApplication app, NiftyJmeDisplay display) {
        super(app);
        this.display = display;
    }
    
    @Override
    public void onStartScreen() {
        initFields();
        initSliders();
        initPics();
        addInputListeners();
        inputManager.setCursorVisible(true);
    }
    
    @Override
    public void onEndScreen() {
        state.setEnabled(false);
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
        JsonFactory.save(level);
    }

   private void initFields() {
        if (state == null){
            state = stateManager.getState(EditorState.class);
            state.setEnabled(true);
        }
        mapSelect = screen.findNiftyControl("mapSelect", ImageSelect.class);
        level = state.getLevel();
        assetManager = level.getApp().getAssetManager();
    }
    
    private void initSliders() {
        HashMap<String, Float> options = new HashMap();
        options.put("water_height_slider", level.getWater().getLevel());
        for (Map.Entry<String, Float> opt : options.entrySet()) {
            setFloatSlider(opt.getKey(), opt.getValue());
        }
        
        setColorSlider("light_slider_", level.getSky().getSunColor());
    }

    private void initPics() {
        mapSelect = nifty.getCurrentScreen()
            .findNiftyControl("mapSelect", ImageSelect.class);
        mapNames = fillImageSelector(mapSelect, "Maps", "preview.jpg", display);
    }
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {}
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            switch(name) {
                case "BACK": // KEY_ESCAPE
                    nifty.gotoScreen("Exit");
                    break;
                case "OPTION_1":
                    break;
                case "OPTION_2":
                    break;
                case "OPTION_3":
                    break;
                case "OPTION_4":
                    break;
                case "R_UP":
                    break;
                case "R_DOWN":
                    break;
            }
        }
    };
    
    private void addInputListeners() {
        inputManager.addListener(actionListener,
                "BACK",
                "ACTION_2", //SHIFT
                "OPTION_1", // 1
                "OPTION_2", // 2
                "OPTION_3", // 3
                "OPTION_4", // 4
                "R_UP",
                "R_LEFT",
                "R_RIGHT",
                "R_DOWN"
        );
        // inputManager.addListener(analogListener, "...");
    }

    @NiftyEventSubscriber(pattern = ".*_Button")
    public void onClick(String id, NiftyMousePrimaryClickedEvent event){
        String name = id.replace("_Button", "");
        switch(name) {
            case "Sky":
                break;
            case "Rain":
                level.getPrecipitation().toggle();
        }
    }
    
    @NiftyEventSubscriber(pattern = ".*")
    public void onSliderChanged(String id, SliderChangedEvent event) {
        String[] parts = id.split("_");
        try {
            Method objGetter = level.getClass().getMethod("get" + parts[0]);
            Object obj = objGetter.invoke(level);
            if (id.contains("Color")) {
                Method rgbGetter = obj.getClass().getMethod("get" + parts[1]);
                ColorRGBA color = ((ColorRGBA) rgbGetter.invoke(obj)).clone();
                color.getClass().getDeclaredField(parts[2].toLowerCase()).setFloat(color, event.getValue() / 255f);
                Method rgbSetter = obj.getClass().getMethod("set" + parts[1], ColorRGBA.class);
                rgbSetter.invoke(obj, color);
            } else if (id.contains("Vector") || id.contains("Direction")) {
                Method rgbGetter = obj.getClass().getMethod("get" + parts[1]);
                Vector3f vec = ((Vector3f) rgbGetter.invoke(obj)).clone();
                vec.getClass().getDeclaredField(parts[2].toLowerCase()).setFloat(vec, event.getValue());
                Method rgbSetter = obj.getClass().getMethod("set" + parts[1], Vector3f.class);
                rgbSetter.invoke(obj, vec);
            } else {
                Method setter = obj.getClass().getMethod("set" + parts[1], float.class);
                setter.invoke(obj, event.getValue());
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Slider reflection exception!", ex);
        }
    }

    private void setSky() {
        int currSky = Integer.parseInt(level.getSky().getName());
        int newSky = (currSky == 6) ? 0 : (currSky + 1);
        level.getSky().setName(String.valueOf(newSky));
        level.getSky().update(assetManager);
    }
    
    public void nextMap() {
//        String name = mapNames[i];
//        state.setRenderedObject(name);
//        mapSelect.setSelectedImageIndex(getNextImageIndex());
    }
    
    @NiftyEventSubscriber(id = "mapSelect")
    public void onChange(final String id, ImageSelectSelectionChangedEvent event) {
//        nextMap(event.getSelectedIndex());
    }
    
    public void save() {
        JsonFactory.save("Levels/", this);
    }
}