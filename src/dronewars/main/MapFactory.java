package dronewars.main;

import com.jhlabs.image.GaussianFilter;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.awt.Color;
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
public class MapFactory {
    private static final String MAP_DIR = "/assets/Maps/";

    public static String getMapPath(String name, String type) {
        return System.getProperty("user.dir") + MAP_DIR + name + "/" + type + ".png";
    }
    
    public static BufferedImage loadBufferedImage(String path) {
        try {
            return ImageIO.read(Files.newInputStream(Paths.get(path)));
        } catch (IOException ex) {
            Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static boolean saveBufferedImage(BufferedImage image, String path) {
        BufferedImage blurred = deepCopy(image);
        new GaussianFilter(6).filter(image, blurred);
        try {
            return ImageIO.write(blurred, "png", new File(path));
        } catch (IOException ex) {
            Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    public static void saveImage(Image image, String path) {
        saveBufferedImage(fromImage(image), path);
    }

    public static BufferedImage fromImage(Image image) {
        return ImageToAwt.convert(image, false, true, 0);
    }
    
    public static Image createAlphamap(BufferedImage heightmap, GradientABGR gradient) {
        int width = heightmap.getWidth();
        int height = heightmap.getHeight();

        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {

                Color h = new Color(heightmap.getRGB(z, x));
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
        String sourcePath = System.getProperty("user.dir") + "/assets/Maps/New/height.png";
        GradientABGR gradient = new GradientABGR(new int[]{75, 86, 87, 130, 131, 200});
        BufferedImage heightmap = MapFactory.loadBufferedImage(sourcePath);
        
        String destPath = MapFactory.getMapPath("Default", "alpha");
        Image alphamap = MapFactory.createAlphamap(heightmap, gradient);
        MapFactory.saveImage(alphamap, destPath);
    }
}
