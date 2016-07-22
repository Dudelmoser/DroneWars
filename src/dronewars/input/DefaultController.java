package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Jan David Kleiß
 */
public class DefaultController implements ScreenController {

    protected Screen screen;
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
    
    public DefaultController(SimpleApplication app) {
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
    }
    
    public void to(String screenId) {
        nifty.gotoScreen(screenId);
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = nifty.getCurrentScreen();
    }
    
    @Override
    public void onStartScreen() {
        screen = nifty.getCurrentScreen();
    }
    
    @Override
    public void onEndScreen() {}
    
    protected void setRgbSlider(String id, ColorRGBA color) {        
        Slider r = screen.findNiftyControl(id + "R", Slider.class);
        r.setValue(color.r * 255);
        Slider g = screen.findNiftyControl(id + "G", Slider.class);
        g.setValue(color.g * 255);
        Slider b = screen.findNiftyControl(id + "B", Slider.class);
        b.setValue(color.b * 255);
    }
    
    protected void setSingleSlider(String id, float v){
        Slider slider = screen.findNiftyControl(id, Slider.class);
        slider.setValue(v);
    }
}
