package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author Jan David Kleiß
 */
public class Drone {
    
    private float mass = 1;
    
    private float scale = 0.2f;
    private String model = "Drones/Default/Drone.blend";
    
    private ColorRGBA laserColor = new ColorRGBA(0.8f, 1, 0, 1);
    private ColorRGBA rotorColor = new ColorRGBA(0.8f, 1, 0, 1);
    private ColorRGBA shellColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 1);
    
    private transient AssetManager assetManager;
    private transient BulletAppState bullet;
    private transient RigidBodyControl control;
    private transient Node parent;
    private transient Geometry laser;
    private transient Spatial spatial;
    private transient Spatial rotorFR, rotorFL, rotorBR, rotorBL;
    
    private transient float throttle;
    private transient float pitch;
    private transient float roll;
    private transient float yaw;
    
    public Drone() {}
        
    public void create(Node parent, BulletAppState bullet, AssetManager assetManager) {
        this.parent = parent;
        this.bullet = bullet;
        this.assetManager = assetManager;
        
        spatial = assetManager.loadModel(model);
        spatial.scale(scale);
        spatial.setShadowMode(RenderQueue.ShadowMode.Cast);
        
        CollisionShape hitbox = CollisionShapeFactory.createDynamicMeshShape(spatial);
        control = new RigidBodyControl(hitbox);
        spatial.addControl(control);
        control.setMass(mass);
        
        if (bullet != null)
            bullet.getPhysicsSpace().add(spatial);
        
        parent.attachChild(spatial);
        
        Box box = new Box(0.004f, 0.004f, 100);
        laser = new Geometry("Laser", box);
        
        Vector3f upward = spatial.getLocalRotation().getRotationColumn(1);
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-100.1f));
        parent.attachChild(laser);
        
        initMaterials();
    }
    
    public void destroy() {
        parent.detachChild(spatial);
        parent.detachChild(laser);
        bullet.getPhysicsSpace().remove(spatial);
        assetManager = null;
        bullet = null;
        control = null;
        parent = null;
    }
    
    private void initMaterials() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", laserColor);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        laser.setMaterial(mat);
        
        Node drone = (Node) spatial;
        
        Spatial laserMesh = drone.getChild("Laser");
        Material laserMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        laserMat.setColor("Color", laserColor);
        laserMesh.setMaterial(laserMat);
                
        Spatial shellMesh = drone.getChild("Shell");
        Material shellMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        shellMat.setColor("Diffuse", shellColor);
        shellMat.setBoolean("UseMaterialColors", true);
        shellMesh.setMaterial(shellMat);
        
        Material rotorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        rotorMat.setColor("Diffuse", rotorColor);
        rotorMat.setBoolean("UseMaterialColors", true);
        
        rotorFR = drone.getChild("RotorFR");
        rotorFL = drone.getChild("RotorFL");
        rotorBR = drone.getChild("RotorBR");
        rotorBL = drone.getChild("RotorBL");
        
        rotorFR.setMaterial(rotorMat);
        rotorFL.setMaterial(rotorMat);
        rotorBR.setMaterial(rotorMat);
        rotorBL.setMaterial(rotorMat);
    }
    
    public void setTextureScale(Spatial spatial, float scale) {
        if (spatial instanceof Node) {
            Node findingnode = (Node) spatial;
            for (int i = 0; i < findingnode.getQuantity(); i++) {
                Spatial child = findingnode.getChild(i);
                setTextureScale(child, scale);
            }
        } else if (spatial instanceof Geometry) {
            Mesh mesh = ((Geometry) spatial).getMesh();
            mesh.scaleTextureCoordinates(new Vector2f(scale, scale));
        }
    }
    
    public void update(float tpf) {
        spatial.updateLogicalState(tpf);
        laser.setLocalTranslation(spatial.getLocalTranslation());
        laser.setLocalRotation(spatial.getLocalRotation());
        Vector3f upward = spatial.getLocalRotation().getRotationColumn(1);
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-100.1f));
        laser.move(upward.mult(0.05f));
        
        Quaternion rot = spatial.getLocalRotation().clone();
        Vector3f rx = rot.mult(Vector3f.UNIT_X).normalize();
        Vector3f ry = rot.mult(Vector3f.UNIT_Y).normalize();
        Vector3f rz = rot.mult(Vector3f.UNIT_Z).normalize();

        Vector3f pitchForce = ry.mult(pitch / 4);
        Vector3f rollForce = ry.mult(roll / 4);
        Vector3f yawForce = rz.mult(yaw / 8);
        
        final float MAX_SPIN = 1;
        Vector3f spin = control.getAngularVelocity().negate();
                
        if (!(spin.x > MAX_SPIN && pitch > 0
              || spin.x < -MAX_SPIN && pitch < 0)) {
            control.applyForce(pitchForce, rz);
        }
        if (!(spin.z > MAX_SPIN && roll > 0
              || spin.z < -MAX_SPIN && roll < 0)) {
            control.applyForce(rollForce, rx);
        }
        if (!(spin.y > MAX_SPIN && yaw > 0
              || spin.y < -MAX_SPIN && yaw < 0)) {
            control.applyForce(yawForce, rx);
        }
        
        if (throttle < 0) {
            control.setPhysicsLocation(control.getPhysicsLocation().add(0,0.1f,0));
        }
        
        control.applyCentralForce(ry.mult(throttle * (-control.getGravity().y + 20)));
        control.applyCentralForce(control.getLinearVelocity().negate());
        control.applyForce(pitchForce, rz);
        control.applyForce(rollForce, rx);
        control.applyForce(yawForce, rx);
        
        // should be dependent on input
        rotorFR.rotate(0,0,1);
        rotorFL.rotate(0,0,1);
        rotorBR.rotate(0,0,1);
        rotorBL.rotate(0,0,1);
    }
    
    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }
    
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public void setRoll(float roll) {
        this.roll = roll;
    }
    
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    
    public void setMass(float mass) {
        this.mass = mass;
    }
        
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return model;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.model = path;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * @return the laserColor
     */
    public ColorRGBA getLaserColor() {
        return laserColor;
    }

    /**
     * @param laserColor the laserColor to set
     */
    public void setLaserColor(ColorRGBA laserColor) {
        this.laserColor = laserColor;
    }

    /**
     * @return the shellColor
     */
    public ColorRGBA getShellColor() {
        return shellColor;
    }

    /**
     * @param shellColor the shellColor to set
     */
    public void setShellColor(ColorRGBA shellColor) {
        this.shellColor = shellColor;
    }

    /**
     * @return the rotorColor
     */
    public ColorRGBA getRotorColor() {
        return rotorColor;
    }

    /**
     * @param rotorColor the rotorColor to set
     */
    public void setRotorColor(ColorRGBA rotorColor) {
        this.rotorColor = rotorColor;
    }

    /**
     * @return the laser
     */
    public Geometry getLaser() {
        return laser;
    }

    /**
     * @param laser the laser to set
     */
    public void setLaser(Geometry laser) {
        this.laser = laser;
    }

    /**
     * @return the control
     */
    public RigidBodyControl getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(RigidBodyControl control) {
        this.control = control;
    }

    /**
     * @param spatial the spatial to set
     */
    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    public void fire() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void missile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void flare() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void stabilize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
