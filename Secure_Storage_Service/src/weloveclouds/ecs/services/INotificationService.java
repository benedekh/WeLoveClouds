package weloveclouds.ecs.services;

import weloveclouds.ecs.models.messaging.notification.INotificationRequest;

/**
 * Created by Benoit on 2017-01-09.
 */
public interface INotificationService<T> {
    void process(INotificationRequest<T> notificationRequest);

    void start();
}
