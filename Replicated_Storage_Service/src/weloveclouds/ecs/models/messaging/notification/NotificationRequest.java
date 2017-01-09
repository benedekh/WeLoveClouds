package weloveclouds.ecs.models.messaging.notification;

/**
 * Created by Benoit on 2017-01-09.
 */
public class NotificationRequest<T> implements INotificationRequest<T> {
    private T notificationMessage;
    private INotifiable target;

    protected NotificationRequest(Builder<T> builder) {
        this.notificationMessage = builder.notificationMessage;
        this.target = builder.target;
    }

    @Override
    public INotifiable getTarget() {
        return target;
    }

    @Override
    public T getNotificationMessage() {
        return notificationMessage;
    }

    public static class Builder<T> {
        private T notificationMessage;
        private INotifiable target;

        public Builder notificationMessage(T notificationMessage) {
            this.notificationMessage = notificationMessage;
            return this;
        }

        public Builder target(INotifiable target) {
            this.target = target;
            return this;
        }

        public NotificationRequest build() {
            return new NotificationRequest<>(this);
        }
    }
}
