/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import dronewars.serializable.Warplane;

/**
 *
 * @author Jan David Kleiß
 */
public class Shot extends Effect {

    private final float width = 0.1f;
    private final float length = 1024;
    private final float maxAngle = (float) Math.toRadians(20);
    
    private static final Quaternion ROLL90 = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X);
    private static final Quaternion YAW90 = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
    
    private Node bulletTrail;
    private AudioNode sound;
        
    public Shot(boolean collide, Vector3f position, Quaternion rotation, Warzone zone,
            Timer timer, AssetManager assetManager) {
        super(0.7f, timer);
        this.bulletTrail = new Node("BulletTrail");
        
        Vector3f direction = getTarget(collide, position, rotation, zone);
        
        if (collide) {
        Quaternion quat = new Quaternion();
            quat.lookAt(direction, Vector3f.UNIT_Y);
        zone.getSocket().send("SHOT;" + Serializer.fromVector(position) + ";"
                                      + Serializer.fromQuaternion(quat));
        }
        
        Geometry trail1 = getTrail(assetManager, timer);
        trail1.setLocalTranslation(position);
        Quaternion rot1 = new Quaternion();
        rot1.lookAt(direction.negate(), Vector3f.UNIT_Z);
        trail1.setLocalRotation(rot1);
        trail1.setLocalRotation(trail1.getLocalRotation().mult(ROLL90));
        trail1.move(rotation.getRotationColumn(0).normalize().mult(-width / 2));
        bulletTrail.attachChild(trail1);

        Spatial trail2 = trail1.deepClone();
        Quaternion rot2 = new Quaternion();
        rot2.lookAt(direction.negate(), Vector3f.UNIT_X);
        trail2.setLocalRotation(trail2.getLocalRotation().mult(YAW90));
        trail2.move(rotation.getRotationColumn(1).normalize().mult(width / 2));
        trail2.move(rotation.getRotationColumn(0).normalize().mult(width / 2));
        bulletTrail.attachChild(trail2);
        
        zone.getNode().attachChild(bulletTrail);
        
        sound = new AudioNode(assetManager, "Sounds/shot.wav", false);
        sound.setPositional(true);
        sound.setLocalTranslation(position);
        bulletTrail.attachChild(sound);
        sound.play();
    }
            
    private Vector3f getTarget(boolean collide, Vector3f position, Quaternion rotation, 
            Warzone zone) {
        Vector3f forward = rotation.mult(Vector3f.UNIT_Z).negate().normalize();
        if (!collide)
            return forward;
        
        float closestAngle = FastMath.PI;
        Vector3f targetDir = null;
        Warplane target = null;
        
        for (Warplane enemy : zone.getEnemies().values()) {
            targetDir = enemy.getSpatial().getLocalTranslation()
                    .subtract(position).normalize();
            float angle = forward.angleBetween(targetDir);
            if (angle < maxAngle && angle < closestAngle) {
                target = enemy;
            }
        }
        
        if (target != null) {
            zone.getSocket().send("HIT;" + target.getUuid());
        } else {
            targetDir = forward;
        }
        return targetDir;
    }
    
    private Geometry getTrail(AssetManager assetManager, Timer timer) {
        Quad quad = new Quad(width, length);
        quad.scaleTextureCoordinates(new Vector2f(1, length / 2));
        Geometry trail = new Geometry("Trail", quad);
                
        trail.setQueueBucket(RenderQueue.Bucket.Translucent);
        trail.setMaterial(getMaterial(assetManager, timer));
        return trail;
    }

    private Material getMaterial(AssetManager assetManager, Timer timer) {
        Material mat = new Material(assetManager, "MatDefs/Bullet/Bullet.j3md");
        mat.setFloat("StartTime", timer.getTimeInSeconds());
        mat.setFloat("FadeTime", lifeTime);
        
        Texture tex = assetManager.loadTexture("Particles/smoketrail.png");
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(true);
        
        return mat;
    }
    
    @Override
    public void remove() {
        System.out.println("shot removed");
        sound.removeFromParent();
        bulletTrail.removeFromParent();
    }
    
    @Override
    public void update(float tpf) {
        
    }
}
