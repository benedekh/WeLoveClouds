package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.StringJoiner;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class LogLevel extends AbstractCommand {
    private static final int LEVEL_INDEX = 0;

    private Logger logger;

    public LogLevel(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            String logLevel = arguments[LEVEL_INDEX];
            logger.info("Executing setLevel command.");
            Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

            String statusMessage = StringJoiner.join(" ", "Latest log level:", logLevel);
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            logger.info("setLevel command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }
}
