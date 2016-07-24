package dronewars.input;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.niftygui.RenderImageJme;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ImageSelect;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import dronewars.main.StereoApplication;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;

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

    protected String[] fillImageSelector(ImageSelect selector, String folder, 
            String image, NiftyJmeDisplay display) {
        // define path to maps directory and list all sub directories (maps)
        String basePath = Paths.get(System.getProperty("user.dir")).toString() + "/assets/";
        String[] dirs = new File(basePath + folder).list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        
        for (String dirName : dirs) {
            String imgPath;
            String relPath = folder + "/" + dirName + "/" + image;
            File f = new File(basePath + relPath);
            if(f.exists() && !f.isDirectory()) {
                imgPath = relPath;
            } else {
                imgPath = folder + "/" + image;
            }
            RenderImageJme rImg = new RenderImageJme(imgPath, true, display);
            NiftyImage nImg = new NiftyImage(nifty.getRenderEngine(), rImg);
            selector.addImage(nImg);
        }
        return dirs;
    }
}
