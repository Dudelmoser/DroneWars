/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.ui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author Maria Gebauer
 */
public class Crosshair {
    
    private Screen screen;
    private float displayX;
    private float displayY;
    private Element crossHair;
    
    public Crosshair(Screen screen, float displayX, float displayY){
        this.screen = screen;
        this.displayX = displayX;
        this.displayY = displayY;
        
        createCrossHair();
    }

    private void createCrossHair() {
        crossHair = new Element(screen, "CrossHair", Vector2f.ZERO,
                Vector2f.ZERO, Vector4f.ZERO, "/GUI/crosshair.png");
        crossHair.setHeight(200);
        crossHair.setWidth(475);
        float posX = displayX/2- crossHair.getWidth()/2;
        float posY = displayY/2 -crossHair.getHeight()/2;
        crossHair.setPosition(posX, posY);
        screen.addElement(crossHair);
        crossHair.centerToParent();
    }
    
}
