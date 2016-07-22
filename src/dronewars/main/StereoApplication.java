package dronewars.main;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public abstract class StereoApplication extends SimpleApplication {
    boolean stereo = false;
    FrameBuffer buffer1, buffer2;
    Texture2D tex1, tex2;
    Material stereoMat;
    Picture stereoPic;
    Camera cam2;
    ViewPort viewPort2;

    public void init() {
        System.out.println(speed);
        
        if (stereo) {
            buffer1 = new FrameBuffer(cam.getWidth(), cam.getHeight(), 1);
            tex1 = new Texture2D(cam.getWidth(), cam.getHeight(), Format.RGBA8);
            buffer1.setColorTexture(tex1);
            buffer1.setDepthBuffer(Format.Depth);
            viewPort.setOutputFrameBuffer(buffer1);

            cam2 = cam.clone();
            viewPort2 = renderManager.createMainView("Invisible", cam2);
            viewPort2.setClearFlags(true, true, true);
            viewPort2.attachScene(rootNode);
            buffer2 = new FrameBuffer(cam.getWidth(), cam.getHeight(), 1);
            tex2 = new Texture2D(cam.getWidth(), cam.getHeight(), Format.RGBA8);
            buffer2.setColorTexture(tex2);
            buffer2.setDepthBuffer(Format.Depth);
            viewPort2.setOutputFrameBuffer(buffer2);

            stereoPic = new Picture("result");
            stereoMat = new Material(assetManager, "MatDefs/Anaglyph/Anaglyph.j3md");
            stereoMat.setTexture("Texture1", tex1);
            stereoMat.setTexture("Texture2", tex2);
            stereoPic.setMaterial(stereoMat);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        if (stereo) {
            stereoPic.setWidth(cam.getWidth());
            stereoPic.setHeight(cam.getHeight());
            stereoPic.setPosition(0, 0);
            stereoPic.updateGeometricState();
            rm.setCamera(cam, true);
            rm.getRenderer().setFrameBuffer(null);
            rm.renderGeometry(stereoPic);
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if (stereo) {
            Vector3f cx = cam.getRotation().getRotationColumn(0);
            cam2.setFrustumFar(4096);
            cam2.setRotation(cam.getRotation());
            cam2.setLocation(cam.getLocation().clone().add(cx.normalize().mult(0.1f)));
        }
    }
    
    public void setStereo(boolean enabled) {
        stereo = enabled;
    }
}