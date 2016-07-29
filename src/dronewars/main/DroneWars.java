/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.main;

import com.jme3.input.Joystick;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import dronewars.input.HangarController;
import dronewars.input.EditorController;
import dronewars.input.EditorLoadingController;
import dronewars.input.ExitController;
import dronewars.input.MainMenuController;
import dronewars.input.PlayerController;
import dronewars.input.PlayerLoadingController;
import dronewars.input.SettingsController;
import dronewars.input.SpectatorController;
import dronewars.input.SpectatorLoadingController;
import dronewars.input.StartController;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.List;

/**
 *
 * @author Jan David Kleiß
 */

public class DroneWars extends StereoApplication {
    
    private final ColorRGBA backgroundColor = new ColorRGBA(227 / 255f, 219 / 255f, 201 / 255f, 1);
    
    public static void main(String[] args) {
        DroneWars app = new DroneWars();
        
        System.setProperty("java.util.logging.SimpleFormatter.format", 
            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
       
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
                
        // automatically detect the right resolution, color depth etc.
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();
//        settings.setResolution(displayMode.getWidth(), displayMode.getHeight());
        settings.setResolution(1920, 1080);
        settings.setFrameRate(60);
        settings.setBitsPerPixel(displayMode.getBitDepth());
//        settings.setFullscreen(device.isFullScreenSupported());
        
        // anti aliasing - lower to increase performance
        settings.put("Samples", 1);
        settings.setUseJoysticks(true);
        settings.put("Title", "DroneWars");
        
        app.setSettings(settings);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        super.init();
        // init nifty
        NiftyJmeDisplay display = new NiftyJmeDisplay(assetManager, inputManager,
                audioRenderer, guiViewPort);
        guiViewPort.addProcessor(display);
        Nifty nifty = display.getNifty();
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        viewPort.setBackgroundColor(backgroundColor);        
        
        // load menu screens from xml files
        nifty.registerScreenController(
                  new StartController(this)
                , new MainMenuController(this)
                , new ExitController(this)
                , new HangarController(this, display)
                , new PlayerLoadingController(this)
                , new PlayerController(this)
                , new EditorLoadingController(this)
                , new EditorController(this, display)
                , new SpectatorLoadingController(this)
                , new SpectatorController(this)
                , new SettingsController(this)
        );
        
        String[] screens = {"Hangar", "Editor", "EditorLoading", "Exit", "MainMenu",
                            "Player", "PlayerLoading", "Settings", "Spectator",
                            "SpectatorLoading", "Start"};
        for (String screen : screens) {
            nifty.addXml("Interface/Screens/" + screen + ".xml");
        }
        
        nifty.gotoScreen("MainMenu");
        
        addMappings();
    }
    
    private void addMappings() {
        addXboxMappings();
        addKeyboardMappings();
        inputManager.deleteMapping("SIMPLEAPP_Exit");
    }

    private void addXboxMappings() {
        Joystick[] joysticks = inputManager.getJoysticks();
        for (Joystick joystick : joysticks) {
            if (joystick.getName().contains("XBOX")) {
                int id = joystick.getJoyId();
                List<JoystickButton> buttons = joystick.getButtons();
                
                inputManager.addMapping("LS_UP", new JoyAxisTrigger(id, 0, true));
                inputManager.addMapping("LS_LEFT", new JoyAxisTrigger(id, 1, false));
                inputManager.addMapping("LS_RIGHT", new JoyAxisTrigger(id, 1, true));
                
                inputManager.addMapping("RS_UP", new JoyAxisTrigger(id, 2, true));
                inputManager.addMapping("RS_DOWN", new JoyAxisTrigger(id, 2, false));
                inputManager.addMapping("RS_LEFT", new JoyAxisTrigger(id, 3, true));
                inputManager.addMapping("RS_RIGHT", new JoyAxisTrigger(id, 3, false));
                                
                inputManager.addMapping("ACTION_1", new JoyAxisTrigger(id, 4, true));
                inputManager.addMapping("ACTION_2", new JoyAxisTrigger(id, 4, false));
                buttons.get(5).assignButton("ACTION_3");
                buttons.get(4).assignButton("ACTION_4");
                
                buttons.get(0).assignButton("OPTION_1");
                buttons.get(1).assignButton("OPTION_3");
                buttons.get(2).assignButton("OPTION_2");
                buttons.get(3).assignButton("OPTION_4");
                
                buttons.get(6).assignButton("BACK");
                buttons.get(7).assignButton("START");
                
                buttons.get(8).assignButton("TOGGLE_STICKS");
                buttons.get(9).assignButton("TOGGLE_STICKS");
            }
        }
        inputManager.setAxisDeadZone(0.1f);
    }

    private void addKeyboardMappings() {
        inputManager.addMapping("L_UP",  new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("L_LEFT",  new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("L_RIGHT",  new KeyTrigger(KeyInput.KEY_D));
        
        inputManager.addMapping("R_UP", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("R_DOWN", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("R_LEFT", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("R_RIGHT", new KeyTrigger(KeyInput.KEY_RIGHT));
        
        inputManager.addMapping("ACTION_1", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("ACTION_2", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("ACTION_3", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("ACTION_4", new KeyTrigger(KeyInput.KEY_F));
        
        inputManager.addMapping("OPTION_1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("OPTION_2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("OPTION_3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("OPTION_4", new KeyTrigger(KeyInput.KEY_4));
        
        inputManager.addMapping("BACK", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("START", new KeyTrigger(KeyInput.KEY_RETURN));
    }
}
