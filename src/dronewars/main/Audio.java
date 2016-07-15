package dronewars.main;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author Jan David Kleiß
 */
public class Audio {
    private static final String[] IDS = {"shoot", "jam", "hit", "lockon",
                                         "rotor", "rotor2", "ambient",
                                         "draw", "lost", "won"};
    private static final String DIR = "Sounds/";
    private HashMap<String, AudioNode> sounds;
    
    public Audio(SimpleApplication app) {
        Node root = app.getRootNode();
        AssetManager am = app.getAssetManager();
        sounds = new HashMap();
        
        for (String id : IDS) {
            AudioNode sound = new AudioNode(am, DIR + id + ".wav", false);
            sound.setPositional(false);
            root.attachChild(sound);
            sounds.put(id, sound);   
        }
    }
    
    public void play(String id) {
        sounds.get(id).play();
    }
    
    public void stop(String id) {
        sounds.get(id).stop();
    }
    
    public void setLooping(String id) {
        sounds.get(id).setLooping(true);
    }
    
    public void setVolume(String id, float volume) {
        sounds.get(id).setVolume(volume);
    }
}
