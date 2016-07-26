package dronewars.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpBroadcastSocket {
    
    private final int uuidLength = 12;
    private int port;
    private int bufferSize;
    private boolean closed;
    private String uuid;

    private DatagramSocket socket;
    private UdpBroadcastHandler handler;
    private static final Logger logger = Logger.getLogger(
            UdpBroadcastSocket.class.getName());

    public UdpBroadcastSocket(UdpBroadcastHandler handler, int port, int bufferSize) {
        this.port = port;
        this.handler = handler;
        this.bufferSize = bufferSize;
        String time = String.valueOf(System.currentTimeMillis());
        uuid = time.substring(time.length() - uuidLength);
        open();
        listen();
    }

    private void listen() {
        new Thread() {
            @Override
            public void run() {
                byte[] buffer = new byte[bufferSize];
                while (!closed) {
                    if (socket != null) {
                        try {
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            if (packet.getLength() > 0) {
                                String data = new String(packet.getData(), 0, packet.getLength());
                                if (!data.substring(0, uuidLength).equals(uuid) && data.length() > uuidLength) {
                                    handler.onMessage(packet.getAddress().getHostAddress(), packet.getPort(), data.substring(uuidLength));
                                }
                            }
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }.start();
    }

    private void open() {
        try {
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (UnknownHostException | SocketException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String data) {
        try {
            byte[] buffer;
            buffer = (uuid + data).getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, 
                    new InetSocketAddress("255.255.255.255", port));
            socket.send(packet);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        closed = true;
         try {
            socket.setReuseAddress(true);
        } catch (SocketException ex) {
            Logger.getLogger(UdpBroadcastSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
       
    }
    
    private int getIntFromIp() {
        String ip = InetAddress.getLoopbackAddress().getHostAddress();
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        String[] parts = ip.split(".");
        int value = 0;
        for (int i = 0; i < parts.length; i++) {
            value += Integer.parseInt(parts[i]) * Math.pow(256, 3 - i);
        }
        
        return value;
    }
    
    private InetAddress getBroadcastInterfaceAddress(Class ipVersion) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (interfaceAddress.getAddress().getClass() == ipVersion) {
                        return broadcast;
                    }
                }
            }
        } catch (SocketException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
}