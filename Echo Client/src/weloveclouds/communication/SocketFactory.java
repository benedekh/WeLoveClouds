package weloveclouds.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Benoit on 2016-10-21.
 */
public class SocketFactory {
    public Socket createTcpSocketFromServerInfos(InetAddress remoteServerAddress, int port)
            throws IOException {
        return new Socket(remoteServerAddress, port);
    }
}
