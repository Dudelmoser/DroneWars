package dronewars.main;

/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.Timer;

public class Explosion extends Effect {

    private static final int COUNT_FACTOR = 1;
    private static final float COUNT_FACTOR_F = 1;
    private static final Type EMITTER_TYPE = Type.Point;
    
    private AudioNode sound;
    private Node explosion;
    private ParticleEmitter flame, flash, spark, roundspark, smoketrail, debris, shockwave;
    private AssetManager assetManager;
    
    
    private float time = 0;
    private int state = 0;

    public Explosion(Node parent, float lifeTime, Vector3f position, Timer timer, AssetManager am) {
        super(lifeTime, timer);
        
        assetManager = am;
        explosion = new Node("Explosion");
        createFlame();
        createFlash();
        createSpark();
        createRoundSpark();
        createSmokeTrail();
        createDebris();
        createShockwave();
        createSound();
//            renderManager.preloadScene(template);
                
        sound = new AudioNode(assetManager, "Sounds/explosion.wav", false);
        sound.setPositional(true);
        sound.setRefDistance(20);
        sound.setReverbEnabled(false);
        explosion.attachChild(sound);
        sound.play();
        
        explosion.setLocalTranslation(position);
        parent.attachChild(explosion);
    }
    
    
    @Override
    public void update(float tpf){  
        time += tpf;
        if (state == 0){
            flame.emitAllParticles();
            spark.emitAllParticles();
            smoketrail.emitAllParticles();
            debris.emitAllParticles();
            shockwave.emitAllParticles();
            roundspark.emitAllParticles();
            state++;
        }
//        if (time > 1f + .05f && state == 1){
//            flame.emitAllParticles();
//            state++;
//        }
        
        // rewind the effect
        if (time > 5 && state == 2){
            flash.killAllParticles();
            spark.killAllParticles();
            smoketrail.killAllParticles();
            debris.killAllParticles();
            ((ParticleEmitter)explosion.getChild("Flame")).killAllParticles();
            roundspark.killAllParticles();
            shockwave.killAllParticles();
        }
    }
    
    @Override
    public void remove() {
        Node parent = explosion.getParent();
        parent.detachChild(explosion);
        
        sound.stop();
        parent.detachChild(sound);
    }
    
    private void createFlame(){
        flame = new ParticleEmitter("Flame", EMITTER_TYPE, 32 * COUNT_FACTOR);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f / COUNT_FACTOR_F)));
        flame.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        flame.setStartSize(1.3f);
        flame.setEndSize(2f);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        flame.setParticlesPerSec(0);
        flame.setGravity(0, -5, 0);
        flame.setLowLife(.4f);
        flame.setHighLife(.5f);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean("PointSprite", true);
        flame.setMaterial(mat);
        explosion.attachChild(flame);
    }

    private void createFlash(){
        flash = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        flash.setSelectRandomImage(true);
        flash.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1f / COUNT_FACTOR_F)));
        flash.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        flash.setStartSize(.1f);
        flash.setEndSize(3.0f);
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        flash.setParticlesPerSec(0);
        flash.setGravity(0, 0, 0);
        flash.setLowLife(.2f);
        flash.setHighLife(.2f);
        flash.setInitialVelocity(new Vector3f(0, 5f, 0));
        flash.setVelocityVariation(1);
        flash.setImagesX(2);
        flash.setImagesY(2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flash.png"));
        mat.setBoolean("PointSprite", true);
        flash.setMaterial(mat);
        explosion.attachChild(flash);
    }

    private void createRoundSpark(){
        roundspark = new ParticleEmitter("RoundSpark", EMITTER_TYPE, 20 * COUNT_FACTOR);
        roundspark.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, (float) (1.0 / COUNT_FACTOR_F)));
        roundspark.setEndColor(new ColorRGBA(0, 0, 0, (float) (0.5f / COUNT_FACTOR_F)));
        roundspark.setStartSize(1.2f);
        roundspark.setEndSize(1.8f);
        roundspark.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        roundspark.setParticlesPerSec(0);
        roundspark.setGravity(0, -.5f, 0);
        roundspark.setLowLife(1.8f);
        roundspark.setHighLife(2f);
        roundspark.setInitialVelocity(new Vector3f(0, 3, 0));
        roundspark.setVelocityVariation(.5f);
        roundspark.setImagesX(1);
        roundspark.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/roundspark.png"));
        mat.setBoolean("PointSprite", true);
        roundspark.setMaterial(mat);
        explosion.attachChild(roundspark);
    }

    private void createSpark(){
        spark = new ParticleEmitter("Spark", Type.Triangle, 30 * COUNT_FACTOR);
        spark.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1.0f / COUNT_FACTOR_F)));
        spark.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        spark.setStartSize(.5f);
        spark.setEndSize(.5f);
        spark.setFacingVelocity(true);
        spark.setParticlesPerSec(0);
        spark.setGravity(0, 5, 0);
        spark.setLowLife(1.1f);
        spark.setHighLife(1.5f);
        spark.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 20, 0));
        spark.getParticleInfluencer().setVelocityVariation(1);
        spark.setImagesX(1);
        spark.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/spark.png"));
        spark.setMaterial(mat);
        explosion.attachChild(spark);
    }

    private void createSmokeTrail(){
        smoketrail = new ParticleEmitter("SmokeTrail", Type.Triangle, 22 * COUNT_FACTOR);
        smoketrail.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1.0f / COUNT_FACTOR_F)));
        smoketrail.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        smoketrail.setStartSize(.2f);
        smoketrail.setEndSize(1f);
        smoketrail.setFacingVelocity(true);
        smoketrail.setParticlesPerSec(0);
        smoketrail.setGravity(0, 1, 0);
        smoketrail.setLowLife(.4f);
        smoketrail.setHighLife(.5f);
        smoketrail.setInitialVelocity(new Vector3f(0, 12, 0));
        smoketrail.setVelocityVariation(1);
        smoketrail.setImagesX(1);
        smoketrail.setImagesY(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/smoketrail.png"));
        smoketrail.setMaterial(mat);
        explosion.attachChild(smoketrail);
    }

    private void createDebris(){
        debris = new ParticleEmitter("Debris", Type.Triangle, 15 * COUNT_FACTOR);
        debris.setSelectRandomImage(true);
        debris.setRandomAngle(true);
        debris.setRotateSpeed(FastMath.TWO_PI * 4);
        debris.setStartColor(new ColorRGBA(1f, 0.59f, 0.28f, (float) (1.0f / COUNT_FACTOR_F)));
        debris.setEndColor(new ColorRGBA(.5f, 0.5f, 0.5f, 0f));
        debris.setStartSize(.2f);
        debris.setEndSize(.2f);
        debris.setParticlesPerSec(0);
        debris.setGravity(0, 12f, 0);
        debris.setLowLife(1.4f);
        debris.setHighLife(1.5f);
        debris.setInitialVelocity(new Vector3f(0, 15, 0));
        debris.setVelocityVariation(.60f);
        debris.setImagesX(3);
        debris.setImagesY(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
        debris.setMaterial(mat);
        explosion.attachChild(debris);
    }

    private void createShockwave(){
        shockwave = new ParticleEmitter("Shockwave", Type.Triangle, 1 * COUNT_FACTOR);
        shockwave.setFaceNormal(Vector3f.UNIT_Y);
        shockwave.setStartColor(new ColorRGBA(.48f, 0.17f, 0.01f, (float) (.8f / COUNT_FACTOR_F)));
        shockwave.setEndColor(new ColorRGBA(.48f, 0.17f, 0.01f, 0f));
        shockwave.setStartSize(0f);
        shockwave.setEndSize(7f);
        shockwave.setParticlesPerSec(0);
        shockwave.setGravity(0, 0, 0);
        shockwave.setLowLife(0.5f);
        shockwave.setHighLife(0.5f);
        shockwave.setInitialVelocity(new Vector3f(0, 0, 0));
        shockwave.setVelocityVariation(0f);
        shockwave.setImagesX(1);
        shockwave.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
        shockwave.setMaterial(mat);
        explosion.attachChild(shockwave);
    }
    
    private void createSound() {
    }
}