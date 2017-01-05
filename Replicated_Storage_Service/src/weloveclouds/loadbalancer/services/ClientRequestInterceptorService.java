package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;

import static weloveclouds.commons.status.ServerStatus.*;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class ClientRequestInterceptorService extends AbstractServer<IKVMessage> {
    private DistributedSystemAccessService distributedSystemAccessService;
    private ICacheService<String, String> cacheService;

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
                                           ServerSocketFactory serverSocketFactory,
                                           IMessageSerializer<SerializedMessage, IKVMessage>
                                                   messageSerializer,
                                           IMessageDeserializer<IKVMessage, SerializedMessage>
                                                   messageDeserializer,
                                           @ClientRequestsInterceptorPort int port,
                                           DistributedSystemAccessService
                                                   distributedSystemAccessService,
                                           ICacheService<String, String> cacheService)
            throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer,
                port);
        this.logger = Logger.getLogger(ClientRequestInterceptorService.class);
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.cacheService = cacheService;
    }

    @Override
    public void run() {
        status = RUNNING;
        logger.info("Client request interceptor started with endpoint: " + serverSocket);
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                ConnectionHandler connectionHandler = new ConnectionHandler(
                        communicationApiFactory.createConcurrentCommunicationApiV1(),
                        new Connection.Builder().socket(socket.accept()).build(), messageSerializer,
                        messageDeserializer, distributedSystemAccessService, cacheService);
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

    private class ConnectionHandler extends AbstractConnectionHandler<IKVMessage> {
        private DistributedSystemAccessService distributedSystemAccessService;
        private ICacheService<String, String> cacheService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                          Connection connection,
                          IMessageSerializer<SerializedMessage, IKVMessage> messageSerializer,
                          IMessageDeserializer<IKVMessage, SerializedMessage> messageDeserializer,
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
                    IKVMessage deserializedMessage =
                            messageDeserializer.deserialize(receivedMessage);
                    logger.debug(StringUtils.join(" ", "Message received:", deserializedMessage));
                    try {
                        switch (deserializedMessage.getStatus()) {
                            case GET:
                                try {
                                    String value = cacheService.get(deserializedMessage.getKey());
                                    communicationApi.send(
                                            messageSerializer.serialize(
                                                    new KVMessage.Builder().status(GET_SUCCESS)
                                                            .key(deserializedMessage.getKey())
                                                            .value(value).build())
                                                    .getBytes(),
                                            connection);
                                } catch (UnableToFindRequestedKeyException ex) {
                                    transferDestination = distributedSystemAccessService
                                            .getReadServerFor(deserializedMessage.getKey());
                                    communicationApi.send(transferMessageToServerAndGetResponse(
                                            receivedMessage, transferDestination), connection);
                                }
                                break;
                            case PUT:
                                cacheService.put(deserializedMessage.getKey(),
                                        deserializedMessage.getValue());
                                transferDestination = distributedSystemAccessService
                                        .getWriteServerFor(deserializedMessage.getKey());
                                communicationApi.send(transferMessageToServerAndGetResponse(
                                        receivedMessage, transferDestination), connection);
                                break;
                            case DELETE:
                                cacheService.delete(deserializedMessage.getKey());
                                transferDestination = distributedSystemAccessService
                                        .getWriteServerFor(deserializedMessage.getKey());
                                communicationApi.send(transferMessageToServerAndGetResponse(
                                        receivedMessage, transferDestination), connection);
                                break;
                            default:
                                transferDestination = distributedSystemAccessService
                                        .getWriteServerFor(deserializedMessage.getKey());
                                communicationApi.send(transferMessageToServerAndGetResponse(
                                        receivedMessage, transferDestination), connection);
                                break;
                        }
                    } catch (UnableToFindServerResponsibleForReadingException e) {
                        communicationApi.send(messageSerializer
                                .serialize(new KVMessage.Builder()
                                        .status(IKVMessage.StatusType.GET_ERROR)
                                        .key(deserializedMessage.getKey())
                                        .build())
                                .getBytes(), connection);
                    } catch (UnableToFindServerResponsibleForWritingException e) {
                        communicationApi.send(messageSerializer
                                .serialize(new KVMessage.Builder()
                                        .status(IKVMessage.StatusType.PUT_ERROR)
                                        .key(deserializedMessage.getKey())
                                        .build())
                                .getBytes(), connection);
                    }
                }
            } catch (Throwable e) {
                logger.error(e);
            } finally {
                closeConnection();
                logger.info("Client is disconnected.");
            }
        }

        private byte[] transferMessageToServerAndGetResponse(byte[] rawMessage,
                                                             StorageNode destination) throws ClientSideException {
            byte[] serverResponse;
            ICommunicationApi communicationApi = communicationApiFactory.createCommunicationApiV1();
            communicationApi.connectTo(destination.getServerConnectionInfo());
            communicationApi.send(rawMessage);
            serverResponse = communicationApi.receive();
            communicationApi.disconnect();

            return serverResponse;
        }
    }
}
