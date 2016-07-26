/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import dronewars.serializable.Warplane;
import dronewars.network.UdpBroadcastSocket;
import dronewars.network.UdpBroadcastHandler;
import dronewars.serializable.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Warplane player;
    private Map<String, Warplane> enemies;
    private Map<String, Missile> missiles;
    private List<Effect> effects;
    
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
        
        this.udp = new UdpBroadcastSocket(this, PORT, 1024);
        
        node = new Node("Warzone");
        parent.attachChild(node);
        
        buffer = new Stack<>();
        enemies = new HashMap<>();
        missiles = new HashMap<>();
        effects = new ArrayList<>();
    }
    
    public void update(float tpf) {
        // prevents some sort of timeout bug for the spectator
        udp.send("");
                
        if (player != null) {
            player.update(tpf);
            udp.send(player.serialize());
        }
        
        for (Warplane enemy : enemies.values()) {
            enemy.update(tpf);
        }
        for (Missile missile : missiles.values()) {
            missile.update(tpf, udp);
        }
        
        for (Effect effect : effects) {
            effect.update(tpf);
        }
        
        handleNetworkBuffer();
    }
    
    private void handleNetworkBuffer() {
        while(!buffer.empty()) {
            String[] parts = buffer.pop().split(";");
            switch(parts[0]) {
                case "PLANE":
                    if (enemies.containsKey(parts[1])) {
                        enemies.get(parts[1]).deserialize(parts);
                    } else {
                        System.out.println("Warplane received!");
                        Warplane plane = new Warplane();
                        plane.createPassive(parts, this, assetManager);
                        enemies.put(parts[1], plane);
                    }
                    break;
                case "MISSILE":
                    if (missiles.containsKey(parts[1])) {
                        System.out.println("Missile update received!");
                        missiles.get(parts[1]).deserialize(parts);
                    } else {
                        System.out.println("Missile received!");
                        Missile missile = new Missile(parts, node, 
                                level.getTerrain().getTerrainQuad(), assetManager);
                        missiles.put(parts[1], missile);
                    }
                    break;
                case "SHOT":
                    System.out.println("Shot received!");
                    addShot(Deserializer.toVector(parts[1]),
                            Deserializer.toVector(parts[2]),
                            Deserializer.toQuaternion(parts[3]));
                    break;
                case "ATTACK":
                    if (parts[1].equals(player.getUuid()) && !player.getControl().isImmune()) {
                        System.out.println("Attack received!");
                        player.getControl().crash();
                        addExplosion(player.getControl().getPhysicsLocation(), true);
                    }
                    break;
                case "HIT":
                    if (parts[1].equals(player.getUuid()))
                        System.out.println("Hit received!");
                        player.getControl().crash();
                        addExplosion(player.getSpatial().getLocalTranslation(), true);
                    break;
                case "EXPLOSION":
                    System.out.println("Explosion received!");
                    addExplosion(Deserializer.toVector(parts[1]), false);
                    break;
                case "FLARES":
                    System.out.println("Flares received!");
                    addFlares(Deserializer.toVector(parts[1]), false);
                    break;
            }
        }
    }
    
    public void addPlayer() {
        player = JsonFactory.load(Warplane.class);
        player.createActive(this, bullet, assetManager);
        player.getControl().setPhysicsLocation(Vector3f.UNIT_Y.mult(100));
        player.getControl().respawn();
    }
    
    public Warplane getPlayer() {
        return player;
    }

    public Node getNode() {
        return node;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public Map<String,Warplane> getEnemies() {
        return enemies;
    }
    
    public UdpBroadcastSocket getSocket() {
        return udp;
    }
    
    public BulletAppState getBullet() {
        return bullet;
    }
        
    public void addShot(Vector3f direction, Vector3f position, Quaternion rotation) {
        Shot shot = new Shot(direction, position, rotation, this, timer, assetManager);
        effects.add(shot);
    }
    
    public void addMissile(Vector3f position, Quaternion rotation) {
        Missile missile = new Missile(player, enemies, this,
                node, level.getTerrain().getTerrainQuad(), assetManager);
        missiles.put(missile.getUuid(), missile);
    }

    public void addFlares(Vector3f position, boolean active) {
        Flares flares = new Flares(player.getSpatial().getLocalTranslation(), 
                node, timer, assetManager);
        if (active)
            udp.send("FLARES;" + Serializer.fromVector(position));
        effects.add(flares);
    }
    
    public void addExplosion(Vector3f position, boolean active) {
        Explosion explosion = new Explosion(node, 10, position, timer, assetManager);
        if (active)
            udp.send("EXPLOSION;" + Serializer.fromVector(position));
        effects.add(explosion);
    }
    
    public void destroy() {
        bullet.getPhysicsSpace().remove(player);
        node.removeFromParent();
    }

    public float getSize() {
        return level.getTerrain().getSize();
    }
    
    public float getRadius() {
        return level.getTerrain().getSize() * 0.75f;
    }

    @Override
    public void onMessage(String host, int port, String line) {
        buffer.add(line);
    }
}
