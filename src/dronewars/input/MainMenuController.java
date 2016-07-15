package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;

/**
 *
 * @author Jan David Kleiß
 */
public class MainMenuController extends DefaultController {
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("BACK"))
                nifty.gotoScreen("Start");
        }
    };
    
    public MainMenuController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.addListener(keyListener, "OPTION_1", "OPTION_2", "OPTION_3", "OPTION_4");
        inputManager.addListener(actionListener, "BACK");
    }
    
    @Override
    public void onEndScreen() {
        inputManager.removeListener(actionListener);
        inputManager.removeListener(keyListener);
    }
}
