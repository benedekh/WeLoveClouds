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
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;

import static weloveclouds.commons.status.ServerStatus.RUNNING;


/**
 * Created by Benoit on 2016-12-05.
 */
public class HealthMonitoringService extends AbstractServer<KVAdminMessage> {
    private DistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public HealthMonitoringService(CommunicationApiFactory communicationApiFactory,
                                   ServerSocketFactory serverSocketFactory,
                                   IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer,
                                   IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer,
                                   @HealthMonitoringServicePort int port,
                                   DistributedSystemAccessService distributedSystemAccessService) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer, port);
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.logger = Logger.getLogger(this.getClass());
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
                        messageDeserializer);
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

    private class ConnectionHandler extends AbstractConnectionHandler<KVAdminMessage> {


        ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                          Connection connection,
                          IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer,
                          IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer) {
            super(communicationApi, connection, messageSerializer, messageDeserializer);
            this.logger = Logger.getLogger(this.getClass());
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
                }
            } catch (Throwable e) {
                logger.error(e);
                closeConnection();
            }
            logger.info("Client is disconnected.");
        }
    }
}
