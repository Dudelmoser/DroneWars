package dronewars.input;

import com.jme3.input.controls.ActionListener;
import dronewars.main.StereoApplication;

/**
 *
 * @author Jan David Kleiß
 */
public class ModesController extends DefaultController {
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("BACK"))
                nifty.gotoScreen("MainMenu");
        }
    };
    
    public ModesController(StereoApplication app) {
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
}
