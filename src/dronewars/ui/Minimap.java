/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.ui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.text.Label;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author Maria Gebauer
 */
public class Minimap {
    
    private Screen screen;
    private final int ICON_SIZE = 20;
    private final int CIRCLE_SIZE = 200;
    private float displayX;
    private float displayY;
    private float startX;
    private float startY;
    
    private Element circle;
    private Element enemy;
    private Element player;
    
    public Minimap(Screen screen, float displayX, float displayY){
        this.screen = screen;
        this.displayX = displayX;
        this.displayY = displayY;
        this.startX = displayX - CIRCLE_SIZE/2 - ICON_SIZE/2;
        this.startY = CIRCLE_SIZE/2 - ICON_SIZE/2;
        
        createCircle();
        createPlayers();
        createNorthLabel();
    }

    private void createCircle() {
        circle = new Element(screen, "minimap", new Vector2f(200,200), new Vector2f(200,200), new Vector4f(5,5,5,5),"/GUI/minimap.png");
        circle.setHeight(CIRCLE_SIZE);
        circle.setWidth(CIRCLE_SIZE);
        circle.setPosition(displayX - circle.getHeight(), displayY - circle.getHeight());
        screen.addElement(circle);
    }

    private void createPlayers() {
        enemy = new Element(screen, "enemy", new Vector2f(200,200), 
                new Vector2f(200,200), new Vector4f(5,5,5,5), "/GUI/enemy.png");
        enemy.setHeight(ICON_SIZE);
        enemy.setWidth(ICON_SIZE);
        enemy.setPosition(startX, startY);
        screen.addElement(enemy);
        
        player = new Element(screen, "player",new Vector2f(200,200), 
                new Vector2f(200,200), new Vector4f(5,5,5,5), "/GUI/player.png" );
        player.setHeight(ICON_SIZE);
        player.setWidth(ICON_SIZE);
        player.setPosition(startX, startY);
        screen.addElement(player);
        
    }
    
    private void createNorthLabel() {
        Label north = new Label(screen, Vector2f.ZERO);
        north.setWidth(10);
        north.setHeight(10);
        north.setPosition(displayX-circle.getWidth() / 2 - north.getWidth(), 
                displayY-circle.getHeight() - north.getHeight() * 2);
        north.setText("N");
        north.setFontColor(new ColorRGBA(210f/255f, 1, 120f/255f, 1));
        north.setFontSize(30);
        north.setGlobalAlpha(70);
        
        screen.addElement(north);
    }
    
    
    public void updatePlayer(float x, float y) {
//        Quaternion rot = new Quaternion();
//        rot.fromAngleAxis((float)Math.toRadians(compass), Vector3f.UNIT_Z);
//        player.setLocalRotation(rot);
        
        player.setPosition(startX + x, startY + y);
    }
    
    public void updateEnemy(float x, float y) {  
//        Quaternion rot = new Quaternion();
//        rot.fromAngleAxis((float)Math.toRadians(compass), Vector3f.UNIT_Z);
//        player.setLocalRotation(rot);
        
        enemy.setPosition(startX + x, startY + y);
    }
}
