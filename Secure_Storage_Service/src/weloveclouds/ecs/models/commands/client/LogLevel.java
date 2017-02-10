package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;


/**
 * 
 * @author Benedek, Benoit, Hunton
 *
 */
public class LogLevel extends AbstractEcsClientCommand {

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

            String statusMessage = StringUtils.join(" ", "Latest log level:", logLevel);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            LOGGER.info("logLevel command execution finished.");
        }

    }

    @Override
    public String toString() {
        return StringUtils.join("", "Loglevel command ", arguments.get(LEVEL_INDEX));
    }

}
