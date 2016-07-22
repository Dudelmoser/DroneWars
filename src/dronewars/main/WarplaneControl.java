/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Airplane;

/**
 *
 * @author Jan David Kleiß
 */
public class WarplaneControl extends AirplaneControl implements PhysicsCollisionListener {
    
    private final int shotCooldown = 100;
    private final int missileCooldown = 2000;
    private final int flaresCooldown = 10000;
    private final int respawnDelay = 1000;
    private final float respawnTreshold = 2;
    
    private Vector3f spawnpoint = new Vector3f(0, 300, 0);
        
    private long lastShot;
    private long lastMissile;
    private long lastFlares;
    private boolean broken = false;
    private long crashTime = Long.MAX_VALUE;
    
    private Warzone warzone;
        
    public WarplaneControl(Airplane airplane, Warzone warzone) {
        super(airplane);
        this.warzone = warzone;
    }
        
    public void fireShot() {
        long now = System.currentTimeMillis();
        if (now - lastShot > shotCooldown) {
            warzone.addShot();
            lastShot = now;
        }
    }
    
    public void fireMissile() {
        long now = System.currentTimeMillis();
        if (now - lastMissile > missileCooldown) {
            warzone.addMissile();
            lastMissile = now;
        }
    }
    
    public void useFlares() {
        long now = System.currentTimeMillis();
        if (now - lastFlares > flaresCooldown) {
            warzone.addFlares();
            lastFlares = now;
        }
    }
    
    public void refresh(float tpf) {
        if (broken) {
            long tPassed = System.currentTimeMillis() - crashTime;
            if (tPassed > respawnDelay) {
                respawn();
            } else {
                return;
            }
        }
        applyForces(tpf);
    }

    public void respawn() {
        setLinearVelocity(Vector3f.ZERO);
        setAngularVelocity(Vector3f.ZERO);
        setPhysicsLocation(spawnpoint);
        setPhysicsRotation(Quaternion.ZERO);
        crashTime = Long.MAX_VALUE;
        broken = false;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (!broken) {
            broken = true;
            warzone.addExplosion(spatial.getLocalTranslation());
        } else if (crashTime == Long.MAX_VALUE 
                    && getAngularVelocity().length() < respawnTreshold) {
                crashTime = System.currentTimeMillis();
        }
    }
}
