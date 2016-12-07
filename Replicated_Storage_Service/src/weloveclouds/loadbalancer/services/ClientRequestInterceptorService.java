package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.kvstore.models.messages.IKVMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;

import static weloveclouds.commons.status.ServerStatus.*;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;

/**
 * Created by Benoit on 2016-12-03.
 */
public class ClientRequestInterceptorService extends AbstractServer<KVMessage> {
    private DistributedSystemAccessService distributedSystemAccessService;
    private ICacheService<String, String> cacheService;

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
                                           ServerSocketFactory serverSocketFactory,
                                           IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                           IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                           @ClientRequestsInterceptorPort int port,
                                           DistributedSystemAccessService distributedSystemAccessService,
                                           ICacheService<String, String> cacheService) throws
            IOException {
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
        private ICacheService<String, String> cacheService;

        public ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                                 Connection connection,
                                 IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                 IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                 DistributedSystemAccessService distributedSystemAccessService,
                                 ICacheService<String, String> cacheService) {
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
                    StorageNode transferDestination;
                    byte[] receivedMessage = communicationApi.receiveFrom(connection);
                    KVMessage deserializedMessage = messageDeserializer.deserialize(receivedMessage);

                    logger.debug(CustomStringJoiner.join(" ", "Message received:",
                            deserializedMessage.toString()));

                    switch (deserializedMessage.getStatus()) {
                        case GET:
                            try {
                                String value = cacheService.get(deserializedMessage.getKey());
                                communicationApi.send(messageSerializer
                                        .serialize(new KVMessage.Builder()
                                                .status(GET_SUCCESS)
                                                .key(deserializedMessage.getKey())
                                                .value(value)
                                                .build()).getBytes(), connection);
                            } catch (UnableToFindRequestedKeyException ex) {
                                transferDestination = distributedSystemAccessService
                                        .getReadServerFor(deserializedMessage.getKey());
                                respondToClientWithStatus(transferMessageToServerAndGetResponse
                                        (receivedMessage, transferDestination));
                            }
                            break;
                        case PUT:
                            cacheService.put(deserializedMessage.getKey(), deserializedMessage.getValue());
                            transferDestination = distributedSystemAccessService
                                    .getWriteServerFor(deserializedMessage.getKey());
                            respondToClientWithStatus(transferMessageToServerAndGetResponse
                                    (receivedMessage, transferDestination));
                            break;
                        default:
                            transferDestination = distributedSystemAccessService
                                    .getWriteServerFor(deserializedMessage.getKey());
                            respondToClientWithStatus(transferMessageToServerAndGetResponse
                                    (receivedMessage, transferDestination));
                            break;
                    }
                }
            } catch (Throwable e) {
                logger.error(e);
                closeConnection();
            }
            logger.info("Client is disconnected.");
        }

        private IKVMessage.StatusType transferMessageToServerAndGetResponse(byte[] rawMessage,
                                                                            StorageNode destination) {
            IKVMessage.StatusType response = SERVER_NOT_RESPONSIBLE;
            try {
                ICommunicationApi communicationApi = communicationApiFactory.createCommunicationApiV1();
                communicationApi.connectTo(destination.getServerConnectionInfo());
                communicationApi.send(rawMessage);
                KVMessage receivedMessage = messageDeserializer.deserialize(communicationApi
                        .receive());
                communicationApi.disconnect();
                response = receivedMessage.getStatus();
            } catch (ClientSideException | DeserializationException ex) {
                //logg
            }
            return response;
        }

        private void respondToClientWithStatus(IKVMessage.StatusType status) throws IOException {
            communicationApi.send(messageSerializer.serialize(new KVMessage.Builder()
                    .status(status)
                    .build()).getBytes(), connection);
        }
    }
}
