package weloveclouds.ecs.models.commands.client;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.ecs.utils.ArgumentsValidator;


/**
 * 
 * @author Benedek, Benoit, hb
 *
 */
public class LogLevel extends AbstractEcsClientCommand{
    
    private static final int LEVEL_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(LogLevel.class);

    public LogLevel(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateLogLevelArguments(arguments);
        return this;
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing logLevel command.");
            String logLevel = arguments.get(LEVEL_INDEX);
            Logger.getRootLogger().setLevel(Level.toLevel(logLevel));

            String statusMessage = CustomStringJoiner.join(" ", "Latest log level:", logLevel);
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
    public String toString() {
        //no point
        return null;
    }

}
