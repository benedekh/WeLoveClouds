package main.java.com.weloveclouds.client.communication.services;

import java.io.IOException;
import java.util.Optional;

import main.java.com.weloveclouds.client.communication.SocketFactory;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToCloseConnectionException;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToConnectException;
import main.java.com.weloveclouds.client.communication.models.Connection;
import main.java.com.weloveclouds.client.communication.models.ConnectionState;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;
import main.java.com.weloveclouds.client.communication.models.Request;
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
        this.socketFactory = socketFactory;
        this.requestValidator = requestValidator;
    }

    public void connectTo(RemoteServer remoteServer) throws UnableToConnectException {
        try {
            connectionToServer = new ConnectionBuilder().withRemoteServer(remoteServer)
                    .withSocketToRemoteServer(socketFactory.createTcpSocketFromServerInfos
                            (remoteServer.getIpAddress(), remoteServer.getPort())).build();
        } catch (IOException e) {
            throw new UnableToConnectException(remoteServer);
        }
    }

    public void disconnect() throws UnableToCloseConnectionException {
        if(Optional.ofNullable(connectionToServer).isPresent()){
            try {
                connectionToServer.kill();
            }catch(IOException e){
                throw new UnableToCloseConnectionException(connectionToServer.getRemoteServer());
            }
        }
    }

    public void sendRequest(Request request){

    }
}
