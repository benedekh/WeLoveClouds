package weloveclouds.communication.models.factory;

import java.io.IOException;

import weloveclouds.commons.networking.socket.client.ISocketFactory;
import weloveclouds.commons.networking.socket.client.SocketFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A factory to create {@link Connection}.
 * 
 * @author Benedek
 */
public abstract class AbstractConnectionFactory {
    
    protected ISocketFactory socketFactory;

    public AbstractConnectionFactory(ISocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /**
     * Creates a {@link Connection} based on the connection infos.
     * 
     * @throws IOException {@link SocketFactory#createTcpSocketFromInfo(connectionInfo)}
     */
    public abstract Connection<?> createConnectionFrom(ServerConnectionInfo connectionInfo)
            throws IOException;

}
