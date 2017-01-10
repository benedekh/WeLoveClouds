package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-06.
 */
@Singleton
public class EcsNotificationService extends AbstractServer<IKVEcsNotificationMessage>
        implements IEcsNotificationService {
    private DistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public EcsNotificationService(CommunicationApiFactory communicationApiFactory,
                                  ServerSocketFactory serverSocketFactory,
                                  IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage>
                                          messageSerializer,
                                  IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage>
                                          messageDeserializer,
                                  @EcsNotificationServicePort int port,
                                  DistributedSystemAccessService distributedSystemAccessService) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer,
                port);
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.logger = Logger.getLogger(EcsNotificationService.class);
    }

    @Override
    public void run() {
        status = RUNNING;
        logger.info("ECS notification service started with endpoint: " + serverSocket);
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
    public void requestSystemScale() {

    }

    @Override
    public void notifyUnresponsiveServer(String unresponsiveServerName) {

    }

    @Override
    public void notify(IKVEcsNotificationMessage kvEcsNotificationMessage) {

    }

    private class ConnectionHandler extends AbstractConnectionHandler<IKVEcsNotificationMessage> {
        private DistributedSystemAccessService distributedSystemAccessService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection connection,
                          IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer,
                          IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage>
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
                    byte[] message = communicationApi.receiveFrom(connection);
                    IKVEcsNotificationMessage notification = messageDeserializer
                            .deserialize(message);

                    switch (notification.getStatus()) {
                        case TOPOLOGY_UPDATE:
                            logger.debug("Updating loadbalancer topology");
                            distributedSystemAccessService.updateServiceTopologyWith
                                    (notification.getRingTopology());
                            break;
                    }
                }
            } catch (Throwable e) {
                logger.error(e);
            } finally {
                closeConnection();
            }
            logger.info("Client is disconnected.");
        }
    }
}
