package weloveclouds.loadbalancer.core.handlers.requestInterceptors;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsRequestInterceptor extends AbstractServer {
    private static final Logger LOGGER = Logger.getLogger(EcsRequestInterceptor.class);


    /**
     * @param serverSocketFactory to create the server socket on the referred port
     * @param port                on which the server will listen
     * @throws IOException {@link ServerSocketFactory#createServerSocketFromPort(int)}}
     */
    public EcsRequestInterceptor(ServerSocketFactory serverSocketFactory, int port) throws IOException {
        super(serverSocketFactory, port);
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            while (status == RUNNING) {

            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } catch (Throwable ex) {
            LOGGER.fatal(ex);
        } finally {
            LOGGER.info("Active server stopped.");
        }
    }
}
