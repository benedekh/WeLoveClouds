package weloveclouds.communication.api.v1;

import java.io.IOException;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit, Benedek
 */
public class CommunicationApiV1 implements ICommunicationApi {
  private static final double VERSION = 1.0;

  private CommunicationService communicationService;

  public CommunicationApiV1(CommunicationService communicationService) {
    this.communicationService = communicationService;
  }

  @Override
  public double getVersion() {
    return VERSION;
  }

  @Override
  public boolean isConnected() {
    return communicationService.isConnected();
  }

  @Override
  public void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException {
    try {
      communicationService.connectTo(remoteServer);
    } catch (IOException e) {
      throw new UnableToConnectException(remoteServer);
    }
  }

  @Override
  public void disconnect() throws UnableToDisconnectException {
    try {
      communicationService.disconnect();
    } catch (IOException e) {
      throw new UnableToDisconnectException();
    }
  }

  @Override
  public void send(byte[] content) throws UnableToSendRequestToServerException {
    try {
      communicationService.send(content);
    } catch (IOException e) {
      throw new UnableToSendRequestToServerException();
    }
  }

  @Override
  public byte[] receive() throws ClientNotConnectedException, ConnectionClosedException {
    try {
      return communicationService.receive();
    } catch (IOException e) {
      throw new ConnectionClosedException();
    }
  }

}
