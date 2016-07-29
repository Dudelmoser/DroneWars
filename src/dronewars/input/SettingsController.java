/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.input;

import com.jme3.input.controls.ActionListener;
import dronewars.main.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Settings;

/**
 *
 * @author Jan David Kleiß
 */
public class SettingsController extends DefaultController {

    private Settings settings;
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("BACK") && !isPressed)
                nifty.gotoScreen("MainMenu");
        }
    };
    
    public SettingsController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.addListener(actionListener, "BACK");
        settings = JsonFactory.load(Settings.class);
    }
        
    @Override
    public void onEndScreen() {
        JsonFactory.save(settings);
        inputManager.removeListener(actionListener);
    }
}
