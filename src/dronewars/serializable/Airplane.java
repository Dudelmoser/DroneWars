package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
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
public class Airplane {
    
    private String path = "Dragonfire";
    private String name = "Ju 87 'Kanonenvogel'";
    private ColorRGBA laserColor = new ColorRGBA(1, 0, 0, 1);
    private ColorRGBA primaryColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
    private ColorRGBA secondaryColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
    
    private transient final float laserLength = 100;
    private transient final float laserWidth = 0.004f;
    
    private transient AssetManager assetManager;
    private transient Node parent;
    private transient Geometry laser;
    private transient Spatial spatial;
    private transient HashSet<Spatial> xRotors = new HashSet();
    private transient HashSet<Spatial> yRotors = new HashSet();
    private transient HashSet<Spatial> zRotors = new HashSet();
    
    public Airplane() {}
        
    public void create(Node parent, AssetManager assetManager) {
        this.parent = parent;
        this.assetManager = assetManager;
        
        createAirplane();
        createLaser();
        assignParts();
    }
    
    public void destroy() {
        parent.detachChild(spatial);
        parent.detachChild(laser);
    }
    
    private void createAirplane() {
        spatial = assetManager.loadModel("Airplanes/" + path + "/model.blend");
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
            if (name.contains("primary")) {
                Geometry geom = (Geometry) ((Node) child).getChild(0);
                geom.getMaterial().setColor("Diffuse", primaryColor);
            } else if (name.contains("secondary")) {
                Geometry geom = (Geometry) ((Node) child).getChild(0);
                geom.getMaterial().setColor("Diffuse", secondaryColor);
            }
        }
    }
    
    public void update(float tpf, float mainRotorSpeed, float yawRotorSpeed) {
        updateRotors(mainRotorSpeed, yawRotorSpeed);
        updateLaser();
    }
    
    private void updateLaser() {
        laser.setLocalTranslation(spatial.getLocalTranslation());
        laser.setLocalRotation(spatial.getLocalRotation());
        Vector3f forward = spatial.getLocalRotation().getRotationColumn(2);
        laser.move(forward.mult(-laserLength));
    }
    
    private void updateRotors(float mainRotorSpeed, float yawRotorSpeed) {        
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
    
    public float getLiftThrustRatio() {
        int rotorCount = yRotors.size() + zRotors.size();
        if (rotorCount == 0) {
            return 0;
        } else {
            return yRotors.size() / rotorCount;   
        }
    }
    
    public CollisionShape getCollisionShape() {
        return CollisionShapeFactory.createBoxShape(spatial);
    }
        
    public Spatial getSpatial() {
        return spatial;
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
}
