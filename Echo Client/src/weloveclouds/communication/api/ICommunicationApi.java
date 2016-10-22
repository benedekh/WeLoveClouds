package weloveclouds.communication.api;

import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public interface ICommunicationApi {
  double getVersion();

  void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException;

  void disconnect() throws UnableToDisconnectException;

  void send(byte[] content) throws UnableToSendRequestToServerException;

  byte[] receive() throws ClientNotConnectedException, ConnectionClosedException;
}
