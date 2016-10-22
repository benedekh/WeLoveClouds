package weloveclouds.communication.models;

import static weloveclouds.communication.models.ConnectionState.CONNECTED;
import static weloveclouds.communication.models.ConnectionState.DISCONNECTED;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Benoit, Benedek
 */
public class Connection {
  private ServerConnectionInfo remoteServer;
  private Socket socket;
  
  protected Connection(ConnectionBuilder builder) {
    this.remoteServer = builder.remoteServer;
    this.socket = builder.socket;
  }

  public ConnectionState getState() {
    if (socket == null) {
      return DISCONNECTED;
    }
    return socket.isConnected() == true ? CONNECTED : DISCONNECTED;
  }

  public ServerConnectionInfo getRemoteServer() {
    return remoteServer;
  }

  public Socket getSocket() {
    return socket;
  }

  public void kill() throws IOException {
    socket.close();
  }

  public static class ConnectionBuilder {
    private ServerConnectionInfo remoteServer;
    private Socket socket;

    public ConnectionBuilder remoteServer(ServerConnectionInfo remoteServer) {
      this.remoteServer = remoteServer;
      return this;
    }

    public ConnectionBuilder socket(Socket socket) {
      this.socket = socket;
      return this;
    }

    public Connection build() {
      return new Connection(this);
    }
  }
}
