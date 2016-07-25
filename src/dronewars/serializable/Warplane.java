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
import dronewars.main.Deserializer;
import dronewars.main.Serializer;
import dronewars.main.WarplaneControl;
import dronewars.main.Warzone;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Jan David Kleiß
 */
public class Warplane {
            
    private transient static final String type = "PLANE";

    private String name = "0";
    private ColorRGBA color = ColorRGBA.Gray;
    private ColorRGBA laserColor = ColorRGBA.Red;
    
    private transient final float laserLength = 100;
    private transient final float laserWidth = 0.004f;
    private transient final float maxStep = 100;
    
    private transient String uuid;
    private transient long[] tRec = new long[2];
    private transient long[] tSend = new long[2];
    private transient Vector3f[] pos = new Vector3f[2];
    private transient Vector3f[] vel = new Vector3f[2];
    private transient Quaternion[] rot = new Quaternion[2];
    // rotational velocity extrapolation missing
    
    private transient AssetManager assetManager;
    private transient Node parent;
    private transient Geometry laser;
    private transient Spatial spatial;
    private transient WarplaneControl control;
    private transient HashSet<Spatial> xRotors = new HashSet();
    private transient HashSet<Spatial> yRotors = new HashSet();
    private transient HashSet<Spatial> zRotors = new HashSet();
        
    public Warplane() {}
    
    public boolean equals(String uuid) {
        return this.uuid.equals(uuid);
    }
    
    public String serialize() {
//        return type + ";" + uuid + ";" + name + ";" + System.currentTimeMillis() 
//                + ";" + Serializer.fromVector(spatial.getLocalTranslation())
//                + ";" + Serializer.fromQuaternion(spatial.getLocalRotation());
        return type + ";" + uuid + ";" + name + ";" + System.currentTimeMillis() 
                + ";" + Serializer.fromVector(control.getPhysicsLocation())
                + ";" + Serializer.fromQuaternion(control.getPhysicsRotation());
//                + ";" + Serializer.from);
    }
       
    public void update(float tpf) {
        if (control != null) {
            spatial.updateLogicalState(tpf);
            control.refresh(tpf);
            updateLaser();
            updateRotors(control.getMainRotorSpeed(), control.getYawRotorSpeed());
        } else {
            interpolate();
            updateLaser();
            if (vel[0] != null)
                updateRotors(vel[0].length(), 0);
        }
    }
    
    public void update(String[] parts) {
        long tNew = Long.parseLong(parts[3]);
        if (tNew > tSend[0]) {
            tSend[1] = tSend[0];
            tSend[0] = tNew;
            tRec[1] = tRec[0];
            tRec[0] = System.currentTimeMillis();
            pos[1] = pos[0];
            pos[0] = Deserializer.toVector(parts[4]);
            rot[1] = rot[0];
            rot[0] = Deserializer.toQuaternion(parts[5]);
                        
            if (pos[1] != null) {
                vel[1] = vel[0];
                Vector3f newVel = pos[0].subtract(pos[1]).divide((tSend[0] - tSend[1]) / 1000f);
                if (newVel.length() <= vel[0].length() * 1.1f)
                    vel[0] = newVel;
            }
        }
    }
    
    private void interpolate() {
        if (pos[0] == null || pos[1] == null || vel[0].length() > maxStep)
            return;
        
        float fac = (System.currentTimeMillis() - tRec[0]) / (float) (tSend[0] - tSend[1]);
        if (fac > 1) {
            spatial.setLocalTranslation(pos[0].add(vel[0].mult(fac - 1)));
            spatial.setLocalRotation(new Quaternion().slerp(rot[0], rot[1], fac));
        } else {
            spatial.setLocalTranslation(pos[1].interpolate(pos[0], fac));
            spatial.setLocalRotation(new Quaternion().slerp(rot[0], rot[1], fac));
        }
    }
    
    public void updateLaser() {
        laser.setLocalTranslation(spatial.getLocalTranslation());
        laser.setLocalRotation(spatial.getLocalRotation());
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-laserLength));
    }
    
    public void updateRotors(float mainRotorSpeed, float yawRotorSpeed) {
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
    
    public float getLiftThrustRatio() {
        int rotorCount = yRotors.size() + zRotors.size();
        if (rotorCount == 0) {
            return 0;
        } else {
            return yRotors.size() / rotorCount;   
        }
    }
    
    public final void createActive(Warzone zone, BulletAppState bullet, AssetManager assetManager) {
        this.parent = zone.getNode();
        this.assetManager = assetManager;
        uuid = Integer.toString((int)(System.currentTimeMillis() & 0x00000000FFFFFFFFL));
        
        createSpatial();
        createLaser();
        assignParts();
        
        control = new WarplaneControl(this, zone);
        bullet.getPhysicsSpace().addCollisionListener(control);
        spatial.addControl(control);
        bullet.getPhysicsSpace().add(spatial);
    }
        
    public void createPassive(String[] serialized, Warzone zone, AssetManager assetManager) {
        this.parent = zone.getNode();
        this.assetManager = assetManager;
        uuid = serialized[1];
        name = serialized[2];
        
        createSpatial();
        createLaser();
        assignParts();
        
        CollisionShape shape = CollisionShapeFactory.createBoxShape(spatial);
        RigidBodyControl ctrl = new RigidBodyControl(shape);
        spatial.addControl(ctrl);
        zone.getBullet().getPhysicsSpace().add(spatial);
        ctrl.setKinematic(true);
        
        update(serialized);
    }
    
    public void createStatic(Node parent, AssetManager assetManager) {
        this.parent = parent;
        this.assetManager = assetManager;
        createSpatial();
        createLaser();
        assignParts();
    }
    
    public void remove() {
        if (spatial == null)
            return;
        parent.detachChild(spatial);
        parent.detachChild(laser);
    }
    
    private void createSpatial() {
        spatial = assetManager.loadModel(getClass().getSimpleName() + "s/" + name + "/model.blend");
        spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
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
            if (name.contains("color")) {
                Geometry geom = (Geometry) ((Node) child).getChild(0);
                geom.getMaterial().setColor("Diffuse", color);
            }
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
    
    public String getUuid() {
        return uuid;
    }
        
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Spatial getSpatial() {
        return spatial;
    }
    
    public WarplaneControl getControl() {
        return control;
    }
    
    public CollisionShape getCollisionShape() {
        return CollisionShapeFactory.createBoxShape(spatial);
    }
    
    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
    
    public ColorRGBA getLaserColor() {
        return laserColor;
    }
    
    public void setLaserColor(ColorRGBA laserColor) {
        this.laserColor = laserColor;
    }
}
