package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import dronewars.main.SpectatorState;

/**
 *
 * @author  Jan David Kleiß
 */
public class SpectatorLoadingController extends DefaultController {

    public SpectatorLoadingController(SimpleApplication app) {
        super(app);
    }

    @Override
    public void onStartScreen() {        
        Thread t = new Thread() {
            @Override
            public void run() {
                AppState state = new SpectatorState();
                stateManager.attach(state);
                nifty.gotoScreen("Spectator");
            }
        };
        t.start();
    }

    @Override
    public void onEndScreen() {
    }
    
}
