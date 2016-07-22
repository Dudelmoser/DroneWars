/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.system.Timer;

/**
 *
 * @author Jan David Kleiß
 */
public abstract class Effect {
    
    protected float spawnTime;
    protected float lifeTime;
    
    protected Timer timer;
    
    public Effect(float lifeTime, Timer timer) {
        this.timer = timer;
        this.lifeTime = lifeTime;
        this.spawnTime = timer.getTimeInSeconds();
    }
    
    public boolean hasExpired() {
        float timePassed = timer.getTimeInSeconds() - spawnTime;
        if (timePassed >= lifeTime)
            return true;
        else
            return false;
    }
 
    public abstract void update(float tpf);
    
    public abstract void remove();
}
