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
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Jan David Kleiß
 */
public class Drone {
    
    private String name = "Reaper";
    
    private ColorRGBA laserColor = new ColorRGBA(0.8f, 1, 0, 1);
    private ColorRGBA primaryColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 1);
    private ColorRGBA secondaryColor = new ColorRGBA(0.8f, 1, 0, 1);
        
    private transient AssetManager assetManager;
    private transient BulletAppState bullet;
    private transient RigidBodyControl control;
    private transient Node parent;
    private transient Geometry laser;
    private transient Spatial spatial;
    
    private transient HashSet<Spatial> xRotors;
    private transient HashSet<Spatial> yRotors;
    private transient HashSet<Spatial> zRotors;
    
    private transient float throttle;
    private transient float pitch;
    private transient float roll;
    private transient float yaw;
    
    private transient float agility = 1;
    private transient float speed = 0;
    private transient float yawSpeed = 0;
    
    public Drone() {}
        
    public void create(Node parent, BulletAppState bullet, AssetManager assetManager) {
        this.parent = parent;
        this.bullet = bullet;
        this.assetManager = assetManager;
        
        xRotors = new HashSet();
        yRotors = new HashSet();
        zRotors = new HashSet();
        
        spatial = assetManager.loadModel("Aircraft/" + name + "/model.blend");
        spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        agility = spatial.getWorldBound().getVolume() / 20;
        
        CollisionShape hitbox = CollisionShapeFactory.createDynamicMeshShape(spatial);
        control = new RigidBodyControl(hitbox);
        spatial.addControl(control);
        
        if (bullet != null)
            bullet.getPhysicsSpace().add(spatial);
        
        parent.attachChild(spatial);
        
        Box box = new Box(0.004f, 0.004f, 100);
        laser = new Geometry("Laser", box);
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-100));
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
        Material laserMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        laserMat.setColor("Color", laserColor);
        laserMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        laserMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        laser.setMaterial(laserMat);
        
        Node node = (Node) spatial;
        List<Spatial> children = node.getChildren();
        
        for (Spatial child : children) {
            String name = child.getName().toLowerCase();
            if (name.contains("xrotor")) {
                xRotors.add(child);
            } else if (name.contains("yrotor")) {
                yRotors.add(child);
            } else if (name.contains("zrotor")) {
                zRotors.add(child);
            }
            if (name.contains("primary")) {
                Geometry geom = (Geometry) ((Node) child).getChild(0);
                geom.getMaterial().setColor("Diffuse", primaryColor);
            } else if (name.contains("secondary")) {
                Geometry geom = (Geometry) ((Node) child).getChild(0);
                geom.getMaterial().setColor("Diffuse", secondaryColor);
            }
        }
        
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
        
        Vector3f pitchForce = ry.mult(pitch * agility);
        Vector3f rollForce = ry.mult(roll * agility);
        Vector3f yawForce = rz.mult(yaw * agility / 2);
        
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
        spinRotors(tpf);
    }
    
    private void spinRotors(float tpf) {
        speed += (Math.abs(throttle) - speed) * tpf;
        yawSpeed += (Math.abs(yaw) - yawSpeed) * tpf;
        
        for (Spatial rotor : xRotors) {
            rotor.rotate(yawSpeed, 0, 0);
        }
        for (Spatial rotor : yRotors) {
            rotor.rotate(0, 0, speed);
        }
        for (Spatial rotor : zRotors) {
            rotor.rotate(0, 0, speed);
        }
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
        
    public Spatial getSpatial() {
        return spatial;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
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
    public ColorRGBA getPrimaryColor() {
        return primaryColor;
    }

    /**
     * @param shellColor the shellColor to set
     */
    public void setPrimaryColor(ColorRGBA shellColor) {
        this.primaryColor = shellColor;
    }

    /**
     * @return the rotorColor
     */
    public ColorRGBA getSecondaryColor() {
        return secondaryColor;
    }

    /**
     * @param rotorColor the rotorColor to set
     */
    public void setSecondaryColor(ColorRGBA rotorColor) {
        this.secondaryColor = rotorColor;
    }

    /**
     * @return the control
     */
    public RigidBodyControl getControl() {
        return control;
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
