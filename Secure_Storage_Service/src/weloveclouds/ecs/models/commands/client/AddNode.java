package weloveclouds.ecs.models.commands.client;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit, Hunton on 2016-11-20.
 */
public class AddNode extends AbstractEcsClientCommand {
    public static final int CACHE_SIZE_ARG_INDEX = 0;
    public static final int DISPlACEMENT_STRATEGY_ARG_INDEX = 1;

    public AddNode(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    private int getCacheSize() {
        return Integer.parseInt(arguments.get(CACHE_SIZE_ARG_INDEX));
    }

    private String getDisplacementStrategy() {
        return arguments.get(DISPlACEMENT_STRATEGY_ARG_INDEX);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.addNode(getCacheSize(), getDisplacementStrategy(), false);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateAddNodeArguments(arguments);
        return this;
    }
}
