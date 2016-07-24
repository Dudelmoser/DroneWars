/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
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
public class Flares extends Effect {
    private static final float velocity = 10;
    private static ParticleEmitter template;
    
    private AudioNode sound;
    private ParticleEmitter flares;
    
    public Flares(Vector3f position, Node parent, Timer timer, AssetManager assetManager) {
        super(4, timer);
        if (template == null) {
            template = new ParticleEmitter("Flares", ParticleMesh.Type.Triangle, 20);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", assetManager.loadTexture("Particles/flares.png"));
            template.setGravity(0, 9.8f, 0);
            template.setSelectRandomImage(true);
            template.setImagesX(2);
            template.setImagesY(2);
            template.setQueueBucket(RenderQueue.Bucket.Translucent);
            mat.getAdditionalRenderState().setDepthWrite(true);
            template.setRandomAngle(true);
            template.setMaterial(mat);
            template.setStartSize(0f);
            template.setEndSize(2);
            template.setLowLife(1);
            template.setHighLife(1);
            template.setStartColor(ColorRGBA.White);
            template.setEndColor(ColorRGBA.White);
        }
    
        flares = template.clone();
        Vector3f rndVel = new Vector3f((float) Math.random(), 0,
                (float) Math.random()).normalize().mult(velocity);
        flares.getParticleInfluencer().setInitialVelocity(rndVel);
        flares.getParticleInfluencer().setVelocityVariation(1);
        
        flares.setLocalTranslation(position);
        parent.attachChild(flares);
        flares.emitAllParticles(); 
        flares.setParticlesPerSec(0);
        
        sound = new AudioNode(assetManager, "Sounds/flares.wav", false);
        sound.setPositional(true);
        sound.setRefDistance(20);
        sound.setReverbEnabled(false);
        sound.setVolume(10);
        parent.attachChild(sound);
        sound.play();
    }

    @Override
    public void remove() {
        flares.removeFromParent();
        sound.stop();
        sound.removeFromParent();
    }
    
    @Override
    public void update(float tpf) {
        
    }
}
