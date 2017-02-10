package weloveclouds.server.client.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerCLIContext;
import weloveclouds.server.store.storage.KVPersistentStorage;

/**
 * The path for the {@link KVPersistentStorage} where it can persist the records.
 *
 * @author Benedek, Hunton
 */
public class StoragePath extends AbstractServerCommand {

    private static final int STORAGE_PATH_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(StoragePath.class);

    private KVServerCLIContext context;

    /**
     * @param arguments the {@link #STORAGE_PATH_INDEX} element of the array shall contain new
     *        storage path
     * @param context contains the server parameter configuration
     */
    public StoragePath(String[] arguments, KVServerCLIContext context) {
        super(arguments);
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing storagePath command.");
            Path path = Paths.get(arguments[STORAGE_PATH_INDEX]);
            context.setStoragePath(path);

            String statusMessage = StringUtils.join(" ", "Latest storage path:", path);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("storagePath command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStoragePathArguments(arguments);
        return this;
    }

}
