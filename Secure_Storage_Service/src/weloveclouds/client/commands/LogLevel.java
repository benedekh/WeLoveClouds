package weloveclouds.client.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.utils.StringUtils;

/**
 * LogLevel command which means setting the log level to the respective value.
 *
 * @author Benoit, Benedek, Hunton
 */
public class LogLevel extends AbstractCommand {

    private static final int LEVEL_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(LogLevel.class);

    /**
     * @param arguments the {@value #LEVEL_INDEX} element of the array shall contain new log level
     */
    public LogLevel(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing logLevel command.");
            String logLevel = arguments[LEVEL_INDEX];
            Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

            String statusMessage = StringUtils.join(" ", "Latest log level:", logLevel);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("logLevel command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }
}
