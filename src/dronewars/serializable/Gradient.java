package dronewars.serializable;

import java.awt.Color;

/**
 *
 * @author Jan David Kleiß
 */
public class Gradient {
        
    private static final Color ALPHA = new Color(0, 0, 0, 0);
    private static final Color[] COLORS = {ALPHA, ALPHA, Color.BLUE, Color.BLUE, 
                                Color.GREEN, Color.GREEN, Color.RED, Color.RED};
    
    private int[] heights = {0, 36, 36*2, 36*3, 36*4, 36*5, 36*6, 255};
     
    public Gradient() {}
    
    public Color getColorAt(int height) {
        for (int i = 1; i < heights.length; i++) {
            if (height <= heights[i]) {
                double f = (height - heights[i - 1]) / (double) (heights[i] - heights[i - 1]);
                double a = (255 - COLORS[i].getAlpha()) * f + (255 - COLORS[i - 1].getAlpha()) * (1 - f);
                double b = COLORS[i].getBlue() * f + COLORS[i - 1].getBlue() * (1 - f);
                double g = COLORS[i].getGreen() * f + COLORS[i - 1].getGreen() * (1 - f);
                double r = COLORS[i].getRed() * f + COLORS[i - 1].getRed() * (1 - f);
                return new Color((int) r, (int) g, (int) b, (int) a);
            }
        }
        return ALPHA;
    }
    
    public int[] getHeights() {
        return heights;
    }
}
