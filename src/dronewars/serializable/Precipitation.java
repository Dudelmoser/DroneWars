package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.Random;

/**
 *
 * @author Jan David Kleiß
 */
public class Precipitation {

    private boolean enabled = true;
    private String type = "rain";
    private int count = 2000;
    private float gravity = 0;
    private float lifetime = 12;
    private float spawnHeight = 200;
    private float spawnRate = 0.5f;
    private float rateVariance = 0.2f;
    private Vector3f velocity = new Vector3f(0, -32, 8);
    private ColorRGBA startColor = new ColorRGBA(1, 1, 1, 0);
    private ColorRGBA endColor = ColorRGBA.White;

    private transient Node node;
    private transient int size;
    private transient Node parent;
    private transient AssetManager assetManager;
    
    public Precipitation() {}
        
    public void create(Node parent, int size, AssetManager assetManager) {
        this.parent = parent;
        this.size = size;
        this.assetManager = assetManager;
        if (enabled) {
            node = new Node("Precipitation");
            ParticleEmitter template = getEmitter(assetManager);

            int particlesPerDim = (int) Math.sqrt(count);
            float particleSpacingXZ = size / (float) particlesPerDim;
            float particleSpacingY = gravity / spawnRate * 4;

            Random rnd = new Random();
            float halfRateVar = rateVariance / 2;
            for (int i = 0; i < particlesPerDim; i++) {
                for (int j = 0; j < particlesPerDim; j++) {
                    ParticleEmitter emitter = template.clone();
                    float x = i * particleSpacingXZ + rnd.nextFloat() * particleSpacingXZ;
                    float y = spawnHeight + rnd.nextFloat() * particleSpacingY;
                    float z = j * particleSpacingXZ + rnd.nextFloat() * particleSpacingXZ;
                    float rate = spawnRate - rateVariance + halfRateVar * rnd.nextFloat();
                    emitter.setParticlesPerSec(rate);
                    emitter.setLocalTranslation(x - size / 2, y, z - size / 2);
                    node.attachChild(emitter);
                }
            }
            parent.attachChild(node);
        }
    }
    
    public ParticleEmitter getEmitter(AssetManager assetManager) {
        ParticleEmitter emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Particles/" + type + ".png");
        mat.setTexture("Texture", tex);
        emitter.setStartColor(startColor);
        emitter.setEndColor(endColor);
        emitter.setStartSize(1.5f);
        emitter.setEndSize(1.5f);
        emitter.setMaterial(mat);
        emitter.setGravity(0, gravity, 0);
        emitter.setLowLife(lifetime);
        emitter.setHighLife(lifetime);
        emitter.setFacingVelocity(true);
        emitter.getParticleInfluencer().setInitialVelocity(velocity);
        emitter.getParticleInfluencer().setVelocityVariation(0);
        
        return emitter;
    }
    
    public void remove() {
        if (node != null)
            node.removeFromParent();
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            create(parent, size, assetManager);
        } else {
            remove();
        }
    }
}
