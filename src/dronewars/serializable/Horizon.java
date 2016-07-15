package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 *
 * @author Jan David Kleiß
 */
public class Horizon {
    
    private String name = "default";
    private float height = 1;
    private float distance = 1;
    private int smoothing = 0;
    private boolean wire = false;
    private ColorRGBA fogColor = ColorRGBA.BlackNoAlpha;
        
    private transient final int baseSize = 8192;
    private transient final int patchSize = 128;
    private transient final int texScale = 128;
    private transient final String alphaPath = "Horizons/alpha.png";
    private transient final String texPath = "Textures/Sand/diffuse.jpg";
    private transient final String matPath = "MatDefs/Horizon/Horizon.j3md";
    
    private transient TerrainQuad horizon;

    public Horizon() {}
        
    public void create(Node node, AssetManager assetManager) {
        Texture tex = assetManager.loadTexture("Horizons/" + name + ".png");
        AbstractHeightMap heightmap = new ImageBasedHeightMap(tex.getImage());
        heightmap.load();
        if (smoothing != 0)
            heightmap.smooth(1, smoothing);
        
        int size = tex.getImage().getWidth();
        float[] map = heightmap.getHeightMap();

        horizon = new TerrainQuad("Horizon", patchSize + 1, size + 1, map);
        Material mat = new Material(assetManager, matPath);
        mat.setTexture("AlphaMap", assetManager.loadTexture(alphaPath));
        
        Texture diffuse = assetManager.loadTexture(texPath);
        diffuse.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", diffuse);
        mat.setFloat("DiffuseMap_0_scale", texScale);
        mat.setTexture("DiffuseMap_1", diffuse);
        mat.setFloat("DiffuseMap_1_scale", texScale);
        mat.setTexture("DiffuseMap_2", diffuse);
        mat.setFloat("DiffuseMap_2_scale", texScale);
        
        mat.setColor("GlowColor", fogColor);
        
        float xy = baseSize / size * distance;
        horizon.setMaterial(mat);
        horizon.setLocalScale(new Vector3f(xy, height, xy));
        horizon.setShadowMode(RenderQueue.ShadowMode.Receive);
        
        if (wire)
            mat.getAdditionalRenderState().setWireframe(wire);
        
        node.attachChild(horizon);
    }
    
    public void setFogColor(ColorRGBA fogColor) {
        horizon.getMaterial().setColor("GlowColor", fogColor);
    }
}
