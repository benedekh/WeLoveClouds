package main.java.com.weloveclouds.client.communication.api;

import main.java.com.weloveclouds.client.communication.exceptions.UnableToConnectException;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;
import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.communication.models.Response;

/**
 * Created by Benoit on 2016-10-21.
 */
public interface CommunicationApi {
    double getVersion();

    void connectTo(RemoteServer remoteServer) throws UnableToConnectException;

    void disconnect();

    Response send(Request request);

    String getHelp();
}
