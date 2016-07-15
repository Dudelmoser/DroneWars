package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Random;

/**
 *
 * @author Jan David Kleiß
 */
public class Species {
    private Model model;
    // 2 by the power of this variable gives the real spacing in world units
    // must be between 1 and 10 - 10 being 1 in the whole 1024x1024 terrain
    private int density = 2;
    private float scaleVar = 0.3f;
    private int channel = 0;
    private Vector3f angleVar = new Vector3f(0,360,0);
    
    public Species() {}
    
    public Species(Model model, int channel) {
        this.model = model;
        this.channel = channel;
    }
    
    public Spatial getRandomSpatial(AssetManager assetManager, Random rng) {
        Model clone = model.clone();
        clone.setScale(getRandomScale(model.getScale(), scaleVar, rng));
        Vector3f rot = clone.getRotation();
        rot.x = getRandomAngle(rot.x, getAngleVar().x, rng);
        rot.y = getRandomAngle(rot.y, getAngleVar().y, rng);
        rot.z = getRandomAngle(rot.z, getAngleVar().z, rng);
        return clone.getSpatial(assetManager);
    }
    
    private float getRandomScale(float scale, float variance, Random rng) {
        float range = scale * variance;
        float delta = range * rng.nextFloat();
        return scale + delta - (range / 2);
    }
    
    private float getRandomAngle(float deg, float variance, Random rng) {
        float var = variance * rng.nextFloat();
        return deg + var;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the spacing
     */
    public int getDensity() {
        return density;
    }

    /**
     * @param density the spacing to set
     */
    public void setDensity(int density) {
        this.density = density;
    }

    /**
     * @return the scaleVar
     */
    public float getScaleVar() {
        return scaleVar;
    }

    /**
     * @param scaleVar the scaleVar to set
     */
    public void setScaleVar(float scaleVar) {
        this.scaleVar = scaleVar;
    }

    /**
     * @return the angleVar
     */
    public Vector3f getAngleVar() {
        return angleVar;
    }

    /**
     * @param angleVar the angleVar to set
     */
    public void setAngleVar(Vector3f angleVar) {
        this.angleVar = angleVar;
    }

}
