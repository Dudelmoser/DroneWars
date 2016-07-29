/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.network;

import java.net.InetAddress;

/**
 *
 * @author Jan David Kleiß
 */
public interface BroadcastReceiver {
    void onReceive(String message, InetAddress ip, int port);
}
