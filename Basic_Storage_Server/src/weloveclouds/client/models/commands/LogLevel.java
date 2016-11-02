package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * LogLevel command which means setting the log level to the respective value.
 *
 * @author Benoit, Benedek, Hunton
 */
public class LogLevel extends AbstractCommand {
    private static final int LEVEL_INDEX = 0;

    private Logger logger;

    /**
     * @param arguments the {@link #LEVEL_INDEX} element of the array shall contain new log level
     */
    public LogLevel(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing logLevel command.");
            String logLevel = arguments[LEVEL_INDEX];
            Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

            String statusMessage = CustomStringJoiner.join(" ", "Latest log level:", logLevel);
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            logger.info("logLevel command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }
}
