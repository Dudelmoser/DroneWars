/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import dronewars.serializable.Airplane;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Jan David Kleiß
 */
public class Missile {
    
    private static final String modelPath = "Models/Rocket/model.blend";
    private static final String soundPath = "Sounds/missile.wav";
    private static final float velocity = 120;
    private static final float maxAngle = (float)Math.toRadians(45);
    private static final int duration = 3000;
    private static final float refDistance = 20;
    private static final float maxDistance = 1000;
    private static Spatial model;
    
    private boolean active;
    private int hashCode;
    private long spawnTime;
    private Spatial missile;
    private Spatial target;
    private AudioNode sound;
    
    private Warzone zone;
    private TerrainQuad terrain;
    
    public Missile(RigidBodyControl origin, Map<String, Airplane> targets, Warzone zone,
            Node parent, TerrainQuad terrain, AssetManager assetManager) {
        
        this.zone = zone;
        
        init(parent, terrain, assetManager);      
        missile.setLocalTranslation(origin.getPhysicsLocation());
        missile.setLocalRotation(origin.getPhysicsRotation());
        
        active = true;
        createHashCode();
        assignTarget(targets);
    }
    
    public Missile(int hash, Node parent, TerrainQuad terrain, 
            AssetManager assetManager) {
        
        init(parent, terrain, assetManager);
        active = false;
        hashCode = hash;
    }
    
    private void init(Node parent, TerrainQuad terrain, AssetManager assetManager) {
        this.terrain = terrain;
        
        if (model == null)
            model = assetManager.loadModel(modelPath);
        missile = model.clone();
        parent.attachChild(missile);
        
        
        sound = new AudioNode(assetManager, soundPath, false);
//        sound.setLooping(true);
        sound.setPositional(true);
        sound.setReverbEnabled(false);
        sound.setRefDistance(refDistance);
        parent.attachChild(sound);
        sound.play();
        
        spawnTime = System.currentTimeMillis();
    }
        
    private void createHashCode() {
        hashCode = this.getClass().getName().hashCode()
                + Float.floatToIntBits(missile.getLocalTranslation().x)
                + (int) (System.currentTimeMillis() & 0x00000000FFFFFFFFL);   
    }
    
    private void assignTarget(Map<String,Airplane> targets) {
        Vector3f forward = missile.getLocalRotation().mult(Vector3f.UNIT_Z)
                .mult(-1).normalize();
        target = null;
        for (Airplane tgt : targets.values()) {
            Vector3f tgtDir = tgt.getSpatial().getLocalTranslation()
                    .subtract(missile.getLocalTranslation()).normalize();
            System.out.println(forward.angleBetween(tgtDir));
            if (forward.angleBetween(tgtDir) < maxAngle) {
                target = tgt.getSpatial();
                    break;
            }
        }
    }
        
    public void update(float tpf) {
        if (target != null) {
            Vector3f step = target.getLocalTranslation().subtract(missile.getLocalTranslation())
                    .normalize().mult(velocity * tpf);
            missile.move(step);
            missile.lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
        } else {
            Vector3f step = missile.getLocalRotation().mult(Vector3f.UNIT_Z)
                    .normalize().mult(-velocity * tpf);
            missile.move(step);
        }
        
        sound.setLocalTranslation(missile.getLocalTranslation());
        
        checkForCollision();
    } 
    
    public void update(Vector3f position, Quaternion rotation) {
        missile.setLocalTranslation(position);
        missile.setLocalRotation(rotation);
    }
    
    private void checkForCollision() {
        CollisionResults results = new CollisionResults();
        terrain.collideWith(missile.getWorldBound(), results);
        
        if (results.size() > 0) {
            spawnTime = 0;
            sound.stop();
            zone.addExplosion(missile.getLocalTranslation());
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() - spawnTime > duration;
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
        Node parent = missile.getParent();
        parent.detachChild(missile);
        parent.detachChild(sound);
    }
}
