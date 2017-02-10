package weloveclouds.communication.models;

import java.net.Socket;

import javax.net.ssl.SSLSocket;

/**
 * Secure connection class, functionally identical to the {@link Connection} class, except that is
 * uses ssl sockets instead of regular ones
 * 
 * @author Benoit, Benedek, Hunton
 */
public class SecureConnection extends Connection<SecureConnection.Builder> {

    protected SecureConnection(Builder builder) {
        super(builder);
    }

    /**
     * builder pattern for creating {@link SecureConnection} instances. Leverages pre-existing
     * Builder code in {@link Connection}.
     * 
     * @author Hunton
     */
    public static class Builder extends Connection.Builder<Builder> {

        /**
         * @throws IllegalArgumentException if the socket is not a {@link SSLSocket}
         */
        public Builder socket(Socket sslSocket) {
            if (!(sslSocket instanceof SSLSocket)) {
                throw new IllegalArgumentException("Secure connection must contain secure socket.");
            }
            super.socket(sslSocket);
            return this;
        }

        public Builder socket(SSLSocket sslSocket) {
            super.socket(sslSocket);
            return this;
        }

        public SecureConnection build() {
            return new SecureConnection(this);
        }
    }
}
