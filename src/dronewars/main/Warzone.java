/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.google.gson.GsonBuilder;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import dronewars.serializable.Airplane;
import java.util.HashSet;
import dronewars.network.Udp;
import dronewars.network.UdpEventHandler;
import dronewars.serializable.Level;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jan David Kleiß
 */
public class Warzone implements UdpEventHandler {
    
    private static final int PORT = 54321;
    
    private Udp udp;
    private Node node;
    private Airplane player;
    private WarplaneControl control;
    private List<Airplane> enemies;
    private List<Effect> effects;
    private Set<Missile> missiles;
    
    private Level level;
    private Timer timer;
    private BulletAppState bullet;
    private AssetManager assetManager;
    
    public Warzone(Node parent, Timer timer, BulletAppState bullet, 
            Level level, AssetManager assetManager){
        this.level = level;
        this.timer = timer;
        this.bullet = bullet;
        this.assetManager = assetManager;
        
        udp = new Udp("localhost", PORT);
        udp.addEventHandler(this);
        
        node = new Node("Airspace");
        parent.attachChild(node);
        
        enemies = new ArrayList();
        effects = new ArrayList();
        missiles = Collections.synchronizedSet(new HashSet<Missile>());
    }
    
    public void update(float tpf) {
        if (control != null) {
            player.getSpatial().updateLogicalState(tpf);
            control.refresh(tpf);
            player.update(tpf, control.getMainRotorSpeed(), control.getYawRotorSpeed());
        }
        
        Iterator<Missile> iterator = missiles.iterator();
        while(iterator.hasNext()) {
            Missile missile = iterator.next();
            if (missile.hasExpired()) {
                missile.remove();
                addExplosion(missile.getPosition());
                iterator.remove();
            } else if (missile.isActive()) {
                missile.update(tpf);
            }
        }
        
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).hasExpired()) {
                effects.remove(i);
            } else {
                effects.get(i).update(tpf);
            }
        }
        udp.send(player.getSpatial().getLocalTranslation().toString());
    }
    
    public void addPlayer() {
        player = JsonFactory.load("airplane.json", Airplane.class);
        player.create(node, assetManager);
        player.getSpatial().setLocalTranslation(0, 200, 0);
        control = new WarplaneControl(player, this);
        bullet.getPhysicsSpace().addCollisionListener(control);
        player.getSpatial().addControl(control);
        if (bullet != null)
            bullet.getPhysicsSpace().add(player.getSpatial());
    }

    public void addAirplane() {
        Airplane airplane = new Airplane();
        airplane.create(node, assetManager);
        enemies.add(airplane);
    }
    
    public Airplane getPlayerAirplane() {
        return player;
    }
    
    public WarplaneControl getControl() {
        return control;
    }

    public Node getNode() {
        return node;
    }
        
    public void addShot() {
        Shot shot = new Shot(node, player.getSpatial(), timer, assetManager);
        effects.add(shot);
    }
    
    public void addMissile() {
        Missile missile = new Missile(control, enemies, this, node.getParent(), 
                level.getTerrain().getTerrainQuad(), assetManager);
        missiles.add(missile);
    }

    public void addFlares() {
        Flares flares = new Flares(timer, assetManager);
        flares.trigger(player.getSpatial().getLocalTranslation(), node);
        effects.add(flares);
    }
    
    public void addExplosion(Vector3f position) {
        Explosion explosion = new Explosion(node, 10, position, timer, assetManager);
        effects.add(explosion);
    }
    
    public void destroy() {
        bullet.getPhysicsSpace().remove(player);
    }

    @Override
    public void onMessage(String host, int port, String line) {
        System.out.println("UDP message received!");
    }
}
