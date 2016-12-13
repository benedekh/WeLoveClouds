package weloveclouds.communication;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create sockets from connection information.
 *
 * @author Benoit, Benedek
 */
public class SocketFactory {

    private static final Logger LOGGER = Logger.getLogger(SocketFactory.class);

    /**
     * Creates a TCP Socket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link Socket}
     */
    public Socket createTcpSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating socket for", connectionInfo.toString()));
        return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
    }
}
