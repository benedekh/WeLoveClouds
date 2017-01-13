package weloveclouds.communication.models;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import weloveclouds.commons.networking.SSLContextHelper;
import weloveclouds.communication.SocketFactory;

public class ConnectionFactory {

    private SocketFactory socketFactory;
    //private SSLContextHelper sslContextHelper = SSLContextHelper.getInstance();

    public ConnectionFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public Connection createConnectionFrom(ServerConnectionInfo connectionInfo) throws IOException {
        return new Connection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createTcpSocketFromInfo(connectionInfo)).build();
    }
    
    public SecureConnection createSecureConnectionFrom(ServerConnectionInfo connectionInfo) throws IOException{
        return new SecureConnection.Builder().remoteServer(connectionInfo)
                .socket(socketFactory.createSSLSocketFromInfo(connectionInfo)).build();
    }

}
