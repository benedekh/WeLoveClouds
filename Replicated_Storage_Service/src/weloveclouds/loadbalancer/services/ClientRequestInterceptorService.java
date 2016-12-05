package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-03.
 */
public class ClientRequestInterceptorService extends AbstractServer<KVMessage> {

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
                                           ServerSocketFactory serverSocketFactory,
                                           IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                           IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                           @ClientRequestsInterceptorPort int port) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer, port);
        logger = Logger.getLogger(ClientRequestInterceptorService.class);
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {

            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Client requests interceptor service stopped.");
        }
    }
}
