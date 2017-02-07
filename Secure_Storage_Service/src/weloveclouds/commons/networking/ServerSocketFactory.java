package weloveclouds.commons.networking;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * Factory class to create {@link ServerSocket}.
 * 
 * @author Benoit, Hunton, Benedek
 */
public class ServerSocketFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerSocketFactory.class);

    /**
     * Creates a new {@link ServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    public ServerSocket createServerSocketFromPort(int port) throws IOException {
        LOGGER.info(StringUtils.join(" ", "Creating server socket on port", port));
        return new ServerSocket(port);
    }

    /**
     * Creates a new SSL-encrypted {@link ServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    public ServerSocket createSSLServerSocketFromPort(int port) throws IOException {
        LOGGER.info(StringUtils.join(" ", "Creating SSL Server Socket on port", port));
        return SSLContextHelper.getInstance().createServerSocket(port);
    }

}
