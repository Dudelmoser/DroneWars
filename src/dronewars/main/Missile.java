/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import dronewars.network.UdpBroadcastSocket;
import dronewars.serializable.Warplane;
import java.util.Map;

/**
 *
 * @author Jan David Kleiß
 */
public class Missile {
    
    private static final String modelPath = "Models/Rocket/model.blend";
    private static final String soundPath = "Sounds/missile.wav";
    private static final float velocity = 120;
    private static final float maxAngle = (float)Math.toRadians(45);
    private static final float refDistance = 20;
    private static Spatial model;
    
    private String uuid;
    private Spatial missile;
    private Spatial target;
    private AudioNode sound;
    
    private Warzone zone;
    private TerrainQuad terrain;
        
    private float lifeTime = 3;
    private boolean expired = false;
    
    public Missile(Warplane player, Map<String,Warplane> enemies, Warzone zone, 
            Node parent, TerrainQuad terrain, AssetManager assetManager) {
        this.zone = zone;
        
        init(parent, terrain, assetManager);      
        missile.setLocalTranslation(player.getSpatial().getLocalTranslation());
        missile.setLocalRotation(player.getSpatial().getLocalRotation());
        
        initUuid();
        assignTarget(enemies);
    }
    
    public Missile(String parts[], Node parent, TerrainQuad terrain, 
            AssetManager assetManager) {
        init(parent, terrain, assetManager);
        missile.setLocalTranslation(Deserializer.toVector(parts[2]));
        missile.setLocalRotation(Deserializer.toQuaternion(parts[3]));
        uuid = parts[1];
    }
    
    private void init(Node parent, TerrainQuad terrain, AssetManager assetManager) {
        this.terrain = terrain;
        
        if (model == null)
            model = assetManager.loadModel(modelPath);
        missile = model.clone();
        parent.attachChild(missile);
                
        sound = new AudioNode(assetManager, soundPath, false);
        sound.setLooping(true);
        sound.setPositional(true);
        sound.setReverbEnabled(false);
        sound.setRefDistance(refDistance);
        parent.attachChild(sound);
        sound.play();
    }
        
    private void initUuid() {
        uuid = String.valueOf(this.getClass().getName().hashCode() +
                + Float.floatToIntBits(missile.getLocalTranslation().x)
                + (int) (System.currentTimeMillis() & 0x00000000FFFFFFFFL));
    }
    
    private void assignTarget(Map<String,Warplane> targets) {
        Vector3f forward = missile.getLocalRotation().mult(Vector3f.UNIT_Z)
                .mult(-1).normalize();
        target = null;
        for (Warplane tgt : targets.values()) {
            Vector3f tgtDir = tgt.getSpatial().getLocalTranslation()
                    .subtract(missile.getLocalTranslation()).normalize();
            System.out.println(forward.angleBetween(tgtDir));
            if (forward.angleBetween(tgtDir) < maxAngle) {
                target = tgt.getSpatial();
                    break;
            }
        }
    }
    
    public void update(float tpf, UdpBroadcastSocket udp) {
        if (expired)
            return;
        lifeTime -= tpf;
        if (zone != null) {
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
            udp.send(serialize());
        }
                                    
        if (lifeTime < 0) {
            if (zone != null) {
                zone.addExplosion(missile.getLocalTranslation(), true);
            }
            remove();
        }
    } 
    
    private void checkForCollision() {
        CollisionResults results = new CollisionResults();
        terrain.collideWith(missile.getWorldBound(), results);
        
        if (results.size() > 0) {
            zone.addExplosion(missile.getLocalTranslation(), true);
            remove();
        }
    }  
    
    public void deserialize(String[] parts) {
        missile.setLocalTranslation(Deserializer.toVector(parts[2]));
        missile.setLocalRotation(Deserializer.toQuaternion(parts[3]));
    }
    
    public String serialize() {
        return "MISSILE;" + uuid + ";" 
                + Serializer.fromVector(missile.getLocalTranslation()) + ";"
                + Serializer.fromQuaternion(missile.getLocalRotation());
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public void remove() {
        expired = true;
        sound.stop();
        sound.removeFromParent();
        missile.removeFromParent();
    }
}
