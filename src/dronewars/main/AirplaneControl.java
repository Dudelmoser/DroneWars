/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dronewars.main;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dronewars.serializable.Airplane;

/**
 *
 * @author Jan David Kleiß
 */
public class AirplaneControl extends RigidBodyControl {
    
    private final float agility = 5;
    private final float gravity = 30;
    private final float climbSpeed = 10;
    private final float powerToSpeed = 1.5f;
    private float liftToSpeed = 0.5f;
    
    private float power;
    private float maxSpeed;
    private float liftThrustRatio;
    
    private float throttle = 1;
    private float pitch;
    private float roll;
    private float yaw;
    private float yawRotorSpeed;
    private float mainRotorSpeed;
    
    public AirplaneControl(Airplane airplane) {
        super(airplane.getCollisionShape());
        spatial = airplane.getSpatial();
        setGravity(new Vector3f(0, -gravity, 0));
        liftThrustRatio = airplane.getLiftThrustRatio();
        float liftPower = (gravity + climbSpeed) * liftThrustRatio;
        float thrustPower = (gravity + (liftToSpeed * gravity)) * (1 - liftThrustRatio);
        power = liftPower + thrustPower;
        maxSpeed = power * powerToSpeed;
        liftToSpeed *= 1 - liftThrustRatio;
        System.out.println(spatial.getWorldBound().getVolume());
    }
    
    public void applyForces(float tpf) {
        Quaternion rot = spatial.getLocalRotation();
        Vector3f rx = rot.mult(Vector3f.UNIT_X).normalize();
        Vector3f ry = rot.mult(Vector3f.UNIT_Y).normalize();
        Vector3f rz = rot.mult(Vector3f.UNIT_Z).normalize();

        applyAngularAirResistance();
        applyLinearAirResistance();
        applyAngularForces(rx, ry, rz);
        applyDrivingForces(ry, rz);
        applyPassiveLift(ry);
        setRotorSpeed(tpf);
    }
    
    private void applyAngularForces(Vector3f rx, Vector3f ry, Vector3f rz) {
        Vector3f pitchForce = ry.mult(pitch * agility);
        Vector3f rollForce = rx.mult(-roll * agility);
        Vector3f yawForce = rz.mult(yaw * agility);
        applyForce(pitchForce, rz);
        applyForce(rollForce, ry);
        applyForce(yawForce, rx);
    }
    
    private void applyDrivingForces(Vector3f ry, Vector3f rz) {
        float impulse = throttle * power;
        float thrust = (1 - liftThrustRatio) * impulse;
        float lift = liftThrustRatio * impulse;
        applyCentralForce(ry.mult(lift));
        applyCentralForce(rz.mult(-thrust));
    }
    
    private void applyLinearAirResistance() {
        Vector3f linVel = getLinearVelocity();
        float vel = linVel.length();
        float damping = (float) Math.pow(vel / maxSpeed, 4);
        Vector3f resist = linVel.negate().mult(damping);
        applyCentralForce(resist);
    }
    
    private void applyAngularAirResistance() {
        Vector3f spin = getAngularVelocity();
        applyForce(Vector3f.UNIT_Y.mult(spin.x), Vector3f.UNIT_Z);
        applyForce(Vector3f.UNIT_Z.mult(spin.y), Vector3f.UNIT_X);
        applyForce(Vector3f.UNIT_X.mult(spin.z), Vector3f.UNIT_Y);
    }
    
    private void applyPassiveLift(Vector3f ry) {
        float passiveLift = liftToSpeed * getForwardVelocity();
        if (passiveLift > gravity) 
            passiveLift = gravity;
        applyCentralForce(ry.mult(passiveLift));
    }
       
    private void setRotorSpeed(float tpf) {
        mainRotorSpeed += (Math.abs(throttle) - mainRotorSpeed) * tpf;
        yawRotorSpeed += (Math.abs(yaw) - yawRotorSpeed) * tpf;
    }
    
    private float getForwardVelocity() {
        Vector3f rz = spatial.getLocalRotation().mult(Vector3f.UNIT_Z).normalize();
        return rz.mult(getLinearVelocity().dot(rz) / rz.lengthSquared()).length();
    }
            
    public float getMainRotorSpeed() {
        return mainRotorSpeed;
    }
    
    public float getYawRotorSpeed() {
        return yawRotorSpeed;
    }
    
    public void setPower(float power) {
        this.power = power;
    }
    
    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }
    
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public void setRoll(float roll) {
        this.roll = roll;
    }
    
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
