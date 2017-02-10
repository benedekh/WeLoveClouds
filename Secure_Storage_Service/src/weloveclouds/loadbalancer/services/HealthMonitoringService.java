package weloveclouds.loadbalancer.services;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.socket.server.IServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.SecureConnection;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;

/**
 * Created by Benoit on 2016-12-05.
 */
@Singleton
public class HealthMonitoringService extends AbstractServer<IKVHeartbeatMessage> implements IHealthMonitoringService {
    private IDistributedSystemAccessService distributedSystemAccessService;
    private NodeHealthWatcher nodeHealthWatcher;

    @Inject
    public HealthMonitoringService(CommunicationApiFactory communicationApiFactory,
            IServerSocketFactory serverSocketFactory,
            IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> messageSerializer,
            IMessageDeserializer<IKVHeartbeatMessage, SerializedMessage> messageDeserializer,
            @HealthMonitoringServicePort int port,
            IDistributedSystemAccessService distributedSystemAccessService,
            NodeHealthWatcher nodeHealthWatcher) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer,
                port);
        this.logger = Logger.getLogger(this.getClass());
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.nodeHealthWatcher = nodeHealthWatcher;
    }

    @Override
    public void run() {
        status = RUNNING;
        logger.info("Health monitoring service started with endpoint: " + serverSocket);
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);
            nodeHealthWatcher.start();

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
            logger.info("Health monitoring service stopped.");
        }
    }

    private class ConnectionHandler extends AbstractConnectionHandler<IKVHeartbeatMessage> {
        private IDistributedSystemAccessService distributedSystemAccessService;

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection<?> connection,
                          IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> messageSerializer,
                          IMessageDeserializer<IKVHeartbeatMessage, SerializedMessage> messageDeserializer,
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
                IKVHeartbeatMessage receivedMessage;
                while (connection.isConnected()) {
                    receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    logger.debug(StringUtils.join(" ", "Message received:", receivedMessage));
                    distributedSystemAccessService
                            .updateServiceHealthWith(receivedMessage.getNodeHealthInfos());
                    nodeHealthWatcher.registerHeartbeat(receivedMessage.getNodeHealthInfos());
                }
            } catch (Throwable e) {
                logger.error(e);
            } finally {
                closeConnection();
                logger.info("Client is disconnected.");
            }
        }
    }
}
