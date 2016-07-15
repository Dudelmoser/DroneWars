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
public class TimeWidget {
    
    private Screen screen;
    private float displayX;
    private float displayY;
    private Label time;
    
    public TimeWidget(Screen screen, float displayX ,float displayY){
        this.screen = screen;
        this.displayX = displayX;
        this.displayY = displayY;
        
        createBackground();
        createClock();
        createTimeCounter(); 
    }

    private void createClock() {
        Element clock = new Element(screen, "Clock", Vector2f.ZERO, Vector2f.ZERO, Vector4f.ZERO, "/GUI/clock.png");
        clock.setWidth(40f);
        clock.setHeight(40f);
        clock.setPosition(displayX/2-clock.getWidth()*2-2, 6);
        screen.addElement(clock);
    }

    private void createTimeCounter() {
        time = new Label(screen, new Vector2f(100f,10f));      
        time.setText("03:00");
        time.setFontSize(40);
        time.setFontColor(new ColorRGBA(210f/255f,255f/255f,120f/255f, 1));
        time.setPosition(displayX/2-time.getWidth()/2+20, 0);
        screen.addElement(time);
    }
    
    public void createBackground(){
        Element background = new Element(screen, "back", Vector2f.ZERO, Vector2f.ZERO, Vector4f.ZERO, "GUI/hintergrund.png");
        background.setPosition(0,0);
        background.setHeight(50);
        background.setWidth(displayX);
        
        screen.addElement(background);
        
    }
    
    public void updateTime(String time){
       this.time.setText(time);       
    }
}
