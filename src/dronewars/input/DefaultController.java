package dronewars.input;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import dronewars.main.StereoApplication;

/**
 *
 * @author Jan David Kleiß
 */
public class DefaultController implements ScreenController {

    protected Nifty nifty;
    protected InputManager inputManager;
    protected AppStateManager stateManager;
        
    protected ActionListener keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (!keyPressed && name.startsWith("OPTION_")) {
               Element el = nifty.getCurrentScreen().findElementByName(
                       name.charAt(name.length() - 1) + "");
               el.onClick();
            }
        }
    };
    
    public DefaultController(StereoApplication app) {
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
    }
    
    public void to(String screenId) {
        nifty.gotoScreen(screenId);
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }
    
    @Override
    public void onStartScreen() {}
    
    @Override
    public void onEndScreen() {}
}
