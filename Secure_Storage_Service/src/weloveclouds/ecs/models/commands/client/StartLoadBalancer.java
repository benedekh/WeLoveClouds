package weloveclouds.ecs.models.commands.client;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit on 2017-01-18.
 */
public class StartLoadBalancer extends AbstractEcsClientCommand {
    public StartLoadBalancer(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.startLoadBalancer();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStartLoadBalancerArguments(arguments);
        return this;
    }
}
