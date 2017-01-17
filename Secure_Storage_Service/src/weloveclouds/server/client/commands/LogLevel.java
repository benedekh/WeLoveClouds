package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;

/**
 * LogLevel command which means setting the log level to the respective value.
 *
 * @author Benoit, Benedek, Hunton
 */
public class LogLevel extends AbstractServerCommand {

    private static final int LEVEL_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(Help.class);

    /**
     * @param arguments the {@link #LEVEL_INDEX} element of the array shall contain new log level
     */
    public LogLevel(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing logLevel command.");
            String logLevel = arguments[LEVEL_INDEX];
            Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

            String statusMessage = StringUtils.join(" ", "Latest log level:", logLevel);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("logLevel command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }

}
