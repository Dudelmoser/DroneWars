/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Jan David Kleiß
 */
public class JsonFactory {
    
    public static <T> T load(String relativePath, Class<T> type) {
        Path path = Paths.get(System.getProperty("user.dir") + "/" + relativePath);
        try {
            String json = new String(Files.readAllBytes(path));
            return new GsonBuilder().create().fromJson(json, type);
        } catch (IOException ioe) {
            try {
                return type.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
    
    public static <T> void save(String relativePath, T obj) {
        String absPath = System.getProperty("user.dir") + "/" + relativePath;
        String json = new GsonBuilder().create().toJson(obj);
        
        try (PrintWriter out = new PrintWriter(absPath)) {
            out.println(json);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
