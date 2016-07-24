package dronewars.input;

import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.tools.Color;
import dronewars.main.EditorState;
import dronewars.main.StereoApplication;
import dronewars.serializable.Level;
import dronewars.serializable.Precipitation;
import dronewars.serializable.Sky;
import dronewars.serializable.Water;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private String[] mapNames;
    private AssetManager assetManager;
    private Sky sky;
    private NiftyJmeDisplay display;
    
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
    }
    
    private void initSliders() {
        HashMap<String, Float> options = new HashMap();
        options.put("water_height_slider", water.getWaterLevel());
        for (Map.Entry<String, Float> opt : options.entrySet()) {
            setSingleSlider(opt.getKey(), opt.getValue());
        }
        
        setRgbSlider("light_slider_", sky.getSunColor());
    }

    private void initPics() {
        selector = nifty.getCurrentScreen()
            .findNiftyControl("mapSelect", ImageSelect.class);
        mapNames = fillImageSelector(selector, "Maps", "preview.jpg", display);
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
                case "ACTION_2":  // KEY_LSHIFT
                    shift = keyPressed ? true : false;
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

    @NiftyEventSubscriber(pattern = ".*_btn")
    public void onClick(String id, NiftyMousePrimaryClickedEvent event){
        if(id.equals("sky_btn")) setSky();
        if(id.equals("rain_btn")) toggleRain();
    }
    
    @NiftyEventSubscriber(pattern = "water_.*._slider")
    public void onChangeSingleProp(String id, SliderChangedEvent event){
        Slider s = event.getSlider();
        boolean sliderHasFocus = s.hasFocus();
        int val = (int)event.getValue();
        
        if(sliderHasFocus && id.contains("reflection")){
            setWaterReflection(val);
        } else if(sliderHasFocus && id.contains("height")){
            setWaterHeight(val);
        }
    }
    
    @NiftyEventSubscriber(pattern = ".*_slider_.")
    public void onChangeColor(String id, SliderChangedEvent event){
        Slider s = event.getSlider();
        boolean sliderHasFocus = s.hasFocus();
        int val = (int)event.getValue();
        
        if(sliderHasFocus && id.contains("water")){
            setWaterColor(s.getId(), val);
        } else if(sliderHasFocus && id.contains("light")){
            setLightColor(s.getId(), val);
        }
    }
    
    private void setWaterHeight(float h) {
        water.setWaterLevel(h / (float)5.5);
        //state.update(h);
    }
    
    private void setWaterReflection(float h) {
        water.setReflectivity(h / 255);
    }
    
     private void setWaterColor(String id, float v) {
        ArrayList<Object> list = new ArrayList();
        list.add(water);
        setColor(list, v, id);
    }
    
    private void setLightColor(String id, float v){
        ArrayList<Object> list = new ArrayList();
        list.add(sky.getSun());
        list.add(sky.getAmbient());
        setColor(list, v, id);
    }

    private void setColor(ArrayList<Object> oToChange, float v, String id) {
        float val = v/255; // MAP VALUES
        boolean isLight = oToChange.contains(sky.getSun()) || oToChange.contains(sky.getAmbient());
        boolean isWater = oToChange.contains(water);
        ColorRGBA newColor = new ColorRGBA(255, 255, 255, 255); 
                
        if(isLight) newColor = sky.getSun().getColor();
        if(isWater) newColor = water.getColor();

        switch(id.charAt(id.length() - 1)){ //last char [RBG(A)]
            case 'R':
                newColor.r = val;
                break;
            case 'G':
                newColor.g = val;
                break;
            case 'B':
                newColor.b = val;
                break;
        }
        
        if(isLight){
            sky.getSun().setColor(newColor);
            sky.getAmbient().setColor(newColor);
        } 
        if(isWater){
            water.setColor(newColor);
        }
    }

    private void setSky() {
        int currSky = Integer.parseInt(sky.getName());
        int newSky = (currSky == 6) ? 0 : (currSky + 1); // max. 6 skies in assets, starts with 0
        sky.setName(String.valueOf(newSky));
        sky.update(assetManager);
    }

    private void toggleRain() {
        Precipitation rain = level.getPrecipitation();
        if(rain.isEnabled()){
            rain.setEnabled(false);
            System.out.println("RAIN OFF");
        } else {
            rain.setEnabled(true);
            System.out.println("RAIN ON");
        }
    }
    
    public void loadMap(int i) {
//        String name = mapNames[i];
//        state.setRenderedObject(name);
//        selector.setSelectedImageIndex(getNextImageIndex());
    }
    
    @NiftyEventSubscriber(id = "mapSelect")
    public void onChange(final String id, ImageSelectSelectionChangedEvent event) {
        System.out.println("SELECTED IMAGE: " + mapNames[event.getSelectedIndex()]);
        loadMap(event.getSelectedIndex());
    }
    
}