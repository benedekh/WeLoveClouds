package weloveclouds.communication.models.factory;

import java.io.IOException;

import weloveclouds.commons.networking.socket.client.SSLSocketFactory;
import weloveclouds.communication.models.SecureConnection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A factory to create {@link SecureConnection}.
 * 
 * @author Hunton, Benedek
 */
public class SecureConnectionFactory extends AbstractConnectionFactory {

    public SecureConnectionFactory(SSLSocketFactory socketFactory) {
        super(socketFactory);
    }

    /**
     * Creates a {@link SecureConnection} based on the connection infos.
     * 
     * @throws IOException {@link SSLSocketFactory#createSocketFromInfo(connectionInfo)}
     * @throws IllegalArgumentException {@link SecureConnection.Builder#socket(java.net.Socket)}
     */
    @Override
    public SecureConnection createConnectionFrom(ServerConnectionInfo connectionInfo)
            throws IOException {
        return new SecureConnection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createSocketFromInfo(connectionInfo)).build();
    }

}
