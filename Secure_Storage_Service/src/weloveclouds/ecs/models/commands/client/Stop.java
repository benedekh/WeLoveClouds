package weloveclouds.ecs.models.commands.client;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit, Hunton on 2016-11-20.
 */
public class Stop extends AbstractEcsClientCommand {
    public Stop(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.stop();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStopArguments(arguments);
        return this;
    }
}
