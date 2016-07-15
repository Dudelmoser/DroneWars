package dronewars.ui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * The Health indicator Class generates a Health indicator for the player (green) and players enemy (orange).
 *
 * @author Maria Gebauer
 */
public class HealthWidget {
 
    private Screen screen;
    private Indicator ownHealth;
    private Indicator enemyHealth;
    
    /**
     * Constructor
     * @param screen 
     */
    public HealthWidget(Screen screen){
       this.screen = screen;
       
       generateOwnHealthIndicator();
       generateEnemyHealthIndicator();
    }

    /**
     * Generates the Helath indicator For the player
     */
    private void generateOwnHealthIndicator() {
        ownHealth = new Indicator(screen, "OwnHealth", new Vector2f(20, 10),
                Element.Orientation.HORIZONTAL) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        }; 
        
        ownHealth.setIndicatorColor(Colors.PRIMARY.mult(Colors.SEMITRANS));
        ownHealth.setMaxValue(100);
        ownHealth.setLocalScale(1.5f);
        ownHealth.setWidth(220);
        ownHealth.setHeight(30);
        ownHealth.setDisplayPercentage();
        ownHealth.setIndicatorPadding(new Vector4f(4,2,4,2));
        ownHealth.setCurrentValue(100);
        ownHealth.setFontColor(ColorRGBA.Black);
        ownHealth.setPosition(screen.getWidth()/2-(ownHealth.getWidth()/2),
                screen.getHeight()-(ownHealth.getHeight()));
        
        screen.addElement(ownHealth);
    }

    /**
     * Generates the Health indicator for the players enemy
     */
    private void generateEnemyHealthIndicator() {
        enemyHealth = new Indicator(screen, "EnemyHealth", new Vector2f(5, 5),
                Element.Orientation.HORIZONTAL) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
        
        enemyHealth.setMaxValue(100);
        enemyHealth.setDisplayPercentage();
        enemyHealth.setIndicatorPadding(new Vector4f(2,2,2,2));
        enemyHealth.setIndicatorColor(Colors.SECONDARY);
        enemyHealth.setCurrentValue(100);
        enemyHealth.setPosition(screen.getWidth()/2-(enemyHealth.getWidth()/2),
                screen.getHeight()-(ownHealth.getHeight()+enemyHealth.getHeight()));
        
        screen.addElement(enemyHealth);
    }
    
    
    /**
     * Setters for Current Value of Indicators
     * 
     * @param currentValue 
     */
    public void setOwnHealth(float currentValue){
        ownHealth.setCurrentValue(currentValue);
    }
    
    public void setEnemyHealth(float currentValue){
        enemyHealth.setCurrentValue(currentValue);
    }
    
}
