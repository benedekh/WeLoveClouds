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
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-06.
 */
public class EcsNotificationService extends AbstractServer<KVAdminMessage> implements INotifier<Object> {
    private DistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public EcsNotificationService(CommunicationApiFactory communicationApiFactory,
                                  ServerSocketFactory serverSocketFactory,
                                  IMessageSerializer<SerializedMessage, KVAdminMessage>
                                          messageSerializer,
                                  IMessageDeserializer<KVAdminMessage, SerializedMessage>
                                          messageDeserializer,
                                  @EcsNotificationServicePort int port,
                                  DistributedSystemAccessService distributedSystemAccessService)
            throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer,
                messageDeserializer, port);
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
                        distributedSystemAccessService);
                connectionHandler.handleConnection();
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Ecs notification service stopped.");
        }
    }

    @Override
    public void notify(Object notification) {

    }

    private class ConnectionHandler extends AbstractConnectionHandler<KVAdminMessage> {
        private DistributedSystemAccessService distributedSystemAccessService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                          Connection connection,
                          IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer,
                          IMessageDeserializer<KVAdminMessage, SerializedMessage>
                                  messageDeserializer,
                          DistributedSystemAccessService distributedSystemAccessService) {
            super(communicationApi, connection, messageSerializer, messageDeserializer);
            this.logger = Logger.getLogger(this.getClass());
            this.distributedSystemAccessService = distributedSystemAccessService;
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
                    KVAdminMessage receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    logger.debug(CustomStringJoiner.join(" ", "Message received:",
                            receivedMessage.toString()));
                    distributedSystemAccessService
                            .updateServiceRingMetadataWith(receivedMessage.getRingMetadata());
                    connection.kill();
                }
            } catch (Throwable e) {
                logger.error(e);
                closeConnection();
            }
            logger.info("Client is disconnected.");
        }
    }
}
