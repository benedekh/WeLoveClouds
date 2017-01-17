package weloveclouds.communication.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a connection to a server. It contains the {@link #remoteServer} connection information
 * and the {@link #socket} through which the client is connected to the server.
 *
 * @author Benoit, Benedek
 */
public class Connection<B extends Connection.Builder<B>> implements AutoCloseable {

    private ServerConnectionInfo remoteServer;
    private Socket socket;

    protected Connection(Builder<B> builder) {
        this.remoteServer = builder.remoteServer;
        this.socket = builder.socket;
    }

    /**
     * @return true if the socket is not null and it is not closed yet.
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public ServerConnectionInfo getRemoteServer() {
        return remoteServer;
    }

    /**
     * Returns the input stream of the socket.
     *
     * @throws IOException see {@link Socket#getInputStream()}
     */
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    /**
     * Returns the output stream of the socket.
     *
     * @throws IOException see {@link Socket#getOutputStream()}
     */
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Closes the socket which is connected to the server.
     *
     * @throws IOException see {@link Socket#close()}
     */
    public synchronized void kill() throws IOException {
        if (isConnected()) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
            } catch (Exception ex) {
                // The methods shutdownOutput() and shutdownInput() are not supported in SSLSocket
            }
            socket.close();
        }
    }

    @Override
    public void close() throws IOException {
        kill();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((remoteServer == null) ? 0 : remoteServer.hashCode());
        result = prime * result + ((socket == null) ? 0 : socket.hashCode());
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
        if (!(obj instanceof Connection)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Connection other = (Connection) obj;
        if (remoteServer == null) {
            if (other.remoteServer != null) {
                return false;
            }
        } else if (!remoteServer.equals(other.remoteServer)) {
            return false;
        }
        if (socket == null) {
            if (other.socket != null) {
                return false;
            }
        } else if (!socket.equals(other.socket)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "{Connection info:" + remoteServer, ", port:", socket, "}");
    }

    /**
     * Builder pattern for creating a {@link Connection} instance.
     *
     * @author Benedek
     */
    public static class Builder<B extends Builder<B>> {
        private ServerConnectionInfo remoteServer;
        private Socket socket;

        @SuppressWarnings("unchecked")
        public B remoteServer(ServerConnectionInfo remoteServer) {
            this.remoteServer = remoteServer;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B socket(Socket socket) {
            this.socket = socket;
            return (B) this;
        }

        public Connection<B> build() {
            return new Connection<>(this);
        }
    }

}
