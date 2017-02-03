package weloveclouds.ecs.models.commands.client;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit, Hunton on 2016-11-20.
 */
public class InitService extends AbstractEcsClientCommand {
    public static final int NUMBER_OF_NODES_ARG_INDEX = 0;
    public static final int CACHE_SIZE_ARG_INDEX = 1;
    public static final int DISPLACEMENT_STRATEGY_ARG_INDEX = 2;

    public InitService(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    private int getCacheSize() {
        return Integer.parseInt(arguments.get(CACHE_SIZE_ARG_INDEX));
    }

    private int getNumberOfNodes() {
        return Integer.parseInt(arguments.get(NUMBER_OF_NODES_ARG_INDEX));
    }

    private String getDisplacementStrategy() {
        return arguments.get(DISPLACEMENT_STRATEGY_ARG_INDEX);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.initService(getNumberOfNodes(), getCacheSize(), getDisplacementStrategy());
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateInitServiceArguments(arguments);
        return this;
    }
}
