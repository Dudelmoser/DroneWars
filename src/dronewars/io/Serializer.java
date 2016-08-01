/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.io;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jan David Kleiß
 */
public class Serializer {
    
    private static final String sep = ",";
    
    public static String fromVector(Vector3f v) {
        return v.x + sep + v.y + sep + v.z;
    }
    
    public static String fromQuaternion(Quaternion q) {
        return q.getX() + sep + q.getY() + sep + q.getZ() + sep + q.getW();
    }
    
    public static String fromColor(ColorRGBA c) {
        return c.r + sep + c.g + sep + c.b + sep + c.a;
    }
}
