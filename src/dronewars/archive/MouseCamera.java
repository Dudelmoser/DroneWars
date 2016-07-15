package dronewars.archive;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Jan David Kleiß
 */
public class MouseCamera implements AnalogListener {

    protected Camera cam;
    protected Vector3f initialUpVec;
    private final float ROTATION_SPEED = 1;
    private static String[] mappings = {"CamLeft", "CamRight", "CamUp", "CamDown"};
    
    public MouseCamera(Camera cam, InputManager input) {
        this.cam = cam;
        initialUpVec = cam.getUp().clone();

        input.addMapping("CamLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        input.addMapping("CamRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        input.addMapping("CamUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        input.addMapping("CamDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        input.addListener(this, mappings);
    }

    protected void rotateCamera(float value, Vector3f axis) {
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(ROTATION_SPEED * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("CamLeft")) {
            rotateCamera(value, initialUpVec);
        } else if (name.equals("CamRight")) {
            rotateCamera(-value, initialUpVec);
        } else if (name.equals("CamUp")) {
            rotateCamera(-value, cam.getLeft());
        } else if (name.equals("CamDown")) {
            rotateCamera(value, cam.getLeft());
        }
    }
}