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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class DefaultController implements ScreenController {

    protected Screen screen;
    protected Nifty nifty;
    protected InputManager inputManager;
    protected AppStateManager stateManager;
    
    protected static final Logger logger = Logger.getLogger("DefaultController");
    
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
    
    protected void setColorSlider(String id, ColorRGBA color) {        
        Slider r = screen.findNiftyControl(id + "R_Slider", Slider.class);
        r.setValue(color.r * 255);
        Slider g = screen.findNiftyControl(id + "G_Slider", Slider.class);
        g.setValue(color.g * 255);
        Slider b = screen.findNiftyControl(id + "B_Slider", Slider.class);
        b.setValue(color.b * 255);
        try {
            Slider a = screen.findNiftyControl(id + "A_Slider", Slider.class);
            b.setValue(color.b * 255);
        } catch (Exception e) {
            logger.log(Level.INFO, "{0} has no alpha channel!", id);
        }
    }
    
    protected void setFloatSlider(String id, float value){
        Slider slider = screen.findNiftyControl(id, Slider.class);
        slider.setValue(value);
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
    
    protected int getNextImageIndex(ImageSelect selector) {
        int curIndex = selector.getSelectedImageIndex();
        int maxIndex = selector.getImageCount() - 1;
        int nextIndex = curIndex + 1;
        if (curIndex + 1 > maxIndex)
            return 0;
        return nextIndex;
    }
}
