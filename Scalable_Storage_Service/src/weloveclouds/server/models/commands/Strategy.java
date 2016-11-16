package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.server.models.ServerCLIConfigurationContext;
import weloveclouds.server.models.exceptions.ServerSideException;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * The displacements strategy to be used in the {@link KVCache}}.
 * 
 * @author Benedek
 */
public class Strategy extends AbstractServerCommand {

    private static final int STRATEGY_INDEX = 0;

    private ServerCLIConfigurationContext context;
    private Logger logger;

    /**
     * @param arguments the {@link #STRATEGY_INDEX} element of the array shall contain the name of
     *        the displacement startegy
     * @param context contains the server parameter configuration
     */
    public Strategy(String[] arguments, ServerCLIConfigurationContext context) {
        super(arguments);
        this.context = context;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing startegy command.");
            String strategyName = arguments[STRATEGY_INDEX];
            DisplacementStrategy strategy =
                    StrategyFactory.createDisplacementStrategy(strategyName);
            context.setDisplacementStrategy(strategy);

            String statusMessage = join(" ", "Latest displacement strategy:", strategyName);
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("Strategy command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStrategyArguments(arguments);
        return this;
    }

}
