/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jhlabs.image.GaussianFilter;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author Jan David Kleiß
 */
public class ImageFactory {
    private static String format = "png";
    
    public static void setFormat(String format) {
        ImageFactory.format = format;
    }
    
    public static String getCwd() {
        return System.getProperty("user.dir");
    }
    
    public static BufferedImage load(String path) throws IOException {
        return ImageIO.read(Files.newInputStream(Paths.get(path)));
    }
    
    public static void save(BufferedImage image, String path) throws IOException {
        ImageIO.write(image, "png", new File(path));
    }

    public static BufferedImage toBufferedImage(Image image) {
        return ImageToAwt.convert(image, false, true, 0);
    }
    
    public static Image toImage(BufferedImage image) {
        AWTLoader loader = new AWTLoader();
        return loader.load(image, true);
    }
    
    public static BufferedImage clone(BufferedImage image) {
        ColorModel model = image.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(model, raster, isAlphaPremultiplied, null);
    }
    
    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resized.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }
    
    public static BufferedImage blur(BufferedImage image, int intensity) {
        BufferedImage blurred = clone(image);
        new GaussianFilter(intensity).filter(image, blurred);
        return blurred;
    }
    
    public static ColorRGBA getAverageColor(BufferedImage image) {
        BufferedImage pixel = resize(image, 1, 1);
        int avgCol = pixel.getRGB(0, 0);
        Color rgb = new Color(avgCol);
        return new ColorRGBA(rgb.getRed() / 255f, rgb.getGreen() / 255f, 
                rgb.getBlue() / 255f, rgb.getAlpha() / 255f);
    }
    
    public static ColorRGBA getSkyColor(String name) {
        ColorRGBA sum = new ColorRGBA(0, 0, 0, 0);
        String path = getCwd() + "/assets/Skies/" + name;
        String[] sides = {"up", "down", "east", "south", "west", "north"};
        for (String side : sides) {
            BufferedImage sideImg;
            try {
                sideImg = load(path + "/" + side + "." + format);
                ColorRGBA sideAvg = getAverageColor(sideImg);
                sum.r += sideAvg.r;
                sum.g += sideAvg.g;
                sum.b += sideAvg.b;
                sum.a += sideAvg.a;
            } catch (IOException ex) {
                Logger.getLogger(ImageFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sum.mult(1 / 6f);
    }
    
    public static Image getAlphaMap(BufferedImage heightMap, GradientABGR gradient) {
        int width = heightMap.getWidth();
        int height = heightMap.getHeight();

        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {

                Color h = new Color(heightMap.getRGB(z, x));
                Color c = gradient.getColorAt(h.getRed());

                data.put((byte) c.getRed())
                    .put((byte) c.getGreen())
                    .put((byte) c.getBlue())
                    .put((byte) c.getAlpha());
            }
        }
                
        return new Image(Image.Format.RGBA8, width, height, data);
    }
    
    public static void main(String[] args) {
        ColorRGBA avg2 = getSkyColor("Cloudy45");
        System.out.println(avg2);
        
        String json = new GsonBuilder().create().toJson(avg2);
        System.out.println(json);
    }
}
