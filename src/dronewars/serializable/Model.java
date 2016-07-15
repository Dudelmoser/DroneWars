package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Jan David Kleiß
 */
public class Model {
    private String name;
    private float scale = 1;
    private Vector3f rotation = new Vector3f();
    private Vector3f position = new Vector3f();
    private boolean solid = true;
    
    public Model() {}
    
    public Model(String name) {
        this.name = name;
    }
    
    @Override
    public Model clone() {
        Model clone = new Model(name);
        clone.setScale(scale);
        clone.setRotation(rotation.clone());
        clone.setPosition(rotation.clone());
        clone.setSolid(isSolid());
        return clone;
    }
    
    public Spatial getSpatial(AssetManager assetManager) {
        Spatial spatial = assetManager.loadModel("Models/" + name + "/model.j3o");
        spatial.setLocalScale(scale);
        Quaternion rot = getQuaternionFromDegrees(rotation.x, rotation.y, rotation.z);
        spatial.setLocalRotation(rot);
        spatial.setLocalTranslation(position);
        return spatial;
    }

    private Quaternion getQuaternionFromDegrees(double xAngle, double yAngle, double zAngle) {
        float[] angles = new float[]{
                  (float)Math.toRadians(xAngle), 
                  (float)Math.toRadians(yAngle),
                  (float)Math.toRadians(zAngle)
              };
        return new Quaternion().fromAngles(angles);
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @return the rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * @return the position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * @return the solid
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * @param solid the solid to set
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }
}
