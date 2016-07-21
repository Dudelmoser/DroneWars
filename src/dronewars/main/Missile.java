/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import dronewars.serializable.Airplane;
import java.util.List;

/**
 *
 * @author Jan David Kleiß
 */
public class Missile {
    
    private static final String path = "Models/Rocket/model.blend";
    private static final float velocity = 120;
    private static final float maxAngle = 45;
    private static final long duration = 5000;
    private static Spatial model;
    
    private int hashCode;
    private boolean active;
    private Spatial missile;
    private Spatial target;
    
    private long spawnTime;
    
    private TerrainQuad terrain;
    
    public Missile(RigidBodyControl origin, List<Airplane> targets,
            Node parent, TerrainQuad terrain, AssetManager assetManager) {
        this.terrain = terrain;
        
        active = true;
        if (model == null)
            model = assetManager.loadModel(path);
        missile = model.clone();
        parent.attachChild(missile);
        
        missile.setLocalTranslation(origin.getPhysicsLocation());
        missile.setLocalRotation(origin.getPhysicsRotation());
        
        createHashCode();
        assignTarget(targets);
        spawnTime = System.currentTimeMillis();
    }
    
    public Missile(int hash, Node parent, AssetManager assetManager) {
        active = false;
        if (model == null)
            model = assetManager.loadModel(path);
        missile = model.clone();
        parent.attachChild(missile);
        
        hashCode = hash;
        spawnTime = System.currentTimeMillis();
    }
        
    private void createHashCode() {
        hashCode = this.getClass().getName().hashCode()
                + Float.floatToIntBits(missile.getLocalTranslation().x)
                + (int) (System.currentTimeMillis() & 0x00000000FFFFFFFFL);   
    }
    
    private void assignTarget(List<Airplane> targets) {
        Vector3f forward = missile.getLocalRotation().mult(Vector3f.UNIT_Z);
        target = null;
        for (Airplane tgt : targets) {
            Vector3f tgtDir = tgt.getSpatial().getLocalTranslation()
                    .subtract(missile.getLocalTranslation());
            if (forward.angleBetween(tgtDir) < maxAngle) {
                if (target != null && target.getLocalTranslation().length() > tgtDir.length()) {
                    target = tgt.getSpatial();
                }
            }
        }
    }
        
    public void update(float tpf) {
        if (target != null) {
            Vector3f step = target.getLocalTranslation().subtract(missile.getLocalTranslation())
                    .normalize().mult(velocity * tpf);
            Quaternion rot = new Quaternion().fromAngleAxis(0, step);
            missile.setLocalRotation(rot);
            missile.move(step);
        } else {
            Vector3f step = missile.getLocalRotation().mult(Vector3f.UNIT_Z)
                    .normalize().mult(-velocity * tpf);
            missile.move(step);
        }
        
        CollisionResults results = new CollisionResults();
        terrain.collideWith(missile.getWorldBound(), results);
        
        if (results.size() > 0) {
            spawnTime = 0;
        }
    } 
    
    public void update(Vector3f position, Vector3f direction) {
        missile.setLocalTranslation(position);
        missile.setLocalRotation(new Quaternion().fromAngleAxis(0, direction));
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean hasExpired() {
        if (System.currentTimeMillis() - spawnTime > duration)
            return true;
        return false;
    }
    
    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        Missile r = (Missile) obj;
        if (r.hashCode == hashCode)
            return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    public Vector3f getPosition() {
        return missile.getLocalTranslation();
    }
    
    public void remove() {
        System.out.println("removing missile");
        missile.getParent().detachChild(missile);
    }
}
