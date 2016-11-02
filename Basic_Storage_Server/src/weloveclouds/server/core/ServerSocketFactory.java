package weloveclouds.server.core;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Created by Benoit on 2016-10-29.
 */
public class ServerSocketFactory {

    private Logger logger = Logger.getLogger(getClass());

    public ServerSocket createServerSocketFromPort(int port) throws IOException {
        logger.info(CustomStringJoiner.join(" ", "Creating server socket on port",
                String.valueOf(port)));
        return new ServerSocket(port);
    }
}
