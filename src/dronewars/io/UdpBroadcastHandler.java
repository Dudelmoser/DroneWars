package dronewars.io;

/**
 *
 * @author Jan David Kleiß
 */

public interface UdpBroadcastHandler{
    public void onMessage(String host, int port, String line);
}