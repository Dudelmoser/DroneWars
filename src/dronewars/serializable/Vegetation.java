package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Jan David Kleiß
 */
public class Vegetation {
    private ArrayList<Species> species = new ArrayList<>();
    private int seed = 1;
    
    private transient Node node;
    private transient Random rng;
    
    private transient BulletAppState bullet;
    
    public Vegetation() {}
        
    public void remove() {
        node.removeFromParent();
    }
    
    public void create(BufferedImage spawnMap, TerrainQuad terrain, 
            BulletAppState bullet, Node parent, AssetManager assetManager) {
        this.bullet = bullet;
        
        Model fern = new Model("Fern");
        fern.setScale(0.5f);
        Species ferns = new Species(fern, 0); 
        ferns.setDensity(5);
        species.add(ferns);
        
        Model tree = new Model("Tree");
        Species trees = new Species(tree, 2);
        trees.setDensity(5);
        species.add(trees);
        
        node = new Node("Vegetation");
        rng = new Random(seed);
        
        for (Species s : species) {
            if (s != null) {
                spawnSpecies(s, spawnMap, terrain, bullet, assetManager);
            }
        }
        parent.attachChild(node);
    }
    
    public ArrayList<Species> getSpecies() {
        return species;
    }
    
    public void spawnSpecies(Species species, BufferedImage spawnmap,
            TerrainQuad terrain, BulletAppState bullet, AssetManager assetManager) {
                
        int gridSize = spawnmap.getWidth() / (int)Math.pow(2, species.getDensity());
        int halfGrid = gridSize / 2;
        
        for (int x = halfGrid; x < spawnmap.getWidth(); x += gridSize) {
            for (int z = halfGrid; z < spawnmap.getWidth(); z += gridSize) {
                int color = spawnmap.getRGB(x, z);
                float chance = new java.awt.Color(color).getRGBComponents(null)[species.getChannel()];
                if (rng.nextFloat() <= chance) {
                    Spatial spatial = species.getRandomSpatial(assetManager, rng);
                    float px = x + halfGrid * rng.nextFloat() - 512;
                    float pz = z + halfGrid * rng.nextFloat() - 512;
                    float py = terrain.getHeight(new Vector2f(px, pz));
                    spatial.setLocalTranslation(px, py, pz);
                   
                    if (species.getModel().isSolid()) {
                        CollisionShape shape =  CollisionShapeFactory.createDynamicMeshShape(spatial);
                        RigidBodyControl control = new RigidBodyControl(shape);
                        control.setMass(0);
                        spatial.addControl(control);
                        if (bullet != null)
                            bullet.getPhysicsSpace().add(spatial);
                    }
                    
                    node.attachChild(spatial);
                }
            }
        }
    }
}
