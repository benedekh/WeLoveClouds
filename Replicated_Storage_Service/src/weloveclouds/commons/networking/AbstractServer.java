package weloveclouds.commons.networking;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.status.ServerStatus;

import static weloveclouds.commons.status.ServerStatus.HALTED;

/**
 * Represents an abstract server which can accept connections on the referred port.
 * 
 * @author Benoit
 */
public class AbstractServer extends Thread {
    protected ServerStatus status;
    protected ServerSocketFactory serverSocketFactory;
    protected ServerSocket serverSocket;
    protected int port;

    /**
     * @param serverSocketFactory to create the server socket on the referred port
     * @param port on which the server will listen
     * @throws IOException {@link ServerSocketFactory#createServerSocketFromPort(int)}}
     */
    public AbstractServer(ServerSocketFactory serverSocketFactory, int port) throws IOException {
        this.serverSocketFactory = serverSocketFactory;
        this.serverSocket = serverSocketFactory.createServerSocketFromPort(port);
        this.status = HALTED;
    }

    protected ServerStatus getStatus() {
        return status;
    }
}
