package weloveclouds.commons.networking.socket.server;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import weloveclouds.commons.networking.socket.SSLContextHelper;
import weloveclouds.commons.utils.StringUtils;

/**
 * Factory class to create {@link SSLServerSocket}.
 * 
 * @author Benoit, Hunton, Benedek
 */
@Singleton
public class SSLServerSocketFactory implements IServerSocketFactory {

    private static final Logger LOGGER = Logger.getLogger(SSLServerSocketFactory.class);

    /**
     * Creates a new {@link SSLServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    @Override
    public SSLServerSocket createServerSocketFromPort(int port) throws IOException {
        LOGGER.info(StringUtils.join(" ", "Creating SSL Server Socket on port", port));
        return SSLContextHelper.getInstance().createServerSocket(port);
    }

}
