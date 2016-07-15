package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import dronewars.main.SpectatorState;

/**
 *
 * @author Jan David Kleiß
 */
public class SpectatorController extends DefaultController {
    private AppState state;
    
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
    
    public SpectatorController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        if (state == null)
            state = stateManager.getState(SpectatorState.class);
        state.setEnabled(true);
        
        inputManager.addListener(actionListener, "BACK");
        inputManager.addListener(analogListener);
    }
    
    @Override
    public void onEndScreen() {
        state.setEnabled(false);
        
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
    }
}