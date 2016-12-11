package weloveclouds.communication.models;

import java.io.IOException;

import weloveclouds.communication.SocketFactory;

public class ConnectionFactory {

    private SocketFactory socketFactory;

    public ConnectionFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public Connection createConnectionFrom(ServerConnectionInfo connectionInfo) throws IOException {
        return new Connection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createTcpSocketFromInfo(connectionInfo)).build();
    }

}
