package main.java.com.weloveclouds.client.communication.models;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import static main.java.com.weloveclouds.client.communication.models.ConnectionState.*;
/**
 * Created by Benoit on 2016-10-21.
 */
public class Connection {
    private ConnectionState state;
    private RemoteServer remoteServer;
    private Socket socketToRemoteServer;

    public Connection(RemoteServer remoteServer, Socket socketToRemoteServer){
        this.remoteServer = remoteServer;
        this.socketToRemoteServer = socketToRemoteServer;
    }

    public ConnectionState getState() {
        if(!Optional.ofNullable(socketToRemoteServer).isPresent()){
            return DISCONNECTED;
        }
        return socketToRemoteServer.isConnected() == true ? CONNECTED:DISCONNECTED;
    }

    public RemoteServer getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(RemoteServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    public Socket getSocketToRemoteServer() {
        return socketToRemoteServer;
    }

    public void setSocketToRemoteServer(Socket socketToRemoteServer) {
        this.socketToRemoteServer = socketToRemoteServer;
    }

    public void kill() throws IOException {
        socketToRemoteServer.close();
    }
}
