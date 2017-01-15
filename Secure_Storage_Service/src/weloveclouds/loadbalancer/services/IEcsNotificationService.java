package weloveclouds.loadbalancer.services;

import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;

/**
 * Created by Benoit on 2016-12-22.
 */
public interface IEcsNotificationService {
    void requestSystemScale();

    void notifyUnresponsiveServer(String unresponsiveServerName);

    void notify(IKVEcsNotificationMessage kvEcsNotificationMessage);
}
