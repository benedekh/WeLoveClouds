package weloveclouds.communication.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import weloveclouds.communication.exceptions.AlreadyDisconnectedException;

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

  public boolean isConnected() {
    return socket != null && socket.isConnected() && !socket.isClosed();
  }

  public ServerConnectionInfo getRemoteServer() {
    return remoteServer;
  }

  public InputStream getInputStream() throws IOException {
    return socket.getInputStream();
  }

  public OutputStream getOutputStream() throws IOException {
    return socket.getOutputStream();
  }

  public void kill() throws IOException {
    if(isConnected()) {
      socket.close();
    }
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
