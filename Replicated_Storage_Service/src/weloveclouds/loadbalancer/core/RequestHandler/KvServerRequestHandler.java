package weloveclouds.loadbalancer.core.RequestHandler;

import java.io.IOException;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;

/**
 * Created by Benoit on 2016-12-03.
 */
public class KvServerRequestHandler extends AbstractServer {
    /**
     * @param serverSocketFactory to create the server socket on the referred port
     * @param port                on which the server will listen
     * @throws IOException {@link ServerSocketFactory#createServerSocketFromPort(int)}}
     */
    public KvServerRequestHandler(ServerSocketFactory serverSocketFactory, int port) throws IOException {
        super(serverSocketFactory, port);
    }
}
