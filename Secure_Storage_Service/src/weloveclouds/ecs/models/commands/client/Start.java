package weloveclouds.ecs.models.commands.client;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit on 2016-11-20.
 */
public class Start extends AbstractEcsClientCommand {
    public Start(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.start();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStartArguments(arguments);
        return this;
    }
}
