package weloveclouds.communication.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Represents a connection to a server. It contains the {@link #remoteServer} connection information
 * and the {@link #socket} through which the client is connected to the server.
 *
 * @author Benoit, Benedek
 */
public class Connection {
    private ServerConnectionInfo remoteServer;
    private Socket socket;

    protected Connection(ConnectionBuilder builder) {
        this.remoteServer = builder.remoteServer;
        this.socket = builder.socket;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
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
    public void kill() throws IOException {
        if (isConnected()) {
            socket.close();
        }
    }

    /**
     * Builder pattern for creating a {@link Connection} instance.
     *
     * @author Benedek
     */
    public static class ConnectionBuilder {
        private ServerConnectionInfo remoteServer;
        private Socket socket;

        public ConnectionBuilder remoteServer(ServerConnectionInfo remoteServer) {
            this.remoteServer = remoteServer;
            return this;
        }

        public ConnectionBuilder socket(Socket socket) {
            this.socket = socket;
            return this;
        }

        public Connection build() {
            return new Connection(this);
        }
    }
}
