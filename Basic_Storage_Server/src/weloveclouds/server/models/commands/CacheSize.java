package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.server.models.ServerConfigurationContext;
import weloveclouds.server.models.exceptions.ServerSideException;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * A cacheSize command which sets the size of the cache ({@link KVCache}).
 * 
 * @author Benedek
 */
public class CacheSize extends AbstractServerCommand {

    private static final int CACHE_SIZE_INDEX = 0;

    private ServerConfigurationContext context;
    private Logger logger;

    /**
     * @param arguments the {@link #CACHE_SIZE_INDEX} element of the array shall contain new cache
     *        size
     * @param context contains the server parameter configuration
     */
    public CacheSize(String[] arguments, ServerConfigurationContext context) {
        super(arguments);
        this.context = context;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing cacheSize command.");
            int cacheSize = Integer.parseInt(arguments[CACHE_SIZE_INDEX]);
            context.setCacheSize(cacheSize);

            String statusMessage = join(" ", "Latest cache size:", String.valueOf(cacheSize));
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("cacheSize command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateCacheSizeArguments(arguments);
        return this;
    }

}
