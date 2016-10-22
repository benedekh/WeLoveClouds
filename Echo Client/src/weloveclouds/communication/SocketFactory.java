package weloveclouds.communication;

import java.io.IOException;
import java.net.Socket;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public class SocketFactory {
  public Socket createTcpSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException {
    return new Socket(connectionInfo.getIpAddress(), connectionInfo.getPort());
  }
}
