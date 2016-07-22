package dronewars.input;

import com.jme3.app.state.AppState;
import dronewars.main.PlayerState;
import dronewars.main.StereoApplication;

/**
 *
 * @author  Jan David Kleiß
 */
public class PlayerLoadingController extends DefaultController {

    public PlayerLoadingController(StereoApplication app) {
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
