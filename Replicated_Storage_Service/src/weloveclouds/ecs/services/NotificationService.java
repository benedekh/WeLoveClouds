package weloveclouds.ecs.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.configuration.annotations.NotificationServicePort;
import weloveclouds.ecs.models.messaging.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.loadbalancer.services.INotifier;

/**
 * Created by Benoit on 2016-12-21.
 */
@Singleton
public class NotificationService extends AbstractServer<IKVEcsNotificationMessage> implements
        INotifier<DistributedService> {

    @Inject
    public NotificationService(CommunicationApiFactory communicationApiFactory,
                               ServerSocketFactory serverSocketFactory,
                               IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage>
                                       messageSerializer,
                               IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage>
                                       messageDeserializer,
                               @NotificationServicePort int port) throws IOException {
        super(communicationApiFactory, serverSocketFactory, messageSerializer, messageDeserializer, port);
    }

    @Override
    public void notify(DistributedService notification) {

    }
}
