package weloveclouds.communication.models.factory;

import java.io.IOException;

import weloveclouds.commons.networking.socket.client.ISocketFactory;
import weloveclouds.commons.networking.socket.client.SocketFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A factory to create {@link Connection}.
 * 
 * @author Benoit, Hunton
 */
public class ConnectionFactory extends AbstractConnectionFactory {

    public ConnectionFactory(ISocketFactory socketFactory) {
        super(socketFactory);
    }

    /**
     * Creates a {@link Connection} based on the connection infos.
     * 
     * @throws IOException {@link SocketFactory#createTcpSocketFromInfo(connectionInfo)}
     */
    public Connection<?> createConnectionFrom(ServerConnectionInfo connectionInfo)
            throws IOException {
        return new Connection.Builder<>().remoteServer(connectionInfo)
                .socket(socketFactory.createSocketFromInfo(connectionInfo)).build();
    }

}
