package dronewars.main;

import java.awt.Color;

/**
 *
 * @author Jan David Kleiß
 */
public class GradientABGR {
    
    private final Color ALPHA = new Color(0, 0, 0, 0);
    
    private int[] heights = new int[8];
    private int[] ranges = new int[7];
    private Color[] colors = new Color[8];
    
    public GradientABGR(int[] stops) {
        heights[0] = 0;
        System.arraycopy(stops, 0, heights, 1, stops.length);
        heights[7] = 255;
        
        for (int i = 0; i < heights.length - 1; i++) {
            if (heights[i] >= heights[i + 1] || stops.length != 6) {
                System.out.println("Invalid height gradient parameters!");
                setDefaultGradient();
                break;
            }
        }
        setHeightRanges();
        setDefaultColors();
    }
    
    public GradientABGR(int min, int max) {
        double range = (max - min) / 6.0;
        for (int i = 1; i < 7; i++) {
            heights[i] = min + (int) Math.floor(range * i);
            System.out.println(heights[i]);
        }
        heights[7] = 255;
        setHeightRanges();
        setDefaultColors();
    }
    
    public GradientABGR() {
        setDefaultGradient();
        setHeightRanges();
        setDefaultColors();
    }
    
    private void setDefaultGradient() {
        double range = 255 / 7.0;
        for (int i = 0; i < 8; i++) {
            heights[i] = (int) Math.floor(range * i);
        }
    }
    
    private void setHeightRanges() {
        for (int i = 0; i < 7; i++) {
            ranges[i] = heights[i + 1] - heights[i];
        }
    }
    
    private void setDefaultColors() {
        colors[0] = colors[1] = ALPHA;
        colors[2] = colors[3] = Color.BLUE;
        colors[4] = colors[5] = Color.GREEN;
        colors[6] = colors[7] = Color.RED;
    }
    
    public Color getColorAt(int value) {
        for (int i = 1; i < 8; i++) {
            if (value <= heights[i]) {
                double f = (value - heights[i - 1]) / (double) ranges[i - 1];
                double a = (255 - colors[i].getAlpha()) * f + (255 - colors[i - 1].getAlpha()) * (1 - f);
                double b = colors[i].getBlue() * f + colors[i - 1].getBlue() * (1 - f);
                double g = colors[i].getGreen() * f + colors[i - 1].getGreen() * (1 - f);
                double r = colors[i].getRed() * f + colors[i - 1].getRed() * (1 - f);
                return new Color((int) r, (int) g, (int) b, (int) a);
            }
        }
        return ALPHA;
    }
}
