package dronewars.network;

public interface UdpBroadcastHandler{
    public void onMessage(String host, int port, String line);
}