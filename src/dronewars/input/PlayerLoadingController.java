package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import dronewars.main.PlayerState;

/**
 *
 * @author  Jan David Kleiß
 */
public class PlayerLoadingController extends DefaultController {

    public PlayerLoadingController(SimpleApplication app) {
        super(app);
    }

    @Override
    public void onStartScreen() {        
        Thread t = new Thread() {
            @Override
            public void run() {
                AppState state = new PlayerState();
                stateManager.attach(state);
                nifty.gotoScreen("Player");
            }
        };
        t.start();
    }

    @Override
    public void onEndScreen() {
    }
    
}
