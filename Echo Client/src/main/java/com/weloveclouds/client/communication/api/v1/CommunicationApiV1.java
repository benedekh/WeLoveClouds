package main.java.com.weloveclouds.client.communication.api.v1;

import main.java.com.weloveclouds.client.communication.api.CommunicationApi;
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
    public void connectTo(RemoteServer remoteServer) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public Response send(Request request) {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }
}
