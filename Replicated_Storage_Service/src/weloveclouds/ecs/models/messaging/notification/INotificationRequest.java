package weloveclouds.ecs.models.messaging.notification;

/**
 * Created by Benoit on 2017-01-09.
 */
public interface INotificationRequest<T> {
    INotifiable getTarget();

    T getNotificationMessage();
}
