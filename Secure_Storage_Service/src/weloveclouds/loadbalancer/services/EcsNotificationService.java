package weloveclouds.loadbalancer.services;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import weloveclouds.commons.configuration.annotations.EcsDnsName;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.socket.server.IServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.SecureConnection;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationResponsePort;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;

/**
 * Created by Benoit on 2016-12-06.
 */
@Singleton
public class EcsNotificationService extends AbstractServer<IKVEcsNotificationMessage>
        implements IEcsNotificationService {
    private static final Logger LOGGER = Logger.getLogger(EcsNotificationService.class);
    private IDistributedSystemAccessService distributedSystemAccessService;
    private int ecsRemotePort;
    private String ecsDNS;

    @Inject
    public EcsNotificationService(CommunicationApiFactory communicationApiFactory,
            IServerSocketFactory serverSocketFactory,
            IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer,
            IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> messageDeserializer,
            @EcsNotificationServicePort int port, @EcsDnsName String ecsDNS,
            @EcsNotificationResponsePort int ecsRemotePort,
            IDistributedSystemAccessService distributedSystemAccessService) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer,
                port);
        this.ecsRemotePort = ecsRemotePort;
        this.ecsDNS = ecsDNS;
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
                        new SecureConnection.Builder().socket(socket.accept()).build(),
                        messageSerializer, messageDeserializer, distributedSystemAccessService);
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
    public void notify(IKVEcsNotificationMessage kvEcsNotificationMessage) {
        try {
            byte[] receivedMessage;
            ICommunicationApi communicationApi = communicationApiFactory.createCommunicationApiV1();
            communicationApi.connectTo(new ServerConnectionInfo.Builder().ipAddress(ecsDNS)
                    .port(ecsRemotePort).build());
            communicationApi.send(messageSerializer.serialize(kvEcsNotificationMessage).getBytes());
            communicationApi.disconnect();
        } catch (ClientSideException | UnknownHostException e) {
            LOGGER.warn("Unable to notify ECS with cause: " + e.getMessage());
        }
    }

    private class ConnectionHandler extends AbstractConnectionHandler<IKVEcsNotificationMessage> {
        private IDistributedSystemAccessService distributedSystemAccessService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection<?> connection,
                          IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer,
                          IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> messageDeserializer,
                          IDistributedSystemAccessService distributedSystemAccessService) {
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
                    IKVEcsNotificationMessage notification =
                            messageDeserializer.deserialize(message);

                    switch (notification.getStatus()) {
                        case TOPOLOGY_UPDATE:
                            logger.debug("Updating loadbalancer topology");
                            distributedSystemAccessService
                                    .updateServiceTopologyWith(notification.getRingTopology());
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
