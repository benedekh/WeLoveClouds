package main.java.com.weloveclouds.client.communication.api.v1;

import java.io.IOException;

import main.java.com.weloveclouds.client.communication.api.CommunicationApi;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToDisconnectException;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToConnectException;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToSendRequestToServerException;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;
import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.communication.models.Response;
import main.java.com.weloveclouds.client.communication.services.CommunicationService;

/**
 * Created by Benoit on 2016-10-21.
 */
public class CommunicationApiV1 implements CommunicationApi {
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
    public void connectTo(RemoteServer remoteServer) throws UnableToConnectException {
        try {
            communicationService.connectTo(remoteServer);
        }catch(IOException e){
            throw new UnableToConnectException(remoteServer);
        }
    }

    @Override
    public void disconnect() throws UnableToDisconnectException {
        try{
            communicationService.disconnect();
        }catch(IOException e){
            throw new UnableToDisconnectException();
        }
    }

    @Override
    public Response send(Request request) throws UnableToSendRequestToServerException{
        try{
            return communicationService.sendRequest(request);
        }catch(IOException e){
            throw new UnableToSendRequestToServerException();
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
