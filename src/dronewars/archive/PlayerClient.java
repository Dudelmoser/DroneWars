package dronewars.archive;

import com.jme3.math.Vector3f;
import dronewars.model.data.Status;
import dronewars.serializable.Player;
import dronewars.model.drones.Drone;
import dronewars.model.data.GPS;
import dronewars.model.data.Health;
import dronewars.model.data.Maneuver;
import dronewars.model.settings.MatchSettings;
import dronewars.model.data.Weapon;
import dronewars.model.network.Client;
import dronewars.model.network.Client;
import java.util.Stack;
import java.util.Date;

/**
 *
 * @author Jan David Kleiß
 */
public class PlayerClient extends Client {
    private Drone drone;
    private Stack<String> audio;
    
    private long tStart;
    private long tLockon;
    
    private GPS origin = null;
    private Player me = null;
    private Player enemy = null;
    private MatchSettings cfg = null;
    
    private Status status = Status.RUNNING;
    
    public PlayerClient(Drone drone) {
        super("PLAYER");
        this.drone = drone;
        me = new Player();
        enemy = new Player();
        cfg = new MatchSettings();
        audio = new Stack();
    }
    
    @Override
    public void run() {
        server.send(drone.getPosition());
        origin = (GPS) server.receive();
        logger.info("Origin received!");
        cfg = (MatchSettings) server.receive();
        logger.info("Settings received!");
        tStart = new Date().getTime();
        
        while(true) {
            // serialization doesn't work without cloning for some reason!
            server.send(me.weapon.clone());
            me.weapon.shooting = false;
            
            me.position = drone.getPosition().getVector(origin);
            me.orientation = drone.getOrientation();
            server.send(me.position);
            server.send(me.orientation);
            
            status = (Status) server.receive();
            enemy.weapon = (Weapon) server.receive();
            enemy.position = (Vector3f) server.receive();
            enemy.orientation = (Vector3f) server.receive();
            
            Health hp = (Health) server.receive();
            me.hp = hp.hp0;
            enemy.hp = hp.hp1;
            
            handleThreat();
            handleStatus();
        }
    }
    
    public GPS getOrigin() {
        return origin;
    }
    
    public int getMyHP() {
        return me.hp;
    }
    
    public int getEnemyHP() {
        return enemy.hp;
    }
    
    public Player getMyStatus() {
        return me;
    }
    
    public Player getEnemyStatus() {
        return enemy;
    }
    
    public Status getGameStatus() {
        return status;
    }
    
    public Drone getDrone() {
        return drone;
    }
    
    public String getAudio() {
        if (!audio.empty())
            return audio.pop();
        return "";
    }
     
    public long getTimeLeft() {
        long tNow = new Date().getTime();
        long tPassed = tNow - tStart;
        return cfg.matchDuration - tPassed;
    }
    
    public float getGroundDistance(Vector3f vec) {
        return (float) Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.z, 2));
    }
    
    private boolean canShoot() {
        long tNow = new Date().getTime();
        if (tNow - tLockon >= cfg.lockonDuration) {
            return true;
        } else {
            return false;
        }
    }

    public void shoot() {
        if (canShoot()) {
            audio.push("shoot");
            me.weapon.shooting = true;
            tLockon = Integer.MAX_VALUE;
            logger.info("Shooting!");
        } else {
            audio.push("jam");
            me.weapon.shooting = false;
            logger.info("Jamming!");
        }
    }

    // to be called by the pattern detection unit
    public void setAim(boolean aiming) {
        if (aiming) {
            if (tLockon == Integer.MAX_VALUE) {
                tLockon = new Date().getTime();
                audio.push("lockon");
                me.weapon.aiming = true;
            }
        } else {
            tLockon = Integer.MAX_VALUE;
            audio.push("lockoff");
            me.weapon.aiming = false;
        }
    }

    private void handleThreat() {
        if (enemy.weapon.shooting) {
            audio.push("hit");
            drone.setManeuver(Maneuver.FLIP);
        }
    }

    private void handleStatus() {
        switch (status) {
            case RUNNING:
                return;
            case CANCELLED:
                audio.push("draw");
                logger.info("Match cancelled!");
                break;
            case DISQUALIFIED:
                audio.push("lost");
                logger.info("Disqualified!");
                break;
            case DRAW:
                audio.push("draw");
                logger.info("Draw!");
                break;
            case LOST:
                audio.push("lost");
                logger.info("Lost!");
                break;
            case WON:
                audio.push("won");
                logger.info("Won!");
                break;
        }
        drone.setManeuver(Maneuver.HOVER);
    }
    
    public boolean isRunning() {
        if (status == Status.RUNNING)
            return true;
        return false;
    }

    @Override
    public void onReceive(Object obj, int socketId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onTimeout(int socketId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
