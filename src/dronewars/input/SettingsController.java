/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.input;

import com.jme3.input.controls.ActionListener;
import de.lessvoid.nifty.controls.CheckBox;
import dronewars.io.JsonFactory;
import dronewars.main.StereoApplication;
import dronewars.serializable.Settings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class SettingsController extends DefaultController {

    private Settings settings;
    private String[] checkboxes = {"fullscreen", "fxaa", "lod", "dof", "bloom", "sunrays"};
    
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
        for (String id : checkboxes) {
            try {
                CheckBox cb = nifty.getCurrentScreen().findNiftyControl(id, CheckBox.class);
                cb.setChecked((boolean)settings.getClass().getField(id).get(settings));
            } catch (NoSuchFieldException | SecurityException | 
                    IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void onEndScreen() {        
        for (String id : checkboxes) {
            try {
                settings.getClass().getField(id).set(settings, isChecked(id));
            } catch (NoSuchFieldException | SecurityException | 
                    IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JsonFactory.save(settings);
        inputManager.removeListener(actionListener);
    }
    
    private boolean isChecked(String id) {
        CheckBox cb = nifty.getCurrentScreen().findNiftyControl(id, CheckBox.class);
        return cb.isChecked();
    }
}
