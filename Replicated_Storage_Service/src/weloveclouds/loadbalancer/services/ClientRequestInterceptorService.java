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

import static weloveclouds.commons.status.ServerStatus.*;

/**
 * Created by Benoit on 2016-12-03.
 */
public class ClientRequestInterceptorService extends AbstractServer<KVMessage> {

    @Inject
    public ClientRequestInterceptorService(CommunicationApiFactory communicationApiFactory,
                                           ServerSocketFactory serverSocketFactory,
                                           IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                           IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer,
                                           @ClientRequestsInterceptorPort int port) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer, port);
        logger = Logger.getLogger(ClientRequestInterceptorService.class);
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
            logger.info("Client requests interceptor service stopped.");
        }
    }

    private class ConnectionHandler extends AbstractConnectionHandler<KVMessage> {

        public ConnectionHandler(IConcurrentCommunicationApi communicationApi,
                                 Connection connection,
                                 IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
                                 IMessageDeserializer<KVMessage, SerializedMessage>
                                         messageDeserializer) {
            super(communicationApi, connection, messageSerializer, messageDeserializer);
            logger = Logger.getLogger(this.getClass());
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
                    KVMessage receivedMessage = messageDeserializer
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
