/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dronewars.archive;

import dronewars.archive.ObjectSocket;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class BroadcastSocket implements Runnable {
    
    protected static final Logger logger = Logger.getLogger(ObjectSocket.class.getName());
    
    protected final int localPort;
    protected final int remotePort;
    protected final BroadcastReceiver receiver;
    
    protected DatagramSocket socket;
    protected DatagramPacket in;
    protected byte[] buffer;
    
    public BroadcastSocket(int localPort, int remotePort, BroadcastReceiver receiver) {
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.receiver = receiver;
        
        try {
            socket = new DatagramSocket(localPort);
            socket.setBroadcast(true);
            
            in = new DatagramPacket(buffer, buffer.length);
            buffer = new byte[1024];
            
            logger.log(Level.INFO, "Udp socket bound to port {0}", localPort);
        } catch (SocketException ex) {
            logger.log(Level.SEVERE, "Can't bind to port " + localPort, ex);
        }  
    }
    
    @Override
    public void run() {
        while(receiver != null) {
            try {
                socket.receive(in);
                String message = new String(buffer);
                receiver.onReceive(message, in.getAddress(), in.getPort());
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void close() {
        if (socket != null)
            socket.close();
    }
    
    public void send(String message) {
        try {
            byte[] msg = message.getBytes();
            broadcastToLocalNetwork(msg);
            broadcastToNetworkInterfaces(msg);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            socket.close();
        }
    }
    
    // broadcast the message to the local network
    private void broadcastToLocalNetwork(byte[] message) 
            throws IOException, UnknownHostException {
        
        DatagramPacket packet = new DatagramPacket(message, message.length,
                InetAddress.getByName("255.255.255.255"), remotePort);
        socket.send(packet);
    }
    // broadcast the message over all the network interfaces
    private void broadcastToNetworkInterfaces(byte[] message) 
            throws IOException, SocketException {
        
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            // ignore loopback interface
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

                // get broadcast address and skíp networks without such
                InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                if (broadcastAddress == null) {
                    continue;
                }

                // broadcast the discovery message
                DatagramPacket packet = new DatagramPacket(message, message.length, 
                        broadcastAddress, remotePort);
                try {
                    socket.send(packet);
                } catch (BindException ex) {
                    logger.log(Level.SEVERE, "Can't bind to port " + remotePort, ex);
                }
            }
        }
    }
}
