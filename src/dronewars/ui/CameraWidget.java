package dronewars.ui;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.text.DecimalFormat;

/**
 *
 * @author Jan David Kleiß
 */
public class CameraWidget {
    private Node guiNode;
    private BitmapFont guiFont;
    private BitmapText camPos;
    
    public CameraWidget(SimpleApplication app) {
        this.guiNode = app.getGuiNode();
        this.guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        init();
    }
    
    public void update(Camera cam) {
            DecimalFormat format = new DecimalFormat("'+'00;' -'00");
            DecimalFormat format2 = new DecimalFormat("'+'00.00;' -'00.00");
            float[] angles = new float[3];
            cam.getRotation().toAngles(angles);
            camPos.setText("p=("
                    + format.format(cam.getLocation().x) + " | "
                    + format.format(cam.getLocation().y) + " | "
                    + format.format(cam.getLocation().z) + ") r=("
                    + format2.format(angles[0]) + " | "
                    + format2.format(angles[1]) + " | "
                    + format2.format(angles[2]) + ")");
    }
    
    private void init() {
        camPos = new BitmapText(guiFont, false);
        camPos.setSize(guiFont.getCharSet().getRenderedSize());
        camPos.setColor(Colors.PRIMARY);
        camPos.setLocalTranslation(300, 2*camPos.getLineHeight(), 0);
        guiNode.attachChild(camPos);
    }
}
