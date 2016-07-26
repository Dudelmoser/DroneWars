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
    private final float flaresDuration = 1;
    private final float respawnDelay = 3;
        
    private long lastShot;
    private long lastMissile;
    private long lastFlares;
    
    private float respawnIn = Float.MAX_VALUE;
    private float immuneFor;
    private boolean crashed;
    
    private Warzone warzone;
    private float minWaterLevel;
        
    public WarplaneControl(Warplane airplane, Warzone warzone) {
        super(airplane);
        this.warzone = warzone;
        this.minWaterLevel = warzone.getLevel().getWater().getLevel() 
                - warzone.getLevel().getWater().getLevelVariance();
    }
        
    public void fireShot() {
        long now = System.currentTimeMillis();
        if (now - lastShot > shotCooldown) {
            warzone.addShot(null, spatial.getLocalTranslation(), spatial.getLocalRotation());
            lastShot = now;
        }
    }
    
    public void fireMissile() {
        long now = System.currentTimeMillis();
        if (now - lastMissile > missileCooldown) {
            warzone.addMissile(spatial.getLocalTranslation(), spatial.getLocalRotation());
            lastMissile = now;
        }
    }
    
    public void useFlares() {
        long now = System.currentTimeMillis();
        if (now - lastFlares > flaresCooldown) {
            warzone.addFlares(spatial.getLocalTranslation(), true);
            lastFlares = now;
            immuneFor = flaresDuration;
        }
    }
    
    public void refresh(float tpf) {
        immuneFor -= tpf;
        respawnIn -= tpf;
        if (respawnIn < respawnDelay) {
            if (respawnIn < -respawnDelay) {
                respawnIn = Long.MAX_VALUE;
            } else if (respawnIn < 0 && respawnIn > -respawnDelay) {
                respawn();
            }
        } else if (getPhysicsLocation().y < minWaterLevel) {
            respawnIn = respawnDelay;
            warzone.addExplosion(spatial.getLocalTranslation(), false);
        } else {
            if (!crashed)
                applyForces(tpf);
        }
        respawnIn -= tpf;
    }
    
    public void crash() {
        crashed = true;
    }

    public void respawn() {
        respawnIn -= respawnDelay;
        setLinearVelocity(Vector3f.ZERO);
        setAngularVelocity(Vector3f.ZERO);
        Vector3f spawn = getSpawnOnCircle(warzone.getRadius(), SPAWN_HEIGHT);
        setPhysicsLocation(spawn);
        Quaternion toCenter = new Quaternion();
        toCenter.lookAt(spawn, Vector3f.UNIT_Y);
        setPhysicsRotation(toCenter);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (respawnIn > respawnDelay) {
            respawnIn = respawnDelay;
            warzone.addExplosion(spatial.getLocalTranslation(), true);
        }
    }
    
    public void hover() {
        setKinematic(!isKinematic());
    }
    
    public boolean isImmune() {
        if (immuneFor > 0) {
            return true;
        }
        return false;
    }
    
    private Vector3f getSpawnOnCircle(float radius, float height) {
        Vector3f rnd = new Vector3f(0, height, 0);
        float rndX = (float) (Math.random() * radius);
        float rndZ = (float) Math.sqrt(Math.abs(rndX*rndX - radius*radius));
        rnd.x = Math.random() > 0.5 ? rndX : -rndX;
        rnd.z = Math.random() > 0.5 ? rndZ : -rndZ;
        return rnd;
    }
        
    private Vector3f getSpawnOnSquare(float width, float height) {
        Vector3f rnd = new Vector3f(-width / 2, height, -width / 2);
        if (Math.random() > 0.5) {
            rnd.x += (float)Math.random() * width;
            rnd.z += Math.random() > 0.5 ? width : -width;
        } else {
            rnd.x += Math.random() > 0.5 ? width : -width;
            rnd.z += (float)Math.random() * width;
        }
        return rnd;
    }
}
