package weloveclouds.commons.networking;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * Factory class to create {@link ServerSocket}.
 * 
 * @author Benoit
 */
public class ServerSocketFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerSocketFactory.class);

    //private final SSLServerSocketFactory SSLSockFactory = (SSLContext.getInstance("SSL"))
    /**
     * Creates a new {@link ServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    public ServerSocket createServerSocketFromPort(int port) throws IOException {
        LOGGER.info(StringUtils.join(" ", "Creating server socket on port", port));
        return new ServerSocket(port);
    }
    /*
    public SSLServerSocket createSSLServerSocketFromPort(int port) throws IOException{
        LOGGER.info(StringUtils.join(" ", "Creating SSL server socket on port", port));;
        return SSLServerSocket(port);
    }*/
}
