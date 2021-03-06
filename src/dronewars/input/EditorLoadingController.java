package dronewars.input;

import com.jme3.app.state.AppState;
import dronewars.main.EditorState;
import dronewars.main.StereoApplication;

/**
 *
 * @author  Jan David Kleiß
 */
public class EditorLoadingController extends DefaultController {
    
    public EditorLoadingController(StereoApplication app) {
        super(app);
    }

    @Override
    public void onStartScreen() {
        Thread t = new Thread() {
            @Override
            public void run() {
                AppState state = new EditorState();
                stateManager.attach(state);
                nifty.gotoScreen("Editor");
            }
        };
        t.start();
    }

    @Override
    public void onEndScreen() {
    }
}
