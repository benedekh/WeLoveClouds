package weloveclouds.communication.models;

import static weloveclouds.communication.models.ConnectionState.*;

import java.io.IOException;
import java.net.Socket;
/**
 * Created by Benoit on 2016-10-21.
 */
public class Connection {
    private ConnectionState state;
    private RemoteServer remoteServer;
    private Socket socket;

    public Connection(){}

    public Connection(RemoteServer remoteServer, Socket socket){
        this.remoteServer = remoteServer;
        this.socket = socket;
    }

    public ConnectionState getState() {
        if(socket == null){
            return DISCONNECTED;
        }
        return socket.isConnected() == true ? CONNECTED:DISCONNECTED;
    }

    public RemoteServer getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(RemoteServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void kill() throws IOException {
        socket.close();
    }
}
