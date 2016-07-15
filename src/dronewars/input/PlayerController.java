package dronewars.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import dronewars.main.PlayerState;
import dronewars.serializable.Drone;
/** 
 * 
 * @author Jan David Kleiß
 */
public class PlayerController extends DefaultController {
    
    private final float analogTreshold = 0.5f;
    private boolean joystick = false;
    
    private Drone drone;
    private PlayerState state;
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (!joystick)
                return;
            
            if (drone == null) {
                drone = state.getDrone();
                return;
            }
            
            float res = value / tpf;
            if (res < analogTreshold)
                res = 0;
            
            switch (name) {
                case "LS_UP":
                    drone.setThrottle(res);
                    break;
                case "LS_DOWN":
                    drone.setThrottle(-res);
                    break;
                case "LS_LEFT":
                    drone.setYaw(res);
                    break;
                case "LS_RIGHT":
                    drone.setYaw(-res);
                    break;
                case "RS_UP":
                    drone.setPitch(res);
                    break;
                case "RS_DOWN":
                    drone.setPitch(-res);
                    break;
                case "RS_LEFT":
                    drone.setRoll(res);
                    break;
                case "RS_RIGHT":
                    drone.setRoll(-res);
                    break;
            }
        }
        
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println("ACTION " + name + " " + keyPressed);
            if (drone == null) {
                drone = state.getDrone();
                return;
            }

            if (keyPressed) {
                switch (name) {
                    case "L_UP":
                        drone.setThrottle(1);
                        break;
                    case "L_DOWN":
                        drone.setThrottle(-1);
                        break;
                    case "L_LEFT":
                        drone.setYaw(-1);
                        break;
                    case "L_RIGHT":
                        drone.setYaw(1);
                        break;
                    case "R_UP":
                        drone.setPitch(1);
                        break;
                    case "R_DOWN":
                        drone.setPitch(-1);
                        break;
                    case "R_LEFT":
                        drone.setRoll(1);
                        break;
                    case "R_RIGHT":
                        drone.setRoll(-1);
                        break;
                }
            }
            
            if (!keyPressed) {
                switch (name) {
                    case "L_UP":
                        drone.setThrottle(0);
                        break;
                    case "L_DOWN":
                        drone.setThrottle(0);
                        break;
                    case "L_LEFT":
                        drone.setYaw(0);
                        break;
                    case "L_RIGHT":
                        drone.setYaw(0);
                        break;
                    case "R_UP":
                        drone.setPitch(0);
                        break;
                    case "R_DOWN":
                        drone.setPitch(0);
                        break;
                    case "R_LEFT":
                        drone.setRoll(0);
                        break;
                    case "R_RIGHT":
                        drone.setRoll(0);
                        break;
                    case "ACTION_1":
                        drone.fire();
                        break;
                    case "ACTION_2":
                        drone.missile();
                        break;
                    case "ACTION_3":
                        drone.flare();
                        break;
                    case "ACTION_4":
                        drone.stabilize();
                        break;
                    case "OPTION_1":
                        drone.fire();
                        break;
                    case "OPTION_2":
                        drone.missile();
                        break;
                    case "OPTION_3":
                        drone.flare();
                        break;
                    case "OPTION_4":
                        drone.stabilize();
                        break;
                    case "BACK":
                        nifty.gotoScreen("Exit");
                        break;
                    case "START":
                        nifty.gotoScreen("Exit");
                        break;
                    case "TOGGLE_STICKS":
                        joystick = !joystick;
                        break;
                }
            }
        }
    };

    public PlayerController(SimpleApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        if (state == null)
            state = stateManager.getState(PlayerState.class);
        state.setEnabled(true);
        
        inputManager.addListener(analogListener, 
                "LS_UP", "LS_DOWN", "LS_LEFT", "LS_RIGHT",
                "RS_UP", "RS_DOWN", "RS_LEFT", "RS_RIGHT");
        inputManager.addListener(actionListener,
                "L_UP", "L_DOWN", "L_LEFT", "L_RIGHT",
                "R_UP", "R_DOWN", "R_LEFT", "R_RIGHT",
                "ACTION_1", "ACTION_2", "ACTION_3", "ACTION_4",
                "OPTION_1", "OPTION_2", "OPTION_3", "OPTION_4",
                "BACK", "START", "TOGGLE_STICKS");
    }
    
    @Override
    public void onEndScreen() {
        state.setEnabled(false);
        
        inputManager.removeListener(analogListener);
        inputManager.removeListener(actionListener);
    }
}
