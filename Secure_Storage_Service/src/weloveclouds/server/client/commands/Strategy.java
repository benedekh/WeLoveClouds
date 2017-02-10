package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerCLIContext;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

/**
 * The displacements strategy to be used in the {@link KVCache}.
 *
 * @author Benedek, Hunton
 */
public class Strategy extends AbstractServerCommand {

    private static final int STRATEGY_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(Strategy.class);

    private KVServerCLIContext context;

    /**
     * @param arguments the {@value #STRATEGY_INDEX} element of the array shall contain the name of
     *        the displacement strategy
     * @param context contains the server parameter configuration
     */
    public Strategy(String[] arguments, KVServerCLIContext context) {
        super(arguments);
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing startegy command.");
            String strategyName = arguments[STRATEGY_INDEX];
            DisplacementStrategy strategy =
                    StrategyFactory.createDisplacementStrategy(strategyName);
            context.setDisplacementStrategy(strategy);

            String statusMessage =
                    StringUtils.join(" ", "Latest displacement strategy:", strategyName);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("Strategy command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStrategyArguments(arguments);
        return this;
    }

}
