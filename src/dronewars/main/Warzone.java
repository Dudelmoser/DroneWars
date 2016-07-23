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
import dronewars.network.UdpBroadcastSocket;
import dronewars.network.UdpBroadcastHandler;
import dronewars.serializable.Level;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Jan David Kleiß
 */
public class Warzone implements UdpBroadcastHandler {
    
    private static final int PORT = 54321;
    private Stack<String> buffer;
    
    private UdpBroadcastSocket udp;
    private Node node;
    private Airplane player;
    private WarplaneControl control;
    private Map<String, Airplane> enemies;
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
        
        udp = new UdpBroadcastSocket(this, PORT);
        
        node = new Node("Airspace");
        parent.attachChild(node);
        
        buffer = new Stack<>();
        enemies = new HashMap<>();
        effects = new ArrayList<>();
        missiles = Collections.synchronizedSet(new HashSet<Missile>());
    }
    
    public void update(float tpf) {
        if (player != null) {
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
            udp.send(player.serialize());
        }
        
        while(!buffer.empty()) {
            String[] parts = buffer.pop().split(";");
            System.out.println(parts[0]);
            switch(parts[0]) {
                case "PLANE":
                    if (enemies.containsKey(parts[0])) {
                        enemies.get(parts[1]).update(parts[3], parts[4]);
                    } else {
                        enemies.put(parts[1], new Airplane(parts, node, assetManager));
                    }
                    break;
            }
        }
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
        System.out.println(host);
        buffer.add(line);
    }
}