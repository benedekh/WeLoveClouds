package weloveclouds.communication.api.v1;

import com.google.inject.Inject;

import weloveclouds.communication.api.CommunicationApi;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.RemoteServer;
import weloveclouds.communication.models.Request;
import weloveclouds.communication.models.Response;
import weloveclouds.communication.services.CommunicationService;

import java.io.IOException;

/**
 * Created by Benoit on 2016-10-21.
 */
public class CommunicationApiV1 implements CommunicationApi {
    private static final double VERSION = 1.0;

    private CommunicationService communicationService;

    @Inject
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
