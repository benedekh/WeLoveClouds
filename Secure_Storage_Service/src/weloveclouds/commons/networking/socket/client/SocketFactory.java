package weloveclouds.commons.networking.socket.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create {@link Socket} from connection information.
 *
 * @author Benoit, Benedek, Hunton
 */
public class SocketFactory implements ISocketFactory {

    private static final Logger LOGGER = Logger.getLogger(SocketFactory.class);

    /**
     * Creates an unencrypted TCP Socket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link Socket}
     */
    @Override
    public Socket createSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating TCP socket for", connectionInfo));
        return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
    }

}
