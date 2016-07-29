package dronewars.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import dronewars.main.WarplaneControl;
import dronewars.main.PlayerState;
import dronewars.main.StereoApplication;
/** 
 * 
 * @author Jan David Kleiß
 */
public class PlayerController extends DefaultController {
    
    private final float analogTreshold = 0.5f;
    
    private boolean joystick = false;
    
    private WarplaneControl warplane;
    private PlayerState state;
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (warplane == null) {
                warplane = state.getCombatControl();
                return;
            }
            
            if (name.equals("ACTION_1")) {
                warplane.fireShot();
            } else if(name.equals("ACTION_2")) {
                warplane.fireMissile();
            }
            
            if (!joystick)
                return;
                        
            float res = value / tpf;
            if (res < analogTreshold)
                res = 0;
            
            switch (name) {
                case "LS_UP":
                    warplane.setThrottle(res);
                    break;
                case "LS_LEFT":
                    warplane.setYaw(res);
                    break;
                case "LS_RIGHT":
                    warplane.setYaw(-res);
                    break;
                case "RS_UP":
                    warplane.setPitch(res);
                    break;
                case "RS_DOWN":
                    warplane.setPitch(-res);
                    break;
                case "RS_LEFT":
                    warplane.setRoll(res);
                    break;
                case "RS_RIGHT":
                    warplane.setRoll(-res);
                    break;
            }
        }
        
    };
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (warplane == null) {
                warplane = state.getCombatControl();
                return;
            }

            if (keyPressed) {
                switch (name) {
                    case "L_UP":
                        warplane.setThrottle(1);
                        break;
                    case "L_LEFT":
                        warplane.setYaw(-1);
                        break;
                    case "L_RIGHT":
                        warplane.setYaw(1);
                        break;
                    case "R_UP":
                        warplane.setPitch(1);
                        break;
                    case "R_DOWN":
                        warplane.setPitch(-1);
                        break;
                    case "R_LEFT":
                        warplane.setRoll(1);
                        break;
                    case "R_RIGHT":
                        warplane.setRoll(-1);
                        break;
                }
            }
            
            if (!keyPressed) {
                switch (name) {
                    case "L_UP":
                        warplane.setThrottle(0);
                        break;
                    case "L_LEFT":
                        warplane.setYaw(0);
                        break;
                    case "L_RIGHT":
                        warplane.setYaw(0);
                        break;
                    case "R_UP":
                        warplane.setPitch(0);
                        break;
                    case "R_DOWN":
                        warplane.setPitch(0);
                        break;
                    case "R_LEFT":
                        warplane.setRoll(0);
                        break;
                    case "R_RIGHT":
                        warplane.setRoll(0);
                        break;
                    case "ACTION_3":
                        warplane.hover();
                        break;
                    case "ACTION_4":
                        warplane.useFlares();
                        break;
                    case "OPTION_1":
                        state.toggleTrail();
                        break;
                    case "OPTION_2":
                        break;
                    case "OPTION_3":
                        break;
                    case "OPTION_4":
                        warplane.respawn();
                        break;
                    case "BACK":
                        nifty.gotoScreen("Exit");
                        break;
                    case "START":
                        System.exit(0);
                        break;
                    case "TOGGLE_STICKS":
                        joystick = !joystick;
                        break;
                }
            }
        }
    };

    public PlayerController(StereoApplication app) {
        super(app);
    }
    
    @Override
    public void onStartScreen() {
        inputManager.setCursorVisible(false);
        if (state == null)
            state = stateManager.getState(PlayerState.class);
        state.setEnabled(true);
        
        inputManager.addListener(analogListener, 
                "LS_UP", "LS_LEFT", "LS_RIGHT",
                "RS_UP", "RS_DOWN", "RS_LEFT", "RS_RIGHT",
                "ACTION_1", "ACTION_2");
        inputManager.addListener(actionListener,
                "L_UP", "L_LEFT", "L_RIGHT",
                "R_UP", "R_DOWN", "R_LEFT", "R_RIGHT",
                "ACTION_3", "ACTION_4",
                "OPTION_1", "OPTION_2", "OPTION_3", "OPTION_4",
                "BACK", "START", "TOGGLE_STICKS");
    }
    
    @Override
    public void onEndScreen() {
        inputManager.setCursorVisible(true);
        state.setEnabled(false);
        
        inputManager.removeListener(analogListener);
        inputManager.removeListener(actionListener);
    }
}
