package weloveclouds.communication;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.mockito.internal.util.StringJoiner;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public class SocketFactory {

    private Logger logger;

    public SocketFactory() {
        this.logger = Logger.getLogger(getClass());
    }

    public Socket createTcpSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
        logger.debug(StringJoiner.join(" ", "Creating socket for", connectionInfo.toString()));
        return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
    }
}
