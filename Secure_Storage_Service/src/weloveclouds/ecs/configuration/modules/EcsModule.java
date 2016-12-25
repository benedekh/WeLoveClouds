package weloveclouds.ecs.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import java.io.File;
import java.util.List;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.api.v1.KVEcsApiV1;
import weloveclouds.ecs.models.messaging.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.services.NotificationService;
import weloveclouds.ecs.services.TaskService;
import weloveclouds.ecs.utils.ConfigurationFileParser;
import weloveclouds.ecs.utils.IParser;
import weloveclouds.loadbalancer.services.INotifier;

/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsModule extends AbstractModule {

    @Provides
    public IParser<List<StorageNode>, File> getConfigurationFileParser() {
        return new ConfigurationFileParser();
    }

    @Provides
    public CommunicationApiFactory getCommunicationApiFactory() {
        return new CommunicationApiFactory();
    }

    @Override
    protected void configure() {
        bind(ITaskService.class).to(TaskService.class);
        bind(IKVEcsApi.class).to(KVEcsApiV1.class);
        bind(new TypeLiteral<INotifier<DistributedService>>() {
        })
                .to(new TypeLiteral<NotificationService>() {
                });
        install(new NotificationServiceModule());
    }
}
