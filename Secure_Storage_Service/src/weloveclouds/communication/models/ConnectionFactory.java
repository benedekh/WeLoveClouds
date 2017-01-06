package weloveclouds.communication.models;

import java.io.IOException;

import javax.net.ssl.SSLContext;

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
    
    public SecureConnection createSecureConnectionFrom(ServerConnectionInfo connectionInfo, SSLContext sslContext) throws IOException{
        return new SecureConnection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createSSLSocketFromInfo(connectionInfo, sslContext)).build();
    }

}
