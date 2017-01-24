package weloveclouds.communication.models;

import java.io.IOException;

import weloveclouds.communication.SocketFactory;

/**
 * A factory to create {@link Connection}.
 * 
 * @author Benoit, Hunton
 */
public class ConnectionFactory {

    private SocketFactory socketFactory;

    public ConnectionFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /**
     * Creates a {@link Connection} based on the connection infos.
     * 
     * @throws IOException {@link SocketFactory#createTcpSocketFromInfo(connectionInfo)}
     */
    public Connection<?> createConnectionFrom(ServerConnectionInfo connectionInfo)
            throws IOException {
        return new Connection.Builder<>().remoteServer(connectionInfo)
                .socket(socketFactory.createTcpSocketFromInfo(connectionInfo)).build();
    }

    /**
     * Creates a {@link SecureConnection} based on the connection infos.
     * 
     * @throws IOException {@link SocketFactory#createSSLSocketFromInfo(connectionInfo)}
     */
    public SecureConnection createSecureConnectionFrom(ServerConnectionInfo connectionInfo)
            throws IOException {
        return new SecureConnection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createSSLSocketFromInfo(connectionInfo)).build();
    }

}
