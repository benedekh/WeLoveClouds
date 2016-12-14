package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.AbstractCommand;

/**
 * Created by Benoit on 2016-11-21.
 */
public abstract class AbstractEcsClientCommand extends AbstractCommand<String> implements weloveclouds.ecs.models.commands.IValidatable {
    protected IKVEcsApi externalCommunicationServiceApi;
    
    public AbstractEcsClientCommand(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        this.externalCommunicationServiceApi = externalCommunicationServiceApi;
        this.addArguments(arguments);
    }
}
