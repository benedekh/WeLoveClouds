package weloveclouds.commons.networking.socket.client;

import java.io.IOException;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.socket.SSLContextHelper;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create {@link SSLSocket} from connection information.
 *
 * @author Hunton, Benedek
 */
public class SSLSocketFactory implements ISocketFactory{
    
    private static final Logger LOGGER = Logger.getLogger(SocketFactory.class);
    
    /**
     * Creates an encrypted TCP Socket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link SSLContextHelper#createSocket(InetAddress, int)}
     */
    @Override
    public SSLSocket createSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating SSL socket for", connectionInfo));
        return SSLContextHelper.getInstance().createSocket(connectionInfo.getIpAddress(),
                connectionInfo.getPort());
    }
}
