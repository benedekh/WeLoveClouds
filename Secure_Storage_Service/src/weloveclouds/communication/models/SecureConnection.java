package weloveclouds.communication.models;

import javax.net.ssl.SSLSocket;
/**
 * Secure connection class, functionally identical to the {@link Connection} class, 
 * except that is uses ssl sockets instead of regular ones
 * @author Benoit, Benedek, hb
 */
public class SecureConnection extends Connection{

    protected SecureConnection(Builder builder) {
        super(builder);
    }
    
    //I have a suspicion that i'll run into problems with the equals(obj) method
    
    /**
     * builder pattern for creating {@link SecureConnection} instances.
     * Leverages pre-existing Builder code in {@link Connection}.
     * @author hb
     *
     */
    public static class Builder extends Connection.Builder<Builder>{
        
        public SecureConnection.Builder remoteServer(ServerConnectionInfo remoteServer){
            super.remoteServer(remoteServer);
            return this;
        }
        
        public SecureConnection.Builder sslSocket(SSLSocket sslSocket){
            super.socket(sslSocket);
            return this;
        }
        
        public SecureConnection build(){
            return new SecureConnection(this);
        }
    }

}
