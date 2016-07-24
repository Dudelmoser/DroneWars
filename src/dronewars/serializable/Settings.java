package dronewars.serializable;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.light.DirectionalLight;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.water.WaterFilter;

/**
 *
 * @author Jan David Kleiß
 */
public class Settings {
    private float volume = 0.5f;
    
    private boolean lod = true;
    private int clippingDistance = 8192;
    
    private int reflectionMapSize = 256;
    
    private boolean shadows = true;
    private int shadowLevel = 2;
    private int shadowMapSize = 4096;
    private int shadowQuality = 0;
    private float shadowIntensity = 0.25f;
    
    private boolean fxaa = false;
    
    private boolean bloom = false;
    private float bloomIntensity = 1;
    
    private boolean sunrays = false;
    private float sunDistance = 2048;
    private float sunrayIntensity = 0.5f;
    
    private boolean dof = false;
    private int blurScale = 2;
    private int blurRange = 1024;
    
    private transient FilterPostProcessor filters;
    private transient TerrainLodControl lodControl;
    
    public void setProfile(int profile) {
        shadowLevel = 0;
        shadowQuality = 0;
        
        dof = false;
        fxaa = false;
        bloom = false;
        sunrays = false;
        
        if (profile > 0) {
            reflectionMapSize = 512;
            shadowLevel = 1;
            shadowQuality = 1;
        }
        
        if (profile > 1) {
            reflectionMapSize = 1024;
            shadowLevel = 2;
            shadowQuality = 2;
            fxaa = true;
            bloom = true;
            sunrays = true;
            lod = false;
        }
    }
    
    public void apply(AssetManager assetManager, ViewPort viewPort, Camera cam,
            TerrainQuad terrain, DirectionalLight sun, WaterFilter water,
            AudioRenderer audio) {
        
        cam.setFrustumFar(clippingDistance);
        
        if (lod && lodControl != null) {
            terrain.removeControl(lodControl);
        } else if (lod && lodControl == null) {
            lodControl = new TerrainLodControl(terrain, cam);
            terrain.addControl(lodControl);
        }
        
        if (shadowLevel == 0) {
            terrain.setShadowMode(RenderQueue.ShadowMode.Off);
        } else if (shadowLevel == 1) {
            terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        } else if (shadowLevel == 2) {
            terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }
        
        if (filters != null) {
            viewPort.removeProcessor(filters);
        }
        
        initFilters(sun, assetManager);
        
        water.setReflectionMapSize(reflectionMapSize);
        filters.addFilter(water);
        
        viewPort.addProcessor(filters);
    }
    
    private void initFilters(DirectionalLight sun, AssetManager assetManager) {
        filters = new FilterPostProcessor(assetManager);
        filters.addFilter(getShadowFilter(sun, assetManager));
        
        if (fxaa) {
            FXAAFilter fxaa = new FXAAFilter();
            filters.addFilter(fxaa);
        }

        if (bloom) {
            BloomFilter bloom = new BloomFilter();
            bloom.setBloomIntensity(bloomIntensity);
            filters.addFilter(bloom);
        }
        
        if (sunrays) {
            LightScatteringFilter sunrays = new LightScatteringFilter(
                    sun.getDirection().mult(-sunDistance));
            sunrays.setLightDensity(sunrayIntensity);
            filters.addFilter(sunrays);
        }
        
        if (dof) {
            DepthOfFieldFilter dof = new DepthOfFieldFilter();
            dof.setFocusDistance(0);
            dof.setBlurScale(blurScale);
            dof.setFocusRange(blurRange);
            filters.addFilter(dof);
        }
    }
    
    private Filter getShadowFilter(DirectionalLight sun, AssetManager assetManager) {
        DirectionalLightShadowFilter shadow = new DirectionalLightShadowFilter(
                assetManager, shadowMapSize, 2);
        shadow.setEnabled(shadows);
        shadow.setLight(sun);
        shadow.setShadowIntensity(shadowIntensity);
        
        if (shadowQuality == 0) {
            shadow.setEdgeFilteringMode(EdgeFilteringMode.Dither);
        } else if (shadowQuality == 1) {
            shadow.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        } else if (shadowQuality == 2) {
            shadow.setEdgeFilteringMode(EdgeFilteringMode.PCF8);
        }
        return shadow;
    }
}
