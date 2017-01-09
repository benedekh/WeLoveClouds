package weloveclouds.ecs.models.messaging.notification;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2017-01-09.
 */
public interface INotifiable {
    ServerConnectionInfo getNotificationServiceEndpoint();
}
