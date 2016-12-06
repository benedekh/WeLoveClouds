package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.services.cache.ICacheService;

import static weloveclouds.commons.status.ServerStatus.*;

/**
 * Created by Benoit on 2016-12-03.
 */
public class ClientRequestInterceptorService extends AbstractServer<KVMessage> {
    private DistributedSystemAccessService distributedSystemAccessService;
    private ICacheService cacheService;

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
                                           ServerSocketFactory serverSocketFactory,
                                           IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                           IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                           @ClientRequestsInterceptorPort int port,
                                           DistributedSystemAccessService distributedSystemAccessService,
                                           ICacheService cacheService) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer, port);
        this.logger = Logger.getLogger(ClientRequestInterceptorService.class);
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.cacheService = cacheService;
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                ConnectionHandler connectionHandler = new ConnectionHandler(
                        communicationApiFactory.createConcurrentCommunicationApiV1(),
                        new Connection.Builder().socket(socket.accept()).build(),
                        messageSerializer,
                        messageDeserializer,
                        distributedSystemAccessService,
                        cacheService);
                connectionHandler.handleConnection();
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Client requests interceptor service stopped.");
        }
    }

    private class ConnectionHandler extends AbstractConnectionHandler<KVMessage> {
        private DistributedSystemAccessService distributedSystemAccessService;
        private ICacheService cacheService;

        public ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                                 Connection connection,
                                 IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                 IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                 DistributedSystemAccessService distributedSystemAccessService,
                                 ICacheService cacheService) {
            super(communicationApi, connection, messageSerializer, messageDeserializer);
            this.logger = Logger.getLogger(this.getClass());
            this.distributedSystemAccessService = distributedSystemAccessService;
            this.cacheService = cacheService;
        }

        @Override
        public void handleConnection() {
            start();
        }

        @Override
        public void run() {
            logger.info("Client is connected to server.");
            try {
                while (connection.isConnected()) {
                    byte[] serializedMessage = communicationApi.receiveFrom(connection);
                    KVMessage deserializedMessage = messageDeserializer.deserialize(serializedMessage);
                    logger.debug(CustomStringJoiner.join(" ", "Message received:",
                            deserializedMessage.toString()));


                }
            } catch (Throwable e) {
                logger.error(e);
                closeConnection();
            }
            logger.info("Client is disconnected.");
        }
    }
}
