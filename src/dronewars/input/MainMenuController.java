package dronewars.input;

import com.jme3.input.controls.ActionListener;
import dronewars.main.StereoApplication;

/**
 *
 * @author Jan David Kleiß
 */
public class MainMenuController extends DefaultController {
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed)
                return;
            switch(name) {
                case "BACK":
                    nifty.gotoScreen("Start");
                    break;
                case "START":
                    nifty.gotoScreen("PlayerLoading");
            }
        }
    };
    
    public MainMenuController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.addListener(keyListener, "OPTION_1", "OPTION_2", "OPTION_3", "OPTION_4");
        inputManager.addListener(actionListener, "BACK", "START");
    }
    
    @Override
    public void onEndScreen() {
        inputManager.removeListener(actionListener);
        inputManager.removeListener(keyListener);
    }
}
