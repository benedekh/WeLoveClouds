package main.java.com.weloveclouds.client.communication.exceptions;

import main.java.com.weloveclouds.client.communication.models.RemoteServer;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToCloseConnectionException extends ClientSideException{
    public UnableToCloseConnectionException(RemoteServer remoteServer) {
        super(String.format("Unable to disconnect from server %s on port: %d",
                remoteServer.getIpAddress(), remoteServer.getPort()));
    }
}
