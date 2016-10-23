package weloveclouds.communication.models;

import java.net.InetAddress;

/**
 * @author Benoit, Benedek
 */
public class ServerConnectionInfo {
    private InetAddress ipAddress;
    private int port;

    public ServerConnectionInfo(InetAddress ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
