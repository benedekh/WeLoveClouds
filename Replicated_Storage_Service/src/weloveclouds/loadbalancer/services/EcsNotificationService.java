package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;
import weloveclouds.loadbalancer.models.EcsNotification;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-06.
 */
@Singleton
public class EcsNotificationService extends AbstractServer<IKVAdminMessage>
        implements INotifier<EcsNotification> {
    private DistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public EcsNotificationService(CommunicationApiFactory communicationApiFactory,
                                  ServerSocketFactory serverSocketFactory,
                                  IMessageSerializer<SerializedMessage, IKVAdminMessage>
                                          messageSerializer,
                                  IMessageDeserializer<IKVAdminMessage, SerializedMessage>
                                          messageDeserializer,
                                  @EcsNotificationServicePort int port,
                                  DistributedSystemAccessService distributedSystemAccessService) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer,
                port);
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                ConnectionHandler connectionHandler = new ConnectionHandler(
                        communicationApiFactory.createConcurrentCommunicationApiV1(),
                        new Connection.Builder().socket(socket.accept()).build(), messageSerializer,
                        messageDeserializer, distributedSystemAccessService);
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
    public void notify(EcsNotification notification) {

    }

    private class ConnectionHandler extends AbstractConnectionHandler<IKVAdminMessage> {
        private DistributedSystemAccessService distributedSystemAccessService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection connection,
                          IMessageSerializer<SerializedMessage, IKVAdminMessage> messageSerializer,
                          IMessageDeserializer<IKVAdminMessage, SerializedMessage>
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
                    IKVAdminMessage receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    logger.debug(StringUtils.join(" ", "Message received:", receivedMessage));
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
