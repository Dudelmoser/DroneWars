/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
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

/**
 *
 * @author Jan David Kleiß
 */
public class Shot extends Effect {

    private final float width = 0.1f;
    private final float length = 1024;
    
    private static final Quaternion PITCH90 = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X);
    private static final Quaternion YAW90 = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
    
    private Node parent;
    private Node bulletTrail;
        
    public Shot(Node parent, Spatial emitter, Timer timer, AssetManager assetManager) {
        super(0.7f, timer);
        this.parent = parent;
        this.bulletTrail = new Node("BulletTrail");
        
        Geometry trail1 = getTrail(assetManager, timer);
        trail1.setLocalTranslation(emitter.getLocalTranslation());
        trail1.setLocalRotation(emitter.getLocalRotation());
        trail1.setLocalRotation(trail1.getLocalRotation().mult(PITCH90));
        trail1.move(emitter.getLocalRotation().getRotationColumn(0).normalize().mult(-width / 2));
        bulletTrail.attachChild(trail1);

        Spatial trail2 = trail1.deepClone();
        Quaternion rot2 = trail2.getLocalRotation().mult(YAW90);
        trail2.setLocalRotation(rot2);
        trail2.move(emitter.getLocalRotation().getRotationColumn(1).normalize().mult(width / 2));
        trail2.move(emitter.getLocalRotation().getRotationColumn(0).normalize().mult(width / 2));
        bulletTrail.attachChild(trail2);
        
        parent.attachChild(bulletTrail);
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
        parent.detachChild(bulletTrail);
    }
}
