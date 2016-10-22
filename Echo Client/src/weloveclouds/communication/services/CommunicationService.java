package weloveclouds.communication.services;

import static weloveclouds.communication.models.ConnectionState.CONNECTED;

import java.io.IOException;
import java.io.InputStream;

import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.exceptions.AlreadyConnectedException;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public class CommunicationService {
  private Connection connectionToServer;
  private Thread connectionShutdownHook;

  private SocketFactory socketFactory;

  public CommunicationService(SocketFactory socketFactory) {
    this.connectionToServer = new Connection();
    this.socketFactory = socketFactory;
  }

  public void connectTo(ServerConnectionInfo remoteServer)
      throws IOException, AlreadyConnectedException {
    if (connectionToServer == null) {
      initializeConnection(remoteServer);
    } else {
      switch (connectionToServer.getState()) {
        case CONNECTED:
          throw new AlreadyConnectedException();
        case DISCONNECTED:
          Runtime.getRuntime().removeShutdownHook(connectionShutdownHook);
          initializeConnection(remoteServer);
      }
    }
  }

  private void initializeConnection(ServerConnectionInfo remoteServer) throws IOException {
    connectionToServer = new Connection.ConnectionBuilder().remoteServer(remoteServer)
        .socket(socketFactory.createTcpSocketFromInfo(remoteServer)).build();

    // create shutdown hook to automatically close the connection
    connectionShutdownHook = new Thread(new ConnectionCloser(connectionToServer));
    Runtime.getRuntime().addShutdownHook(connectionShutdownHook);
  }

  public void disconnect() throws IOException {
    connectionToServer.kill();
  }

  public void send(byte[] content) throws IOException, UnableToSendRequestToServerException {
    if (connectionToServer.getState() == CONNECTED) {
      connectionToServer.getSocket().getOutputStream().write(content);
    } else {
      throw new ClientNotConnectedException();
    }
  }

  public byte[] receive() throws IOException, ClientNotConnectedException {
    if (connectionToServer.getState() == CONNECTED) {
    byte[] receivedData = null;

    InputStream socketDataReader = connectionToServer.getSocket().getInputStream();

    while (receivedData == null) {
      if (socketDataReader.available() != 0) {
        receivedData = new byte[socketDataReader.available()];
        socketDataReader.read(receivedData);
      }
    }

    return receivedData;}  else {
      throw new ClientNotConnectedException();
    }
  }

  private static class ConnectionCloser implements Runnable {
    private Connection connection;

    public ConnectionCloser(Connection connection) {
      this.connection = connection;
    }

    @Override
    public void run() {
      if (connection.getState() == CONNECTED) {
        try {
          connection.kill();
        } catch (IOException e) {
          // suppress exception because the thread is invoked as soon as JVM is to be shut down
        }
      }
    }

  }
}
