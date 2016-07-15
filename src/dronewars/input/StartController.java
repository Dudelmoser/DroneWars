package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;

/**
 *
 * @author Jan David Kleiß
 */
public class StartController extends DefaultController {
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("BACK")) {
                System.exit(0);
            } else {
                nifty.gotoScreen("MainMenu");
            }
        }
    };
    
    public StartController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.addListener(actionListener, "BACK", "MENU", "ACTION_0");
    }
    
    @Override
    public void onEndScreen() {
        inputManager.removeListener(actionListener);
    }
}
