package weloveclouds.communication.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import weloveclouds.commons.utils.StringUtils;

/**
 * Stores the server connection information, the {@link #ipAddress} and the {@link #port}.
 *
 * @author Benoit, Benedek
 */
public class ServerConnectionInfo {

    private InetAddress ipAddress;
    private int port;

    protected ServerConnectionInfo(Builder builder) {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServerConnectionInfo)) {
            return false;
        }
        ServerConnectionInfo other = (ServerConnectionInfo) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null) {
                return false;
            }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.join("", "<", ipAddress.getHostAddress(), " , ", port, ">");
    }

    /**
     * Builder pattern for creating a {@link ServerConnectionInfo} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private InetAddress ipAddress;
        private int port;

        public Builder ipAddress(String ipAddress) throws UnknownHostException {
            if (ipAddress == null) {
                throw new UnknownHostException("No host provided.");
            }
            this.ipAddress = InetAddress.getByName(ipAddress);
            return this;
        }

        public Builder ipAddress(InetAddress address) {
            this.ipAddress = address;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public ServerConnectionInfo build() {
            return new ServerConnectionInfo(this);
        }
    }
}
