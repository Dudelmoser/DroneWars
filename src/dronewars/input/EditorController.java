package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import dronewars.main.EditorState;
import dronewars.serializable.Level;
import dronewars.serializable.Precipitation;
import dronewars.serializable.Sky;
import dronewars.serializable.Water;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorController extends DefaultController {
    private Level level;
    private EditorState state;
    private Water water; 
    private Precipitation precipitation;
    private boolean shift = false;
    private ImageSelect mapSelect;
    private AssetManager assetManager;
    private Sky sky;
        
    public EditorController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        //screen = nifty.getCurrentScreen();
        initFields();
        initSliders();
        addInputListeners();
        inputManager.setCursorVisible(true);
    }
    
    @Override
    public void onEndScreen() {
        state.setEnabled(false);
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
        inputManager.setCursorVisible(false);
    }

   private void initFields() {
        if (state == null){
            state = stateManager.getState(EditorState.class);
            state.setEnabled(true);
        }
        mapSelect = screen.findNiftyControl("mapSelect", ImageSelect.class);
        level = state.getLevel();
        water = level.getWater();
        precipitation = level.getPrecipitation();
        sky = level.getSky();
        assetManager = level.getApp().getAssetManager();
        
        // LOCAL FILE SYSTEM
//        assetManager.registerLoader(AWTLoader.class, "png");
//        assetManager.registerLocator("/", FileLocator.class);
//        Image(new ImageBuilder() {{
//            filename("/home/marek/photo2.jpg");
//        }});
    }
    
    private void initSliders() {
        HashMap<String, Float> options = new HashMap();
        options.put("water_height_slider", water.getWaterLevel());
        for (Map.Entry<String, Float> opt : options.entrySet()) {
            setSingleSlider(opt.getKey(), opt.getValue());
        }
        
        setRgbSlider("light_slider_", sky.getSunColor());
    }
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {}
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            //System.out.println(this.getClass().toString() + ": key pressed. [" + name + " ]");
            switch(name) {
                case "BACK": // KEY_ESCAPE
                    nifty.gotoScreen("Exit");
                    break;
                case "ACTION_2":  // KEY_LSHIFT
                    shift = keyPressed ? true : false;
                    break;
                case "OPTION_1":
                    setWaterColor();
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

    @NiftyEventSubscriber(id = "water_color_btn")
    public void onClick(String id, NiftyMousePrimaryClickedEvent event){
        setWaterColor();
    }
    
    @NiftyEventSubscriber(id = "water_height_slider")
    public void onChangeWater(String id, SliderChangedEvent event){
        Slider s = event.getSlider();
        boolean sliderHasFocus = s.hasFocus();
        int val = (int)event.getValue();
        
        if(sliderHasFocus){
            setWaterHeight(val);
        }
    }
    
    @NiftyEventSubscriber(pattern = "light_slider_.")
    public void onChangeLight(String id, SliderChangedEvent event){
        Slider s = event.getSlider();
        boolean sliderHasFocus = s.hasFocus();
        int val = (int)event.getValue();
        
        if(sliderHasFocus){
            setLightColor(s.getId(), val);
        }
    }
    
     private void setWaterColor() {
        water.getWaterFilter().setWaterColor(ColorRGBA.randomColor());
    }
    
    private void setWaterHeight(float h) {
        water.setWaterLevel(h / (float)5.5);
        state.update(h);
    }
    
    private void setLightColor(String id, float v){
        float val = v/255; // MAP VALUES
        DirectionalLight sun = sky.getSun();
        ColorRGBA sunColor = sun.getColor();
        switch(id.charAt(id.length() - 1)){ //last char [RBG(A)]
            case 'R':
                sunColor.r = val;
                break;
            case 'G':
                sunColor.g = val;
                break;
            case 'B':
                sunColor.b = val;
                break;
        }
        sun.setColor(sunColor);
    }
}