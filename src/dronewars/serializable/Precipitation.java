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

    public Precipitation() {}
        
    public void create(Node parent, int size, AssetManager assetManager) {
        Node precipitation = new Node("Precipitation");
        ParticleEmitter template = getEmitter(assetManager);

        int particlesPerDim = (int) Math.sqrt(getCount());
        float particleSpacingXZ = size / (float) particlesPerDim;
        float particleSpacingY = getGravity() / getSpawnRate() * 4;

        Random rnd = new Random();
        float halfRateVar = getRateVariance() / 2;
        for (int i = 0; i < particlesPerDim; i++) {
            for (int j = 0; j < particlesPerDim; j++) {
                ParticleEmitter emitter = template.clone();
                float x = i * particleSpacingXZ + rnd.nextFloat() * particleSpacingXZ;
                float y = getSpawnHeight() + rnd.nextFloat() * particleSpacingY;
                float z = j * particleSpacingXZ + rnd.nextFloat() * particleSpacingXZ;
                float rate = getSpawnRate() - getRateVariance() + halfRateVar * rnd.nextFloat();
                emitter.setParticlesPerSec(rate);
                emitter.setLocalTranslation(x - size / 2, y, z - size / 2);
                precipitation.attachChild(emitter);
            }
        }
        parent.attachChild(precipitation);
    }
    
    public ParticleEmitter getEmitter(AssetManager assetManager) {
        ParticleEmitter emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Particles/" + getType() + ".png");
        mat.setTexture("Texture", tex);
        emitter.setStartColor(getStartColor());
        emitter.setEndColor(getEndColor());
        emitter.setStartSize(1.5f);
        emitter.setEndSize(1.5f);
        emitter.setMaterial(mat);
        emitter.setGravity(0, getGravity(), 0);
        emitter.setLowLife(getLifetime());
        emitter.setHighLife(getLifetime());
        emitter.setFacingVelocity(true);
        emitter.getParticleInfluencer().setInitialVelocity(getVelocity());
        emitter.getParticleInfluencer().setVelocityVariation(0);
        
        return emitter;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the gravity
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * @param gravity the gravity to set
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * @return the lifetime
     */
    public float getLifetime() {
        return lifetime;
    }

    /**
     * @param lifetime the lifetime to set
     */
    public void setLifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    /**
     * @return the spawnHeight
     */
    public float getSpawnHeight() {
        return spawnHeight;
    }

    /**
     * @param spawnHeight the spawnHeight to set
     */
    public void setSpawnHeight(float spawnHeight) {
        this.spawnHeight = spawnHeight;
    }

    /**
     * @return the spawnRate
     */
    public float getSpawnRate() {
        return spawnRate;
    }

    /**
     * @param spawnRate the spawnRate to set
     */
    public void setSpawnRate(float spawnRate) {
        this.spawnRate = spawnRate;
    }

    /**
     * @return the rateVariance
     */
    public float getRateVariance() {
        return rateVariance;
    }

    /**
     * @param rateVariance the rateVariance to set
     */
    public void setRateVariance(float rateVariance) {
        this.rateVariance = rateVariance;
    }

    /**
     * @return the velocity
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the startColor
     */
    public ColorRGBA getStartColor() {
        return startColor;
    }

    /**
     * @param startColor the startColor to set
     */
    public void setStartColor(ColorRGBA startColor) {
        this.startColor = startColor;
    }

    /**
     * @return the endColor
     */
    public ColorRGBA getEndColor() {
        return endColor;
    }

    /**
     * @param endColor the endColor to set
     */
    public void setEndColor(ColorRGBA endColor) {
        this.endColor = endColor;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean bool){
        this.enabled = bool;
    }
}
