package weloveclouds.commons.networking.socket.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Factory to create {@link ServerSocket}.
 * 
 * @author Benedek
 */
public interface IServerSocketFactory {

    /**
     * Creates a new {@link ServerSocket} on the referred port.
     * 
     * @throws IOException if the socket cannot be created
     */
    public ServerSocket createServerSocketFromPort(int port) throws IOException;

}
