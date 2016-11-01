package weloveclouds.server.core;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Benoit on 2016-10-29.
 */
public class ServerSocketFactory {

    public ServerSocket createServerSocketFromPort(int port) throws IOException{
        return new ServerSocket(port);
    }
}
