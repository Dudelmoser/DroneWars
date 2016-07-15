package dronewars.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan David Kleiß
 */
public class ObjectSocket extends Thread {
    
    protected static final Logger logger = Logger.getLogger(ObjectSocket.class.getName());
    
    public static final int TIMEOUT = 10000;
    
    protected final int port;
    protected final InetAddress ip;
    protected final ObjectReceiver receiver;
    
    protected volatile Socket socket;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
        
    public ObjectSocket(int port, ObjectReceiver receiver) {
        this.ip = null;
        this.port = port;
        this.receiver = receiver;
    }
    
    public ObjectSocket(InetAddress ip, int port, ObjectReceiver receiver) {
        this.ip = ip;
        this.port = port;
        this.receiver = receiver;
    }
    
    @Override
    public void run() {
        init();
        while(true) {
            try {
                receiver.onReceive(in.readObject());
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Socket has received an unknown object!", ex);
            } catch (SocketTimeoutException ex) {
                logger.log(Level.WARNING, "Socket has timed out!", ex);
                receiver.onTimeout();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Socket has closed!", ex);
            }
        }
    }
       
    public void init() {
        close();
        try {
            if (ip == null) {
                ServerSocket ss = new ServerSocket(port);
                socket = ss.accept();
                logger.log(Level.INFO, "Server socket listening on port {0}!",
                        new Object[] {port});
            } else {
                socket = new Socket(ip, port);
            }
                        
            logger.log(Level.INFO, "Socket {0}:{1} connected to {2}:{3}!", 
                    new Object[]{socket.getLocalAddress(),
                    socket.getLocalPort(),
                    socket.getInetAddress(),
                    socket.getPort()});
            socket.setSoTimeout(TIMEOUT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(Object obj) {
        try {
            System.out.println("Sending..");
            out.writeObject(obj);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Socket can't write to output stream!", ex);
        }
    }
    
    public void close() {
        try {
            if (socket != null)
                socket.close();
            socket = null;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Socket can't be closed!", ex);
        }
    }
    
    public int getPort() {
        return socket.getPort();
    }
    
    public InetAddress getAddress() {
        return socket.getInetAddress();
    }
    
    public boolean isConnected() {
        if (socket != null && in != null && out != null) {
            if (socket.getRemoteSocketAddress() != null) {
                logger.info("Socket is ready to exchange objects!");
                return true;
            }
        }
        return false;
    }
}