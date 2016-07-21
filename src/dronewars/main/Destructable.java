/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.bullet.collision.shapes.CollisionShape;

/**
 *
 * @author Jan David Kleiß
 */
public interface Destructable {
    public CollisionShape getCollisionShape();
    public void destroy();
}
