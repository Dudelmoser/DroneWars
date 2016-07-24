package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.LowPassFilter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

/**
 *
 * @author Jan David Kleiß
 */
public class Water {
    private boolean enabled = true;
    
    private ColorRGBA color = new ColorRGBA(0.1f, 0.2f, 0.3f, 0.1f);
    private Vector3f filter = new Vector3f(127, 127, 127);
    
    private float clarity = 50;
    private float reflectivity = 0.5f;
    private float causticIntensity = 0.5f;
    private float waveAmplitude = 1f;
    
    private float level = 23.5f;
    private float levelVariance = 0.5f;
    private float tideDuration = 1;
    
    private float foamHardness = 0.2f;
    private float foamIntensity = 0.3f;
    private String foamTexture = "Common/MatDefs/Water/Textures/foam.jpg";
    
    private String waveSound = "Sound/Environment/Ocean Waves.ogg";
    
    // Tide
    private transient float time = 0;
    private transient float waterHeight = 0;
    
    // Audio
    private transient AudioNode waves;
    private transient boolean underWater = false;
    private transient LowPassFilter aboveWaterFilter;
    private transient LowPassFilter underWaterFilter;
    
    private transient WaterFilter water;
    
    public Water(){}
    
    public void create(Node node, Vector3f sun, Camera cam, AudioRenderer audio,
            AssetManager assetManager) {
        createFilter(node, sun, assetManager);
        createAudio(cam, audio, assetManager);
    }
    
    public void createFilter(Node scene, Vector3f sun, AssetManager assetManager) {
        water = new WaterFilter(scene, sun);
        water.setWaterColor(color); // seabed color
        water.setDeepWaterColor(color); // water color
        water.setColorExtinction(filter.add(Vector3f.UNIT_XYZ)); // water tint
        water.setWaterTransparency(color.a); // water alpha
        water.setUnderWaterFogDistance(clarity); // underwater sight
        water.setRefractionConstant(reflectivity);
        water.setMaxAmplitude(waveAmplitude);
        water.setWaterHeight(level);
        
        Texture2D tex = (Texture2D) assetManager.loadTexture(foamTexture);
        water.setFoamTexture(tex);
        water.setFoamHardness(foamHardness);
        water.setFoamIntensity(foamIntensity);
        water.setCausticsIntensity(causticIntensity);
    }
    
    public void createAudio (Camera cam, AudioRenderer audio, AssetManager assetManager) {
        aboveWaterFilter = new LowPassFilter(1,1);
        underWaterFilter = new LowPassFilter(0.5f, 0.1f);
        underWater = cam.getLocation().y < waterHeight;

        waves = new AudioNode(assetManager, waveSound, false);
        waves.setReverbEnabled(true);
        waves.setLooping(true);
        waves.setVolume(0.1f);
        audio.playSource(waves);
        
    }
    
    public void update(float tpf) {
        waterHeight = (float) Math.sin(time / tideDuration) * levelVariance;
        water.setWaterHeight(level + waterHeight);
        if (water.isUnderWater() && !underWater) {
            waves.setDryFilter(underWaterFilter);
            underWater = true;
        }
        if (!water.isUnderWater() && underWater) {
            waves.setDryFilter(aboveWaterFilter);
            underWater = false;
        }
        time += tpf;
    }
    
    public WaterFilter getWaterFilter() {
        return water;
    }
    
    public AudioNode getAudioNode() {
        return waves;
    }
    
    public ColorRGBA getColor() {
        return color;
    }
    
    public void setColor(ColorRGBA color) {
        this.color.set(color);
    }
    
    public float getLevel() {
        return level;
    }
    
    public void setLevel(float level) {
        this.level = level;
        water.setWaterHeight(level);
    }
    
    public float getReflectivity() {
        return reflectivity;
    }
    
    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
        water.setRefractionConstant(reflectivity);
    }
    
    public float getWaveAmplitude() {
        return waveAmplitude;
    }
    
    public void setWaveAmplitude(float waveAmplitude) {
        this.waveAmplitude = waveAmplitude;
        water.setMaxAmplitude(waveAmplitude);
    }
    
    public float getLevelVariance() {
        return levelVariance;
    }
    
    public void setLevelVariance(float levelVariance) {
        this.levelVariance = levelVariance;
    }
}