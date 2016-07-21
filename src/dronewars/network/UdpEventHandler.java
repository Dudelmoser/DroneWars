package dronewars.network;

public interface UdpEventHandler{
    public void onMessage(String host, int port, String line);
}