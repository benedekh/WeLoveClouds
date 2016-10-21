package main.java.com.weloveclouds.client.communication.services;

import java.io.IOException;

import main.java.com.weloveclouds.client.communication.SocketFactory;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToSendRequestToServerException;
import main.java.com.weloveclouds.client.communication.models.Connection;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;
import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.communication.models.Response;
import main.java.com.weloveclouds.client.communication.models.builders.ConnectionBuilder;
import main.java.com.weloveclouds.client.communication.models.validators.RequestValidator;

import static main.java.com.weloveclouds.client.communication.models.ConnectionState.*;

/**
 * Created by Benoit on 2016-10-21.
 */
public class CommunicationService {
    private Connection connectionToServer;
    private RequestValidator requestValidator;
    private SocketFactory socketFactory;

    public CommunicationService(SocketFactory socketFactory, RequestValidator requestValidator) {
        this.connectionToServer = new Connection();
        this.socketFactory = socketFactory;
        this.requestValidator = requestValidator;
    }

    public void connectTo(RemoteServer remoteServer) throws IOException {
        connectionToServer = new ConnectionBuilder()
                .withRemoteServer(remoteServer)
                .withSocketToRemoteServer(socketFactory.createTcpSocketFromServerInfos
                        (remoteServer.getIpAddress(), remoteServer.getPort()))
                .build();
    }

    public void disconnect() throws IOException {
        connectionToServer.kill();
    }

    public Response sendRequest(Request request) throws IOException,
            UnableToSendRequestToServerException {
        if (connectionToServer.getState() == CONNECTED) {
            connectionToServer.getSocket().getOutputStream().write(request.toBytes());
        } else {
            throw new UnableToSendRequestToServerException();
        }

        return null;
    }
}
