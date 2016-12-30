package weloveclouds.communication;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create sockets from connection information.
 *
 * @author Benoit, Benedek, hb
 */
public class SocketFactory {

    private static final Logger LOGGER = Logger.getLogger(SocketFactory.class);

    /*This won't work, I need to find out how to get a SSLSocketFactory from an SSLContext in here.*/
    private final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    
    /**
     * Creates a raw TCP Socket or an SSLSocket using server connection information
     * ({@link ServerConnectionInfo#getIpAddress()} and {@link ServerConnectionInfo#getPort()}).
     *
     * @throws IOException see {@link Socket}
     */
    public Socket createTcpSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        LOGGER.debug(StringUtils.join(" ", "Creating TCP socket for", connectionInfo));
        return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
    }
    
    public SSLSocket createSSLSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException{
        LOGGER.debug(StringUtils.join(" ", "Creating SSL socket for", connectionInfo));
        return (SSLSocket)sslSocketFactory.createSocket(connectionInfo.getIpAddress(), connectionInfo.getPort());
        //TODO; have this actually return an ssl socket
    }
}
