package weloveclouds.commons.networking.socket.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import weloveclouds.commons.utils.StringUtils;

/**
 * Factory class to create {@link ServerSocket}.
 * 
 * @author Benoit, Hunton, Benedek
 */
@Singleton
public class ServerSocketFactory implements IServerSocketFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerSocketFactory.class);

    @Override
    public ServerSocket createServerSocketFromPort(int port) throws IOException {
        LOGGER.info(StringUtils.join(" ", "Creating server socket on port", port));
        return new ServerSocket(port);
    }

}
