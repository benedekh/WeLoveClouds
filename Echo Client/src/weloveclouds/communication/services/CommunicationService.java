package weloveclouds.communication.services;

import static weloveclouds.communication.models.ConnectionState.*;

import java.io.IOException;
import java.io.InputStream;

import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.RemoteServer;
import weloveclouds.communication.models.Request;
import weloveclouds.communication.models.Response;
import weloveclouds.communication.models.builders.ConnectionBuilder;

/**
 * Created by Benoit on 2016-10-21.
 */
public class CommunicationService {
    private Connection connectionToServer;
    private SocketFactory socketFactory;

    public CommunicationService(SocketFactory socketFactory) {
        this.connectionToServer = new Connection();
        this.socketFactory = socketFactory;
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
            return waitAndReadServerResponse();
        } else {
            throw new UnableToSendRequestToServerException();
        }
    }

    private Response waitAndReadServerResponse() throws IOException{
        byte[] receivedData = null;

        InputStream socketDataReader = connectionToServer.getSocket().getInputStream();

        while(receivedData == null){
            if(socketDataReader.available() != 0){
                receivedData = new byte[socketDataReader.available()];
                socketDataReader.read(receivedData);
            }
        }
        return new Response().withContent(receivedData);
    }
}
