package weloveclouds.ecs.models.commands.internal;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.INotifiable;
import weloveclouds.ecs.models.messaging.notification.INotificationRequest;

/**
 * Created by Benoit on 2017-01-09.
 */
public class NotifyTargetCommand extends AbstractEcsNetworkCommand<INotifiable, IKVEcsNotificationMessage> {
    private static final Logger LOGGER = Logger.getLogger(NotifyTargetCommand.class);
    private INotificationRequest<IKVEcsNotificationMessage> notificationRequest;

    protected NotifyTargetCommand(Builder builder) {
        this.communicationApi = builder.communicationApi;
        this.targetedNode = builder.notificationRequest.getTarget();
        this.messageSerializer = builder.messageSerializer;
        this.notificationRequest = builder.notificationRequest;
        this.errorMessage =
                StringUtils.join(" ", "Unable to send notification to target:", targetedNode);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getNotificationServiceEndpoint());
            communicationApi.send(messageSerializer
                    .serialize(notificationRequest.getNotificationMessage()).getBytes());
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        } catch (ClientSideException e) {
            LOGGER.warn(errorMessage + "with cause: " + e.getMessage());
        } finally {
            communicationApi.disconnect();
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "Command: SetWriteLock", "Targeted node:", targetedNode);
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private INotificationRequest<IKVEcsNotificationMessage> notificationRequest;
        private IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer;

        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder messageSerializer(
                IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public Builder notificationRequest(INotificationRequest<IKVEcsNotificationMessage> notificationRequest) {
            this.notificationRequest = notificationRequest;
            return this;
        }

        public NotifyTargetCommand build() {
            return new NotifyTargetCommand(this);
        }
    }
}
