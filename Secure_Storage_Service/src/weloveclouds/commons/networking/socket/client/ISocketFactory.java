package weloveclouds.commons.networking.socket.client;

import java.io.IOException;
import java.net.Socket;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Factory to create {@link Socket} from connection information.
 *
 * @author Benedek
 */
public interface ISocketFactory {
    
    public Socket createSocketFromInfo(ServerConnectionInfo connectionInfo) throws IOException; 
}
