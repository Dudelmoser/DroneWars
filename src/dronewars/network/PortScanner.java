package dronewars.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class PortScanner {
    private static final Logger logger = Logger.getLogger(PortScanner.class.getName());
    
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static HashSet<Integer> takenPorts = new HashSet();
    
    public static synchronized int getPort() {
        return getPort(MIN_PORT);
    }
    
    public static synchronized int getPort(int startPort) {
         if (startPort < MIN_PORT || startPort > MAX_PORT) {
            throw new IllegalArgumentException("Port must be between " + 
                    MIN_PORT + " and " + MAX_PORT);
        }
        
        for (int i = startPort; i <= MAX_PORT; i++) {            
            if (isAvailable(i)) {
                if (!takenPorts.contains(i)) {
                    takenPorts.add(i);
                    logger.log(Level.INFO, "Found free port {0}", i);
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    public static boolean isAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {}
            }
        }
        return false;
    }
}
