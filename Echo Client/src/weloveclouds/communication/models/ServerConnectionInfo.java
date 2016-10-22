package weloveclouds.communication.models;

import java.net.InetAddress;

/**
 * Created by Benoit on 2016-10-21.
 */
public class RemoteServer {
    public InetAddress ipAddress;
    public int port;

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
