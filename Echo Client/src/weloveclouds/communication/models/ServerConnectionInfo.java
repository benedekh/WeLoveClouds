package weloveclouds.communication.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import weloveclouds.client.utils.StringJoiner;

/**
 * Stores the server connection information, the {@link #ipAddress} and the {@link #port}.
 * 
 * @author Benoit, Benedek
 */
public class ServerConnectionInfo {
    private InetAddress ipAddress;
    private int port;

    protected ServerConnectionInfo(ServerConnectionInfoBuilder builder) {
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return StringJoiner.join(" ", ipAddress.toString(), String.valueOf(port));
    }

    /**
     * Builder pattern for creating a {@link ServerConnectionInfo} instance.
     * 
     * @author Benedek
     */
    public static class ServerConnectionInfoBuilder {
        private InetAddress ipAddress;
        private int port;

        public ServerConnectionInfoBuilder ipAddress(String ipAddress) throws UnknownHostException {
            this.ipAddress = InetAddress.getByName(ipAddress);
            return this;
        }

        public ServerConnectionInfoBuilder ipAddress(InetAddress address) {
            this.ipAddress = address;
            return this;
        }

        public ServerConnectionInfoBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerConnectionInfo build() {
            return new ServerConnectionInfo(this);
        }
    }
}
