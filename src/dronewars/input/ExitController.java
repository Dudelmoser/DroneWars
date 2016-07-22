package dronewars.input;

import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import dronewars.main.EditorState;
import dronewars.main.PlayerState;
import dronewars.main.SpectatorState;
import dronewars.main.StereoApplication;
import java.util.ArrayList;

/**
 *
 * @author Jan David Kleiß
 */
public class ExitController extends DefaultController {
        
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("BACK"))
                back();
        }
    };
    
    public ExitController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.addListener(keyListener, "1", "2", "3");
        inputManager.addListener(actionListener, "BACK");
    }
    
    @Override
    public void onEndScreen() {
        inputManager.removeListener(actionListener);
        inputManager.removeListener(keyListener);
    }
    
    public void exit() {
        ArrayList<AppState> states = new ArrayList<>();
        states.add(stateManager.getState(PlayerState.class));
        states.add(stateManager.getState(EditorState.class));
        states.add(stateManager.getState(SpectatorState.class));
        
        for (AppState state : states) {
            if (state != null) {
                stateManager.detach(state);
            }
        }
        nifty.gotoScreen("MainMenu");
    }
    
    public void back() {
        AppState pState = stateManager.getState(PlayerState.class);
        AppState eState = stateManager.getState(EditorState.class);
        AppState sState = stateManager.getState(SpectatorState.class);
        
        if (pState != null) {
            nifty.gotoScreen("Player");
        } else if (eState != null) {
            nifty.gotoScreen("Editor");
        } else if (sState != null) {
            nifty.gotoScreen("Spectator");
        }
    }
}
