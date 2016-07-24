package dronewars.input;

import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.ImageSelectSelectionChangedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import static dronewars.input.DefaultController.logger;
import dronewars.main.EditorState;
import dronewars.main.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Level;
import dronewars.serializable.Sky;
import dronewars.serializable.Terrain;
import dronewars.serializable.Water;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private Label mapPresetName;
    private TextField levelName;
    private DropDown levelSelect;
    private DropDown skySelect;
    private EditorState state;
    private NiftyJmeDisplay display;
    private AssetManager assetManager;
    private Screen screen;
    
    public EditorController(StereoApplication app, NiftyJmeDisplay display) {
        super(app);
        this.display = display;
    }
    
    @Override
    public void onStartScreen() {
        initFields();
        initSliders();
        initMapPreset();
        initLevelAndSkyControls();
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
        level = state.getLevel();
        assetManager = level.getApp().getAssetManager();
        screen = nifty.getCurrentScreen();
    }
    
    private void initSliders() {
        HashMap<String, Object> options = new HashMap();
        
        Water water = level.getWater();
        Sky sky = level.getSky();
        
        options.put("Water_Level_Slider", water.getLevel());
        options.put("Water_LevelVariance_Slider",water.getLevelVariance());
        options.put("Water_WaveAmplitude_Slider", water.getWaveAmplitude());
        
        options.put("Water_WaterColor_", water.getWaterColor());
        options.put("Sky_SunColor_", sky.getSunColor());
        options.put("Sky_AmbientColor_", sky.getAmbientColor());
        
        for (Map.Entry<String, Object> opt : options.entrySet()) {
            if(opt.getKey().contains("Color")){
                setColorSlider(opt.getKey(), (ColorRGBA) opt.getValue());
            } else {
                setFloatSlider(opt.getKey(), (Float) opt.getValue());
            }
        }
    }

    private void initMapPreset() {
        mapSelect = screen.findNiftyControl("mapSelect", ImageSelect.class);
        mapNames = fillImageSelector(mapSelect, "Maps", "height.png", display);
        int i = java.util.Arrays.asList(mapNames).indexOf(state.getLevel().getTerrain().getName());
        mapSelect.setSelectedImageIndex(i);
        
        mapPresetName = screen.findNiftyControl("mapPresetName", Label.class);
        mapPresetName.setText(mapNames[mapSelect.getSelectedImageIndex()]);
    }

    private void initLevelAndSkyControls() {
        levelName =  screen.findNiftyControl("Level_Name", TextField.class);
        levelSelect =  screen.findNiftyControl("Level_Select", DropDown.class);
        updateLevelSelect();
        
        skySelect = screen.findNiftyControl("Sky_Select", DropDown.class);
        updateSkySelect();
        int i = skySelect.getItems().indexOf(state.getLevel().getSky().getName());
        skySelect.selectItemByIndex(i);
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
            case "Rain":
                level.getPrecipitation().toggle();
                break;
            case "Terrain":
                float y = screen.findNiftyControl("Terrain_Scale_Y", Slider.class).getValue();
                float xz = screen.findNiftyControl("Terrain_Scale_XZ", Slider.class).getValue();
                level.getTerrain().setScaleVector(new Vector3f(xz, y, xz));
            case "Level_Save":
                save();
                break;
        }

    }
    
    @NiftyEventSubscriber(pattern = ".*_Slider")
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

    private void setSky(String skyName) {
        level.getSky().setName(skyName);
        level.getSky().update(assetManager);
    }
    
    @NiftyEventSubscriber(id = "mapSelect")
    public void onChange(final String id, ImageSelectSelectionChangedEvent event) {
        mapPresetName.setText(mapNames[event.getSelectedIndex()]);
        loadHighmap(mapNames[event.getSelectedIndex()]);
    }

    private void loadHighmap(String mapName) {
        Terrain terrain = state.getLevel().getTerrain();
        terrain.setName(mapName);
        terrain.reload();
        state.update(1);
        
    }
    
    @NiftyEventSubscriber(pattern = ".*_Select")
    public void onSelect(String id, DropDownSelectionChangedEvent event){
        if(id.contains("Level")){
            levelName.setText((CharSequence) ((String) event.getSelection()).replace(".json", ""));
            loadLevel((String) event.getSelection());
        } else if(id.contains("Sky")){
            setSky((String) event.getSelection());
        }
    }
    
    public void loadLevel(String filename){
        
    }
    
    public void save() {
        String input = levelName.getText();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        
        if(input.equals("") || input == null) input = timeStamp + ".json";
        
        String fileName = input.endsWith(".json") ? input : input + ".json";
        
        JsonFactory.save("assets/Levels/" + fileName, level);
        updateLevelSelect();
    }

    private ArrayList<String> getSavedLevels() {
        String path = Paths.get(System.getProperty("user.dir")).toString() + "/assets/Levels";
        ArrayList<String> levels;
        levels = new ArrayList(Arrays.asList(new File(path).list()));
        return levels;
    }

    private String[] getSavedSkies() {
        String path = Paths.get(System.getProperty("user.dir")).toString() + "/assets/Skies";
        String[] skies = new File(path).list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return skies;
    }

    private void updateLevelSelect() {
        ArrayList<String> levels = getSavedLevels();
        if(levelSelect.getItems().size() != 0){
            levelSelect.clear();
        }
        levelSelect.addAllItems(levels);
    }

    private void updateSkySelect() {
        String[] skies = getSavedSkies();
        if(!skySelect.getItems().isEmpty()){
            skySelect.clear();
        }
        skySelect.addAllItems(new ArrayList(Arrays.asList(skies)));
    }
}