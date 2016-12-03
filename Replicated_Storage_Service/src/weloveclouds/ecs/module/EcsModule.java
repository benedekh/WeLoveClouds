package weloveclouds.ecs.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.api.v1.KVEcsApiV1;
import weloveclouds.ecs.core.ExternalConfigurationService;
import weloveclouds.ecs.models.commands.client.EcsClientCommandFactory;
import weloveclouds.ecs.models.commands.internal.EcsInternalCommandFactory;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.ssh.SecureShellServiceFactory;
import weloveclouds.ecs.models.tasks.EcsBatchFactory;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.services.TaskService;
import weloveclouds.ecs.utils.ConfigurationFileParser;
import weloveclouds.ecs.utils.IParser;

/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsModule extends AbstractModule{

    @Provides
    public IParser<List<StorageNode>, File> getConfigurationFileParser(){
        return new ConfigurationFileParser();
    }

    @Provides
    public CommunicationApiFactory getCommunicationApiFactory(){
        return new CommunicationApiFactory();
    }

    @Override
    protected void configure() {
        bind(ITaskService.class).to(TaskService.class);
        bind(IKVEcsApi.class).to(KVEcsApiV1.class);
    }
}
