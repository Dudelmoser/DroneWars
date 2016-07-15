package dronewars.serializable;

import com.jme3.math.Vector3f;
import java.io.Serializable;

/**
 *
 * @author Jan David Kleiß
 */
public class Player implements Serializable {
    public int hp;
    public Vector3f position;
    public Vector3f orientation;
    
    public Player() {
        this.hp = 100;
        this.position = new Vector3f();
        this.orientation = new Vector3f();
    }
    
    public Player(int hp, Vector3f position, Vector3f orientation) {
        this.hp = hp;
        this.position = position;
        this.orientation = orientation;
    }
}
