package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import org.apache.log4j.Level;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.LogManager;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class LogLevel extends AbstractCommand {
    private static final int LEVEL_INDEX = 0;

    public LogLevel(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        LogManager.getInstance().setLogLevel(Level.toLevel(arguments[LEVEL_INDEX]));
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }
}
