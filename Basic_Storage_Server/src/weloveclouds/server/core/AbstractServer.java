package weloveclouds.server.core;

import java.io.IOException;
import java.net.ServerSocket;

import static weloveclouds.server.core.ServerStatus.HALTED;

/**
 * Created by Benoit on 2016-11-01.
 */
public class AbstractServer extends Thread {
    protected ServerStatus status;
    protected ServerSocketFactory serverSocketFactory;
    protected ServerSocket serverSocket;
    protected int port;

    public AbstractServer(ServerSocketFactory serverSocketFactory, int port) throws IOException{
        this.serverSocketFactory = serverSocketFactory;
        this.serverSocket = serverSocketFactory.createServerSocketFromPort(port);
        this.status = HALTED;
    }

    protected ServerStatus getStatus(){
        return status;
    }
}
