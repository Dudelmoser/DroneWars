package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.water.WaterFilter;
import dronewars.main.EditorState;
import dronewars.serializable.Level;

/**
 *
 * @author Jan David Kleiß
 */
public class EditorController extends DefaultController {
    private Level level;
    private EditorState state;
    private boolean shift = false;
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("terrainHeight")) {
                TerrainQuad terrain = level.getTerrain().getTerrainQuad();
                float height = terrain.getLocalScale().y;
                float newHeight = height + (shift ? -0.01f : 0.01f);
                terrain.setLocalScale(1, (newHeight > 1 ? 1 : newHeight), 1);
            } else if (name.equals("waveHeight")) {
                WaterFilter water = level.getWater().getWaterFilter();
                water.setMaxAmplitude(water.getMaxAmplitude() + (shift ? -0.1f : 0.1f));
            }
        }
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            switch(name) {
                case "BACK":
                    nifty.gotoScreen("Exit");
            }
//            if (name.equals("shift")) {
//                shift = keyPressed ? true : false;
//            } else if (name.equals("waterColor") && !keyPressed) {
//                WaterFilter water = scene.getWater().getWaterFilter();
//                water.setWaterColor(ColorRGBA.randomColor());
//            } else if (name.equals("skyBox") && !keyPressed) {
//                Sky sky = scene.getSky();
//                String skyName = scene.getSky().getName();
//                int id = Integer.parseInt(skyName) + 1;
//                skyName = id > 6 ? "0" : String.valueOf(id);
//                sky.setName(skyName);
//                ColorRGBA skyColor = ImageFactory.getSkyColor(skyName);
//                sky.setFogColor(skyColor);
//                scene.getHorizon().setFogColor(skyColor.mult(new ColorRGBA(1, 1, 1, 0.5f)));
//                scene.getSky().update(scene.getAssetManager());
//            } else if (name.equals("freeCam") && !keyPressed) {
//                scene.setCameraMode(0);
//            } else if (name.equals("trailCam") && !keyPressed) {
//                scene.setCameraMode(2);
//            } else if (name.equals("hover") && !keyPressed) {
//                if (shift) {
//                    scene.getDrone().setMass(1);
//                } else {
//                    scene.getDrone().setMass(0);
//                }
//            }
        }
    };
    
    public EditorController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        if (state == null)
            state = stateManager.getState(EditorState.class);
        state.setEnabled(true);
        
        inputManager.addListener(actionListener, "BACK");
        inputManager.addListener(analogListener, "OPTION_1");
    }
    
    @Override
    public void onEndScreen() {
        state.setEnabled(false);
        
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
    }
}