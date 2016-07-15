package dronewars.ui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import dronewars.main.PlayerState;

/**
 *
 * @author Jan David Kleiß
 */
public class Overlay {
    private String path = "Textures/hud.png";
    
    private transient Geometry geom;
    private transient Material mat;
    private transient Texture tex;

    public Overlay() {}
    
    public void create(PlayerState scene) {
        createOverlay(scene.getGuiNode(), scene.getCamera(), scene.getAssetManager());
    }
    
    public void createOverlay(Node node, Camera cam, AssetManager assetManager) {
        tex = assetManager.loadTexture(path);
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Quad quad = new Quad(cam.getWidth(), cam.getHeight());
        geom = new Geometry("Overlay", quad);
        geom.setMaterial(mat);
        node.attachChild(geom);
    }
}
