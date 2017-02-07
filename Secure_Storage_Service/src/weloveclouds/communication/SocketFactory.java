package weloveclouds.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.SSLContextHelper;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create sockets from connection information.
 *
 * @author Benoit, Benedek, Hunton
 */
public class SocketFactory {

    private static final Logger LOGGER = Logger.getLogger(SocketFactory.class);

    /**
     * Creates an unencrypted TCP Socket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link Socket}
     */
    public Socket createTcpSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating TCP socket for", connectionInfo));
        return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
    }

    /**
     * Creates an encrypted TCP Socket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link SSLContextHelper#createSocket(InetAddress, int)}
     */
    public Socket createSSLSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating SSL socket for", connectionInfo));
        return SSLContextHelper.getInstance().createSocket(connectionInfo.getIpAddress(),
                connectionInfo.getPort());
    }
}
