package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerCLIContext;
import weloveclouds.server.store.cache.KVCache;

/**
 * A cacheSize command which sets the size of the cache {@link KVCache}.
 * 
 * @author Benedek, Hunton
 */
public class CacheSize extends AbstractServerCommand {

    private static final int CACHE_SIZE_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(CacheSize.class);

    private KVServerCLIContext context;

    /**
     * @param arguments the {@link #CACHE_SIZE_INDEX} element of the array shall contain new cache
     *        size
     * @param context contains the server parameter configuration
     */
    public CacheSize(String[] arguments, KVServerCLIContext context) {
        super(arguments);
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing cacheSize command.");
            int cacheSize = Integer.parseInt(arguments[CACHE_SIZE_INDEX]);
            context.setCacheSize(cacheSize);

            String statusMessage = StringUtils.join(" ", "Latest cache size:", cacheSize);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("cacheSize command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateCacheSizeArguments(arguments);
        return this;
    }

}
