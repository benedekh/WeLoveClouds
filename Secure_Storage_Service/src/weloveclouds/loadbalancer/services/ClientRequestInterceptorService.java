package weloveclouds.loadbalancer.services;

import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;
import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.socket.server.IServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.SecureConnection;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class ClientRequestInterceptorService extends AbstractServer<IKVMessage> implements IClientRequestInterceptorService {
    private IDistributedSystemAccessService distributedSystemAccessService;
    private ICacheService<String, String> cacheService;

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
            IServerSocketFactory serverSocketFactory,
            IMessageSerializer<SerializedMessage, IKVMessage> messageSerializer,
            IMessageDeserializer<IKVMessage, SerializedMessage> messageDeserializer,
            @ClientRequestsInterceptorPort int port,
            IDistributedSystemAccessService distributedSystemAccessService,
            ICacheService<String, String> cacheService) throws IOException {
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
                        new SecureConnection.Builder().socket(socket.accept()).build(),
                        messageSerializer, messageDeserializer, distributedSystemAccessService,
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

    private class ConnectionHandler extends AbstractConnectionHandler<IKVMessage> {
        private IDistributedSystemAccessService distributedSystemAccessService;
        private ICacheService<String, String> cacheService;
        private ICommunicationApi transferCommunicationApi;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection<?> connection,
                          IMessageSerializer<SerializedMessage, IKVMessage> messageSerializer,
                          IMessageDeserializer<IKVMessage, SerializedMessage> messageDeserializer,
                          IDistributedSystemAccessService distributedSystemAccessService,
                          ICacheService<String, String> cacheService) {
            super(communicationApi, connection, messageSerializer, messageDeserializer);
            this.logger = Logger.getLogger(this.getClass());
            this.distributedSystemAccessService = distributedSystemAccessService;
            this.cacheService = cacheService;
            this.transferCommunicationApi = communicationApiFactory.createCommunicationApiV1();
        }

        @Override
        public void handleConnection() {
            start();
        }

        @Override
        public void run() {
            logger.info("Client is connected to server.");
            try {
                byte[] receivedMessage;
                StorageNode transferDestination;
                IKVMessage deserializedMessage;
                while (connection.isConnected()) {
                    receivedMessage = communicationApi.receiveFrom(connection);
                    deserializedMessage = messageDeserializer.deserialize(receivedMessage);
                    logger.debug(StringUtils.join(" ", "Message received:", deserializedMessage));
                    try {
                        switch (deserializedMessage.getStatus()) {
                            case GET:
                                try {
                                    communicationApi.send(
                                            messageSerializer.serialize(
                                                    new KVMessage.Builder().status(GET_SUCCESS)
                                                            .key(deserializedMessage.getKey())
                                                            .value(cacheService.get(
                                                                    deserializedMessage.getKey()))
                                                            .build())
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
                                        .key(deserializedMessage.getKey()).build())
                                .getBytes(), connection);
                    } catch (UnableToFindServerResponsibleForWritingException e) {
                        communicationApi.send(messageSerializer
                                .serialize(new KVMessage.Builder()
                                        .status(IKVMessage.StatusType.PUT_ERROR)
                                        .key(deserializedMessage.getKey()).build())
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
            logger.debug("Transferring request to: " + destination.toString());
            try {
                transferCommunicationApi.connectTo(destination.getServerConnectionInfo());
                transferCommunicationApi.send(rawMessage);
                return transferCommunicationApi.receive();
            } finally {
                transferCommunicationApi.disconnect();
            }
        }
    }
}
