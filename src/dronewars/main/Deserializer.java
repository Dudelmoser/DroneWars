/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jan David Kleiß
 */
public class Deserializer {

    private static final String sep = ",";
    
    public static Vector3f toVector(String serialized) {
        String[] parts = serialized.split(sep);
        return new Vector3f(Float.parseFloat(parts[0]), 
                Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }
    
    public static Quaternion toQuaternion(String serialized) {
        String[] parts = serialized.split(sep);
        Quaternion q = new Quaternion();
        q.set(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]),
                Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
        return q;
    }
    
    public static ColorRGBA toColor(String serialized) {
        String[] parts = serialized.split(sep);
        return new ColorRGBA(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), 
                Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
    }
}
