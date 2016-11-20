package weloveclouds.ecs.models.commands.client;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.AbstractCommand;

/**
 * Created by Benoit on 2016-11-21.
 */
public abstract class AbstractEcsClientCommand extends AbstractCommand<String> {
    protected IKVEcsApi externalCommunicationServiceApi;

    public AbstractEcsClientCommand(IKVEcsApi externalCommunicationServiceApi) {
        this.externalCommunicationServiceApi = externalCommunicationServiceApi;
    }
}
