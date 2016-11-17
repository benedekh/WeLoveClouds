package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import weloveclouds.server.models.ServerConfigurationContext;
import weloveclouds.server.exceptions.ServerSideException;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.utils.ArgumentsValidator;


/**
 * The path for the {@link KVPersistentStorage}} where it can persist the records.
 * 
 * @author Benedek
 */
public class StoragePath extends AbstractServerCommand {

    private static final int STORAGE_PATH_INDEX = 0;

    private ServerConfigurationContext context;
    private Logger logger;

    /**
     * @param arguments the {@link #STORAGE_PATH_INDEX} element of the array shall contain new
     *        storage path
     * @param context contains the server parameter configuration
     */
    public StoragePath(String[] arguments, ServerConfigurationContext context) {
        super(arguments);
        this.context = context;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing storagePath command.");
            Path path = Paths.get(arguments[STORAGE_PATH_INDEX]);
            context.setStoragePath(path);

            String statusMessage = join(" ", "Latest storage path:", path.toString());
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("storagePath command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStoragePathArguments(arguments);
        return this;
    }

}
