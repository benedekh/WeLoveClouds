package weloveclouds.server.core;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Factory class to create {@link ServerSocket}.
 * 
 * @author Benoit
 */
public class ServerSocketFactory {

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Creates a new {@link ServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    public ServerSocket createServerSocketFromPort(int port) throws IOException {
        logger.info(CustomStringJoiner.join(" ", "Creating server socket on port",
                String.valueOf(port)));
        return new ServerSocket(port);
    }
}
