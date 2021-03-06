package dronewars.input;

import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import dronewars.main.SpectatorState;
import dronewars.main.StereoApplication;

/**
 *
 * @author Jan David Kleiß
 */
public class SpectatorController extends DefaultController {
    private SpectatorState state;
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            
        }
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            switch(name) {
                case "BACK":
                    nifty.gotoScreen("Exit");
            }
        }
    };
    
    public SpectatorController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.setCursorVisible(false);
        state = stateManager.getState(SpectatorState.class);
        state.setEnabled(true);
        app.getFlyByCamera().setDragToRotate(false);
        
        inputManager.addListener(actionListener, "BACK");
        inputManager.addListener(analogListener);
    }
    
    @Override
    public void onEndScreen() {
        app.getFlyByCamera().setDragToRotate(true);
        inputManager.setCursorVisible(true);
        state.setEnabled(false);
        
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
    }
}