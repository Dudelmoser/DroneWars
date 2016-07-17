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
public class Aircraft {
    
    // aircraft variables
    private String path = "Reaper";
    private String name = "Ju 87 'Kanonenvogel'";
    private float speed = 40;
    private float agility = 5;
    private ColorRGBA laserColor = new ColorRGBA(1, 0, 0, 1);
    private ColorRGBA primaryColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
    private ColorRGBA secondaryColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
    
    // laser
    private transient final float laserWidth = 0.004f;
    private transient final float laserLength = 100;
    
    // references
    private transient AssetManager assetManager;
    private transient BulletAppState bullet;
    private transient RigidBodyControl control;
    private transient Node parent;
    private transient Geometry laser;
    private transient Spatial spatial;
    private transient HashSet<Spatial> xRotors = new HashSet();
    private transient HashSet<Spatial> yRotors = new HashSet();
    private transient HashSet<Spatial> zRotors = new HashSet();
    
    // flight variables
    private transient float throttle;
    private transient float pitch;
    private transient float roll;
    private transient float yaw;
    private transient float yawRotorSpeed;
    private transient float mainRotorSpeed;
    
    // runtime constants
    private transient float gravity;
    private transient float maxVelocity;
    private transient float activeLiftRate;
    private transient float passiveLiftRate;
    
    public Aircraft() {}
        
    public void create(Node parent, BulletAppState bullet, AssetManager assetManager) {
        this.parent = parent;
        this.bullet = bullet;
        this.assetManager = assetManager;
        
        createAircraft();
        createLaser();
        assignParts();
        initConstants();
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
    
    private void initConstants() {
        maxVelocity = 2 * speed;
        gravity = -control.getGravity().y;
        passiveLiftRate = 0.5f * control.getMass();
        activeLiftRate = yRotors.size() / (yRotors.size() + zRotors.size());
        agility = agility == 0 ? spatial.getWorldBound().getVolume() / 20 : agility;
    }
    
    private void createAircraft() {
        spatial = assetManager.loadModel("Aircraft/" + path + "/model.blend");
        spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        CollisionShape hitbox = CollisionShapeFactory.createDynamicMeshShape(spatial);
        control = new RigidBodyControl(hitbox);
        spatial.addControl(control);
        if (bullet != null)
            bullet.getPhysicsSpace().add(spatial);
        
        parent.attachChild(spatial);
    }
    
    private void createLaser() {
        Box box = new Box(laserWidth, laserWidth, laserLength);
        laser = new Geometry("Laser", box);
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-laserLength));
        parent.attachChild(laser);
        
        Material laserMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        laserMat.setColor("Color", laserColor);
        laserMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        laserMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        laser.setMaterial(laserMat);
    }
    
    private void assignParts() {
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
    
    public void update(float tpf) {
        spatial.updateLogicalState(tpf);

        // anti clipping
        if (throttle < 0)
            control.setPhysicsLocation(control.getPhysicsLocation().add(0,0.1f,0));
        
        Quaternion rot = spatial.getLocalRotation().clone();
        Vector3f rx = rot.mult(Vector3f.UNIT_X).normalize();
        Vector3f ry = rot.mult(Vector3f.UNIT_Y).normalize();
        Vector3f rz = rot.mult(Vector3f.UNIT_Z).normalize();

        applyAngularAirResistance();
        applyLinearAirResistance();
        applyAngularForces(rx, ry, rz);
        applyDrivingForces(ry, rz);
        applyPassiveLift(ry);
        
        updateRotors(tpf);        
        updateLaser();
    }
    
    private void updateLaser() {
        laser.setLocalTranslation(spatial.getLocalTranslation());
        laser.setLocalRotation(spatial.getLocalRotation());
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-laserLength));
    }
    
    private void applyPassiveLift(Vector3f ry) {
        float passiveLift = passiveLiftRate * getForwardVelocity();
        if (passiveLift > gravity) 
            passiveLift = gravity;
        control.applyCentralForce(ry.mult(passiveLift));
    }
    
    private void applyAngularForces(Vector3f rx, Vector3f ry, Vector3f rz) {
        Vector3f pitchForce = ry.mult(pitch * agility);
        Vector3f rollForce = rx.mult(-roll * agility);
        Vector3f yawForce = rz.mult(yaw * agility);
        control.applyForce(pitchForce, rz);
        control.applyForce(rollForce, ry);
        control.applyForce(yawForce, rx);
    }
    
    private void applyDrivingForces(Vector3f ry, Vector3f rz) {
        float impulse = throttle * speed;
        float thrust = (1 - activeLiftRate) * impulse;
        float lift = activeLiftRate * impulse;
        control.applyCentralForce(ry.mult(lift));
        control.applyCentralForce(rz.mult(-thrust));
    }
    
    private void applyLinearAirResistance() {
        Vector3f linVel = control.getLinearVelocity();
        float vel = linVel.length();
        float damping = (float) Math.pow(vel / maxVelocity, 4);
        Vector3f resist = linVel.negate().mult(damping);
        control.applyCentralForce(resist);
    }
    
    private void applyAngularAirResistance() {
        Vector3f spin = control.getAngularVelocity();
        control.applyForce(Vector3f.UNIT_Y.mult(spin.x), Vector3f.UNIT_Z);
        control.applyForce(Vector3f.UNIT_Z.mult(spin.y), Vector3f.UNIT_X);
        control.applyForce(Vector3f.UNIT_X.mult(spin.z), Vector3f.UNIT_Y);
    }
    
    private float getForwardVelocity() {
        Quaternion rot = spatial.getLocalRotation().clone();
        Vector3f rz = rot.mult(Vector3f.UNIT_Z).normalize();
        return rz.mult(control.getLinearVelocity().dot(rz) / rz.lengthSquared()).length();
    }
    
    private void updateRotors(float tpf) {
        mainRotorSpeed += (Math.abs(throttle) - mainRotorSpeed) * tpf;
        yawRotorSpeed += (Math.abs(yaw) - yawRotorSpeed) * tpf;
        
        for (Spatial rotor : xRotors) {
            rotor.rotate(yawRotorSpeed, 0, 0);
        }
        for (Spatial rotor : yRotors) {
            rotor.rotate(0, 0, mainRotorSpeed);
        }
        for (Spatial rotor : zRotors) {
            rotor.rotate(0, 0, mainRotorSpeed);
        }
    }
    
    private void setTextureScale(Spatial spatial, float scale) {
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
        
    public RigidBodyControl getControl() {
        return control;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getName() {
        return name;
    }
    
    public ColorRGBA getLaserColor() {
        return laserColor;
    }
    
    public void setLaserColor(ColorRGBA laserColor) {
        this.laserColor = laserColor;
    }

    public ColorRGBA getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(ColorRGBA shellColor) {
        this.primaryColor = shellColor;
    }

    public ColorRGBA getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(ColorRGBA rotorColor) {
        this.secondaryColor = rotorColor;
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
