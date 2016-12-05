package weloveclouds.loadbalancer.services.requestInterceptors;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-03.
 */
public class ClientRequestInterceptor{
    private static final Logger LOGGER = Logger.getLogger(ClientRequestInterceptor.class);

    /**
     * @param serverSocketFactory to create the server socket on the referred port
     * @param port                on which the server will listen
     * @throws IOException {@link ServerSocketFactory#createServerSocketFromPort(int)}}
     */
    public ClientRequestInterceptor(ServerSocketFactory serverSocketFactory, int port) throws IOException {
    }


}
