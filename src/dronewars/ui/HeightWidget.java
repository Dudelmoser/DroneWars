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
public class HeightWidget {
    
    private Screen screen;
    private Element height;
    private Label zero;
    private Label twentyFive;
    private Label fifty;
    private Element arrow;
    private float displayX;
    private float displayY;
    
    private float LABEL_X = 10f;
    private float LABEL_SIZE = 30f;
    
    private float ARROW_SIZE = 40f;
    
    public HeightWidget(Screen screen, float displayX, float displayY){
       this.screen = screen;
       this.displayX = displayX;
       this.displayY = displayY;
        
       createHeightIndicator();
       createArrow();
       createLabels();       
    }

    /**
     * creates a heigh indicator from 0 meters to 100 meters
     */
    private void createHeightIndicator() {
       height = new Element(
                screen, "height", new Vector2f(200,200), new Vector2f(200,200), new Vector4f(5,5,5,5),"/GUI/height.png");
       height.setHeight(410);
       height.setWidth(50f);
       
       float heightIndicatorY = height.getHeight();
       height.setPosition(30, displayY-(heightIndicatorY+(displayY-heightIndicatorY)/2));
       screen.addElement(height);
    }

    /**
     * creates the indocator arrow
     */
    private void createArrow() {
       arrow = new Element(
                screen, "arrow", new Vector2f(200,200), new Vector2f(200,200), new Vector4f(5,5,5,5),"/GUI/arrow.png");
       arrow.setHeight(ARROW_SIZE);
       arrow.setWidth(ARROW_SIZE);
       arrow.setPosition(26+arrow.getWidth(), displayY-((displayY-height.getHeight()))/2-(arrow.getHeight()/2+7));
       screen.addElement(arrow);
    }

    /**
     * Initialize the lables for the height indicator
     */
    private void createLabels() {
       zero = new Label(screen, Vector2f.ZERO); 
       zero.setText("0");
       zero.setFontColor(new ColorRGBA(210f/255f,255f/255f,120f/255f, 1));
       zero.setHeight(LABEL_SIZE);
       zero.setWidth(LABEL_SIZE);
       zero.setPosition(LABEL_X, displayY-(displayY-height.getHeight())/2-(zero.getHeight()/2 + 15));
       
       twentyFive = new Label(screen, Vector2f.ZERO); 
       twentyFive.setText("25");
       twentyFive.setFontColor(new ColorRGBA(210f/255f,255f/255f,120f/255f, 1));
       twentyFive.setHeight(LABEL_SIZE-5);
       twentyFive.setWidth(LABEL_SIZE-5);
       twentyFive.setPosition(LABEL_X, displayY/2-(twentyFive.getHeight()/2));
       
       fifty = new Label(screen, Vector2f.ZERO); 
       fifty.setText("50");
       fifty.setFontColor(new ColorRGBA(210f/255f,255f/255f,120f/255f, 1));
       fifty.setHeight(LABEL_SIZE);
       fifty.setWidth(LABEL_SIZE);
       fifty.setPosition(LABEL_X, (displayY-height.getHeight())/2-10);
            
       screen.addElement(zero);
       screen.addElement(twentyFive);
       screen.addElement(fifty);
    }
    
    
    /**
     * Setter for the Arrow movement
     * 
     * @param newHeight 
     */
    public void setHeightArrow(float newHeight){       
        float ADD_NEW_POS = 80*(newHeight/10) +5;
        float ARROW_POS = (0+((displayY-height.getHeight())/2)-arrow.getHeight()/2) + ADD_NEW_POS;
        
        if(newHeight > 50){
            arrow.setY(ARROW_POS-4);
        }else{
            arrow.setY(ARROW_POS);
        }
    }
}
