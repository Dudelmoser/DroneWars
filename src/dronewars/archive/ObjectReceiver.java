/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.archive;

/**
 *
 * @author Jan David Kleiß
 */
public interface ObjectReceiver {
    void onReady();
    void onTimeout();
    void onReceive(Object obj);
}
