package weloveclouds.communication.models;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import weloveclouds.commons.networking.SSLContextHelper;
import weloveclouds.communication.SocketFactory;

/**
 * A factory to create {@link Connection}.
 * 
 * @author Benoit
 */
public class ConnectionFactory {

    private SocketFactory socketFactory;

    public ConnectionFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /**
     * Creates a {@link Connection} based on the connection infos.
     * 
     * @throws IOException {@link SocketFactory#createTcpSocketFromInfo(ServerConnectionInfo)}
     */
    public Connection createConnectionFrom(ServerConnectionInfo connectionInfo) throws IOException {
        return new Connection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createTcpSocketFromInfo(connectionInfo)).build();
    }
    
    public SecureConnection createSecureConnectionFrom(ServerConnectionInfo connectionInfo) throws IOException{
        return new SecureConnection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createSSLSocketFromInfo(connectionInfo)).build();
    }

}
