package weloveclouds.ecs.services;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import weloveclouds.commons.cli.utils.UserOutputWriter;
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
import weloveclouds.ecs.api.IKVEcsApi;
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
    private static final int DEFAULT_CACHE_SIZE = 200;
    private static final String DEEFAULT_CACHE_DISPLACEMENT_STRATEGY = "LFU";
    private IKVEcsApi ecsCoreApi;
    private ITaskService taskService;
    private EcsInternalCommandFactory ecsInternalCommandFactory;
    private int maximumNumberOfNotificationSendRetry;

    @Inject
    public NotificationService(CommunicationApiFactory communicationApiFactory,
            IServerSocketFactory serverSocketFactory,
            IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> kvEcsNotificationMessageSerializer,
            IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> kvEcsNotificationMessageDeserializer,
            ITaskService taskService, EcsInternalCommandFactory ecsInternalCommandFactory,
            IKVEcsApi ecsCoreApi, @NotificationServicePort int port,
            @NotificationServiceMaxRetry int maximumNumberOfNotificationSendRetry)
            throws IOException {
        super(communicationApiFactory, serverSocketFactory, kvEcsNotificationMessageSerializer,
                kvEcsNotificationMessageDeserializer, port);
        this.logger = Logger.getLogger(NotificationService.class);
        this.taskService = taskService;
        this.ecsInternalCommandFactory = ecsInternalCommandFactory;
        this.ecsCoreApi = ecsCoreApi;
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
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                NotificationService.ConnectionHandler connectionHandler =
                        new NotificationService.ConnectionHandler(
                                communicationApiFactory.createConcurrentCommunicationApiV1(),
                                new SecureConnection.Builder().socket(socket.accept()).build(),
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
            // logger.info("Client is connected to server.");
            logger.info("Received message from load balancer");
            try {
                while (connection.isConnected()) {
                    IKVEcsNotificationMessage receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    switch (receivedMessage.getStatus()) {
                        case UNRESPONSIVE_NODES_REPORTING:
                            for (String unresponsiveNodeName : receivedMessage.getUnresponsiveNodeNames()) {
                                UserOutputWriter.getInstance().appendToLine
                                        (StringUtils.join(" ", "Notification service message:",
                                                unresponsiveNodeName, "has failed. " +
                                                        "Trying to recover."));
                                ecsCoreApi.removeUnresponsiveNodesWithName(unresponsiveNodeName);
                            }
                            break;
                        case SCALE_REQUEST:
                            ecsCoreApi.addNode(DEFAULT_CACHE_SIZE,
                                    DEEFAULT_CACHE_DISPLACEMENT_STRATEGY, true);

                            break;
                        default:
                            //
                            break;
                    }
                    logger.info("Received message from load balancer");
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
