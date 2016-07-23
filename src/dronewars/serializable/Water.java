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
    
    private ColorRGBA waterColor = new ColorRGBA(0.1f, 0.2f, 0.3f, 0.1f);
    private Vector3f colorExtinction = new Vector3f(127, 127, 127);
    
    private float reflectivity = 0.5f;
    private float underwaterSight = 50;
    private float causticIntensity = 0.5f;
    private float waveAmplitude = 1f;
    
    private float waterLevel = 23.5f;
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
        water.setWaterColor(waterColor); // seabed color
        water.setDeepWaterColor(waterColor); // water color
        water.setColorExtinction(colorExtinction.add(Vector3f.UNIT_XYZ)); // water tint
        water.setWaterTransparency(waterColor.a); // water alpha
        water.setUnderWaterFogDistance(underwaterSight); // underwater sight
        water.setRefractionConstant(reflectivity);
        water.setMaxAmplitude(waveAmplitude);
        water.setWaterHeight(waterLevel);
        
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
        water.setWaterHeight(waterLevel + waterHeight);
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

    /**
     * @return the foamTexture
     */
    public String getFoamTexture() {
        return foamTexture;
    }

    /**
     * @param foamTexture the foamTexture to set
     */
    public void setFoamTexture(String foamTexture) {
        this.foamTexture = foamTexture;
    }

    /**
     * @return the waveSound
     */
    public String getWaveSound() {
        return waveSound;
    }

    /**
     * @param waveSound the waveSound to set
     */
    public void setWaveSound(String waveSound) {
        this.waveSound = waveSound;
    }

    /**
     * @return the waterColor
     */
    public ColorRGBA getColor() {
        return waterColor;
    }

    /**
     * @param waterColor the waterColor to set
     */
    public void setColor(ColorRGBA waterColor) {
        this.waterColor = waterColor;
    }

    /**
     * @return the waterNuance
     */
    public Vector3f getWaterNuance() {
        return colorExtinction;
    }

    /**
     * @param waterNuance the waterNuance to set
     */
    public void setWaterNuance(Vector3f waterNuance) {
        this.colorExtinction = waterNuance;
    }

    /**
     * @return the waterClarity
     */
    public float getWaterClarity() {
        return underwaterSight;
    }

    /**
     * @param waterClarity the waterClarity to set
     */
    public void setWaterClarity(float waterClarity) {
        this.underwaterSight = waterClarity;
    }

    /**
     * @return the foamIntensity
     */
    public float getFoamIntensity() {
        return foamIntensity;
    }

    /**
     * @param foamIntensity the foamIntensity to set
     */
    public void setFoamIntensity(float foamIntensity) {
        this.foamIntensity = foamIntensity;
    }

    /**
     * @return the foamHardness
     */
    public float getFoamHardness() {
        return foamHardness;
    }

    /**
     * @param foamHardness the foamHardness to set
     */
    public void setFoamHardness(float foamHardness) {
        this.foamHardness = foamHardness;
    }

    /**
     * @return the reflectivity
     */
    public float getReflectivity() {
        return reflectivity;
    }

    /**
     * @param reflectivity the reflectivity to set
     */
    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
        water.setRefractionConstant(reflectivity);
    }

    /**
     * @return the causticIntensity
     */
    public float getCausticIntensity() {
        return causticIntensity;
    }

    /**
     * @param causticIntensity the causticIntensity to set
     */
    public void setCausticIntensity(float causticIntensity) {
        this.causticIntensity = causticIntensity;
    }

    /**
     * @return the waveAmplitude
     */
    public float getWaveAmplitude() {
        return waveAmplitude;
    }

    /**
     * @param waveAmplitude the waveAmplitude to set
     */
    public void setWaveAmplitude(float waveAmplitude) {
        this.waveAmplitude = waveAmplitude;
    }

    /**
     * @return the waterLevel
     */
    public float getWaterLevel() {
        return waterLevel;
    }

    /**
     * @param waterLevel the waterLevel to set
     */
    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    /**
     * @return the levelVariance
     */
    public float getLevelVariance() {
        return levelVariance;
    }

    /**
     * @param levelVariance the levelVariance to set
     */
    public void setLevelVariance(float levelVariance) {
        this.levelVariance = levelVariance;
    }

    /**
     * @return the tideDuration
     */
    public float getTideDuration() {
        return tideDuration;
    }

    /**
     * @param tideDuration the tideDuration to set
     */
    public void setTideDuration(float tideDuration) {
        this.tideDuration = tideDuration;
    }
}