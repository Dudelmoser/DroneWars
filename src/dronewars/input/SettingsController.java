/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.input;

import com.jme3.app.state.AppState;
import com.jme3.input.controls.ActionListener;
import dronewars.main.EditorState;
import dronewars.main.JsonFactory;
import dronewars.main.LevelState;
import dronewars.main.PlayerState;
import dronewars.main.SpectatorState;
import dronewars.main.StereoApplication;
import dronewars.serializable.Settings;

/**
 *
 * @author Jan David Kleiß
 */
public class SettingsController extends DefaultController {

    private static final String fileName = "settings.json";
    private Settings settings;
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                switch (name) {
                    case "BACK":
                        nifty.gotoScreen("MainMenu");
                }
            }
        }
    };
    
    public SettingsController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        Class[] stateClasses = {PlayerState.class, EditorState.class, SpectatorState.class};
        for (Class c : stateClasses) {
            AppState state = stateManager.getState(c);
            if (state != null) {
                settings = ((LevelState) state).getSettings();
                break;
            }
        }
        if (settings == null) {
            settings = JsonFactory.load(fileName, Settings.class);
        }
    }
        
    @Override
    public void onEndScreen() {
        JsonFactory.save(fileName, settings);
    }
}
