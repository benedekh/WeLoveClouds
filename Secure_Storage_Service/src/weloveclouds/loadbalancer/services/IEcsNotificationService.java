package weloveclouds.loadbalancer.services;

import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;

/**
 * Created by Benoit on 2016-12-22.
 */
public interface IEcsNotificationService {
    void start();

    void requestSystemScale();

    void notify(IKVEcsNotificationMessage kvEcsNotificationMessage);
}
