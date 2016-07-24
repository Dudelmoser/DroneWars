/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Warplane;

/**
 *
 * @author Jan David Kleiß
 */
public class WarplaneControl extends AirplaneControl implements PhysicsCollisionListener {
    
    private static final float SPAWN_HEIGHT = 200;
    private final int shotCooldown = 100;
    private final int missileCooldown = 2000;
    private final int flaresCooldown = 4000;
    private final int respawnDelay = 2000;
    private final float respawnTreshold = 2;
        
    private long lastShot;
    private long lastMissile;
    private long lastFlares;
    private boolean broken = false;
    private long crashTime = Long.MAX_VALUE;
    
    private Warzone warzone;
        
    public WarplaneControl(Warplane airplane, Warzone warzone) {
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
        crashTime = Long.MAX_VALUE;
        broken = false;
        setLinearVelocity(Vector3f.ZERO);
        setAngularVelocity(Vector3f.ZERO);
        Vector3f spawn = getRandomSpawn();
        setPhysicsLocation(spawn);
        Quaternion toCenter = new Quaternion();
        toCenter.lookAt(spawn.negate(), Vector3f.UNIT_Y);
        setPhysicsRotation(toCenter);
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
    
    public void hover() {
        setKinematic(!isKinematic());
    }
        
    private Vector3f getRandomSpawn() {
        Vector3f rnd = Vector3f.UNIT_Y.mult(SPAWN_HEIGHT);
        if (Math.random() > 0.5) {
            rnd.x = (float)Math.random() * warzone.getSize();
            rnd.z = warzone.getSize();
        } else {
            rnd.x = warzone.getSize();
            rnd.z = (float)Math.random() * warzone.getSize();
        }
        return rnd;
    }
}
