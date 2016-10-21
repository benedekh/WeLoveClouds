package main.java.com.weloveclouds.client.communication.models.builders;

import java.net.Socket;

import main.java.com.weloveclouds.client.communication.models.Connection;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;

/**
 * Created by Benoit on 2016-10-21.
 */
public class ConnectionBuilder {
    private RemoteServer remoteServer;
    private Socket socketToRemoteServer;

    public void setRemoteServer(RemoteServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    public void setSocketToRemoteServer(Socket socketToRemoteServer) {
        this.socketToRemoteServer = socketToRemoteServer;
    }

    public ConnectionBuilder withRemoteServer(RemoteServer remoteServer){
        setRemoteServer(remoteServer);
        return this;
    }

    public ConnectionBuilder withSocketToRemoteServer(Socket socketToRemoteServer){
        setSocketToRemoteServer(socketToRemoteServer);
        return this;
    }

    public Connection build(){
        return new Connection(remoteServer, socketToRemoteServer);
    }
}
