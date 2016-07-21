/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.system.Timer;

/**
 *
 * @author Jan David Kleiß
 */
public class Explosion extends Effect {
    private static final float VELOCITY = 100;
    private static final int PARTICLES = 40;
    private static ParticleEmitter template;
    
    private ParticleEmitter flares;
    
    public Explosion(Timer timer, AssetManager assetManager) {
        super(1, timer);
        if (template == null) {
            template = new ParticleEmitter("Explosion", ParticleMesh.Type.Triangle, PARTICLES);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", assetManager.loadTexture("Particles/explosion.png"));
            template.setSelectRandomImage(true);
            template.setImagesX(2);
            template.setImagesY(2);
            template.setQueueBucket(RenderQueue.Bucket.Translucent);
            mat.getAdditionalRenderState().setDepthWrite(true);
            template.setRandomAngle(true);
            template.setMaterial(mat);
            template.setStartSize(0f);
            template.setEndSize(1);
            template.setLowLife(1);
            template.setHighLife(1);
            template.setStartColor(ColorRGBA.White);
            template.setEndColor(ColorRGBA.White);
        }
    }
    
    public void trigger(Vector3f position, Node parent) {
        flares = template.clone();
        Vector3f rndVel = new Vector3f((float) Math.random(), (float) Math.random(),
                (float) Math.random()).normalize().mult(VELOCITY);
        flares.getParticleInfluencer().setInitialVelocity(rndVel);
        flares.getParticleInfluencer().setVelocityVariation(0.5f);
        
        flares.setLocalTranslation(position);
        parent.attachChild(flares);
        flares.emitAllParticles(); 
        flares.setParticlesPerSec(0);
    }

    @Override
    public void remove() {
        Node parent = flares.getParent();
        parent.detachChild(flares);
    }
}