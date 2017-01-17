package weloveclouds.ecs.services;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.SecureConnection;
import weloveclouds.ecs.configuration.annotations.NotificationServiceMaxRetry;
import weloveclouds.ecs.configuration.annotations.NotificationServicePort;
import weloveclouds.ecs.models.commands.internal.EcsInternalCommandFactory;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.INotificationRequest;
import weloveclouds.ecs.models.tasks.SimpleRetryableTask;

/**
 * Created by Benoit on 2016-12-21.
 */
@Singleton
public class NotificationService extends AbstractServer<IKVEcsNotificationMessage>
        implements INotificationService<IKVEcsNotificationMessage> {
    private ITaskService taskService;
    private EcsInternalCommandFactory ecsInternalCommandFactory;
    private int maximumNumberOfNotificationSendRetry;

    @Inject
    public NotificationService(CommunicationApiFactory communicationApiFactory,
            ServerSocketFactory serverSocketFactory,
            IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> kvEcsNotificationMessageSerializer,
            IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> kvEcsNotificationMessageDeserializer,
            ITaskService taskService, EcsInternalCommandFactory ecsInternalCommandFactory,
            @NotificationServicePort int port,
            @NotificationServiceMaxRetry int maximumNumberOfNotificationSendRetry)
            throws IOException {
        super(communicationApiFactory, serverSocketFactory, kvEcsNotificationMessageSerializer,
                kvEcsNotificationMessageDeserializer, port);
        this.taskService = taskService;
        this.ecsInternalCommandFactory = ecsInternalCommandFactory;
        this.maximumNumberOfNotificationSendRetry = maximumNumberOfNotificationSendRetry;
    }

    @Override
    public void process(INotificationRequest<IKVEcsNotificationMessage> notificationRequest) {
        taskService.launchTask(new SimpleRetryableTask(maximumNumberOfNotificationSendRetry,
                ecsInternalCommandFactory.createNotifyTargetCommandWith(notificationRequest,
                        messageSerializer)));
    }

    @Override
    public void run() {
        status = RUNNING;
        logger.info("Ecs notification service started with endpoint: " + serverSocket);
        try (SSLServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                NotificationService.ConnectionHandler connectionHandler =
                        new NotificationService.ConnectionHandler(
                                communicationApiFactory.createConcurrentCommunicationApiV1(),
                                new SecureConnection.Builder().socket((SSLSocket) socket.accept())
                                        .build(),
                                messageSerializer, messageDeserializer);
                connectionHandler.handleConnection();
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Notification service stopped.");
        }
    }

    private class ConnectionHandler extends AbstractConnectionHandler<IKVEcsNotificationMessage> {

        ConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection<?> connection,
                IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer,
                IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> messageDeserializer) {
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
