package weloveclouds.communication.api;

import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.RemoteServer;
import weloveclouds.communication.models.Request;
import weloveclouds.communication.models.Response;

/**
 * Created by Benoit on 2016-10-21.
 */
public interface CommunicationApi {
    double getVersion();

    void connectTo(RemoteServer remoteServer) throws UnableToConnectException;

    void disconnect() throws UnableToDisconnectException;

    Response send(Request request) throws UnableToSendRequestToServerException;

    String getHelp();
}
